package com.example.webzaim.service;

import com.example.webzaim.model.*;
import com.example.webzaim.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(DataProcessingService.class);

    @Autowired
    private RequestContentRepository requestContentRepository;

    @Autowired
    private RegPersonRepository regPersonRepository;

    @Autowired
    private LoanRequestRepository loanRequestRepository;

    @Autowired
    private CreditBureauRepository creditBureauRepository;

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private VerifiedNameRepository verifiedNameRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Переменная для хранения значения порога из настроек
    private double distanceRatioThreshold;

    @PostConstruct
    public void init() {
        logger.info("Инициализация DataProcessingService...");

        objectMapper.registerModule(new JavaTimeModule());

        // Читаем значение distanceRatioThreshold из таблицы settings
        Setting setting = settingsRepository.findByKey("distanceRatioThreshold");
        if (setting != null) {
            try {
                distanceRatioThreshold = Double.parseDouble(setting.getValue());
                logger.info("Загружено значение distanceRatioThreshold из настроек: {}", distanceRatioThreshold);
            } catch (NumberFormatException e) {
                logger.error("Некорректный формат distanceRatioThreshold в настройках. Используется значение по умолчанию 0.9", e);
                distanceRatioThreshold = 0.9; // Значение по умолчанию
            }
        } else {
            logger.warn("Настройка distanceRatioThreshold не найдена. Используется значение по умолчанию 0.9");
            distanceRatioThreshold = 0.9;
        }

        processRequestContents(); // Запускаем обработку данных
    }

    public void processRequestContents() {
        logger.info("Начало обработки записей request_content");

        List<RequestContent> contents = requestContentRepository.findAll();

        for (RequestContent content : contents) {
            try {
                logger.debug("Обработка записи с id: {}", content.getId());
                JsonNode rootNode = objectMapper.readTree(content.getContent());

                // 1. Сохранение LoanRequest
                String loanRequestID = rootNode.get("loanRequestID").asText();
                LoanRequest loanRequest = new LoanRequest();
                loanRequest.setLoanRequestID(loanRequestID);
                loanRequestRepository.save(loanRequest);
                logger.info("Сохранен loanRequest с ID: {}", loanRequestID);

                // 2. Сохранение RegPerson
                JsonNode regPersonNode = rootNode.get("regPerson");
                RegPerson regPerson = objectMapper.treeToValue(regPersonNode, RegPerson.class);
                regPerson.setLoanRequest(loanRequest);
                regPersonRepository.save(regPerson);

                // 3. Сохранение CreditBureau
                JsonNode creditBureauNode = rootNode.get("creditBureau");
                CreditBureau creditBureau = objectMapper.treeToValue(creditBureauNode, CreditBureau.class);
                creditBureau.setLoanRequest(loanRequest);
                creditBureauRepository.save(creditBureau);

                // 4. Сохранение AccountInfo
                JsonNode accountInfoArray = creditBureauNode.get("account_info");
                if (accountInfoArray != null && accountInfoArray.isArray()) {
                    for (JsonNode accountInfoNode : accountInfoArray) {
                        AccountInfo accountInfo = objectMapper.treeToValue(accountInfoNode, AccountInfo.class);
                        accountInfo.setCreditBureau(creditBureau);
                        accountInfoRepository.save(accountInfo);
                    }
                }

                // 5. Сохранение VerifiedName
                VerifiedName verifiedName = null;
                JsonNode verifiedNameNode = creditBureauNode.get("verified_name");
                if (verifiedNameNode != null) {
                    verifiedName = objectMapper.treeToValue(verifiedNameNode, VerifiedName.class);

                    // Проверяем, существует ли запись с таким же firstName, otherName и surname
                    Optional<VerifiedName> existingVerifiedName = verifiedNameRepository.findByFirstNameAndOtherNameAndSurname(
                            verifiedName.getFirstName(),
                            verifiedName.getOtherName(),
                            verifiedName.getSurname());

                    if (existingVerifiedName.isPresent()) {
                        VerifiedName existing = existingVerifiedName.get();
                        // Проверяем, установлена ли связь с CreditBureau
                        if (existing.getCreditBureau() == null) {
                            existing.setCreditBureau(creditBureau);
                            verifiedNameRepository.save(existing);
                            logger.info("Обновлен verifiedName: {} {} {} - добавлена связь с CreditBureau",
                                    existing.getFirstName(),
                                    existing.getOtherName(),
                                    existing.getSurname());
                        } else {
                            logger.info("verifiedName уже существует и связан с CreditBureau: {} {} {}",
                                    existing.getFirstName(),
                                    existing.getOtherName(),
                                    existing.getSurname());
                        }
                    } else {
                        // Устанавливаем связь с CreditBureau
                        verifiedName.setCreditBureau(creditBureau);
                        verifiedNameRepository.save(verifiedName);
                        logger.info("Сохранен verifiedName: {} {} {}",
                                verifiedName.getFirstName(),
                                verifiedName.getOtherName(),
                                verifiedName.getSurname());
                    }
                }

            } catch (Exception e) {
                logger.error("Ошибка при обработке записи с id: {}", content.getId(), e);
            }
        }
    }

    public boolean calculateStopFactor(RegPerson regPerson, VerifiedName verifiedName) {
        // Извлекаем и нормализуем слова
        List<String> regPersonWords = normalizeWords(extractWordsFromRegPerson(regPerson));
        List<String> verifiedNameWords = normalizeWords(extractWordsFromVerifiedName(verifiedName));

        LevenshteinDistance levenshtein = new LevenshteinDistance();
        double totalDistance = 0;
        int count = 0;

        // Сравниваем каждое слово из regPerson с каждым словом из verifiedName
        for (String regWord : regPersonWords) {
            for (String verWord : verifiedNameWords) {
                int distance = levenshtein.apply(regWord, verWord);
                totalDistance += (double) distance / Math.max(regWord.length(), verWord.length());
                count++;
            }
        }

        double averageNormalizedDistance = totalDistance / count;

        logger.debug("Среднее нормализованное расстояние Левенштейна: {}", averageNormalizedDistance);

        // Сравниваем с пороговым значением
        boolean stopFactor = averageNormalizedDistance < distanceRatioThreshold;

        // Логируем результат
        if (stopFactor) {
            logger.info("Стоп-Фактор активирован для RegPerson ID: {} (среднее нормализованное расстояние: {})", regPerson.getId(), averageNormalizedDistance);
        } else {
            logger.info("Стоп-Фактор не активирован для RegPerson ID: {} (среднее нормализованное расстояние: {})", regPerson.getId(), averageNormalizedDistance);
        }

        return stopFactor;
    }





     // Извлекаем слова из объекта RegPerson.

    public List<String> extractWordsFromRegPerson(RegPerson regPerson) {
        List<String> words = new ArrayList<>();
        if (regPerson.getFirstName() != null && !regPerson.getFirstName().isEmpty())
            words.add(regPerson.getFirstName());
        if (regPerson.getMiddleName() != null && !regPerson.getMiddleName().isEmpty())
            words.add(regPerson.getMiddleName());
        if (regPerson.getLastName() != null && !regPerson.getLastName().isEmpty())
            words.add(regPerson.getLastName());
        return words;
    }


    // Извлекаем слова из объекта VerifiedName.

    public List<String> extractWordsFromVerifiedName(VerifiedName verifiedName) {
        List<String> words = new ArrayList<>();
        if (verifiedName.getFirstName() != null && !verifiedName.getFirstName().isEmpty())
            words.add(verifiedName.getFirstName());
        if (verifiedName.getOtherName() != null && !verifiedName.getOtherName().isEmpty())
            words.add(verifiedName.getOtherName());
        if (verifiedName.getSurname() != null && !verifiedName.getSurname().isEmpty())
            words.add(verifiedName.getSurname());
        return words;
    }


     // Генерируем все возможные сочетания по два слова и объединяет их в строки.

    public List<String> generateWordPairs(List<String> words) {
        List<String> pairs = new ArrayList<>();
        int n = words.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                pairs.add(words.get(i) + words.get(j));
            }
        }
        return pairs;
    }

    public void setDistanceRatioThreshold(double distanceRatioThreshold) {
        this.distanceRatioThreshold = distanceRatioThreshold;
    }


     //  переводим в нижний регистр и удаляет пробелы.

    public List<String> normalizeWords(List<String> words) {
        return words.stream()
                .map(s -> s.toLowerCase().replaceAll("\\s+", ""))
                .collect(Collectors.toList());
    }


}
