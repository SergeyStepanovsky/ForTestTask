package com.example.webzaim.service;

import com.example.webzaim.model.RegPerson;
import com.example.webzaim.model.VerifiedName;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataProcessingServiceTest {

    @InjectMocks
    private DataProcessingService dataProcessingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dataProcessingService.setDistanceRatioThreshold(0.9); // Устанавливаем значение вручную
    }


    @Test
    void testCalculateStopFactor_True() {
        // Данные для RegPerson
        RegPerson regPerson = new RegPerson();
        regPerson.setFirstName("John");
        regPerson.setMiddleName("Michael");
        regPerson.setLastName("Doe");

        // Данные для VerifiedName
        VerifiedName verifiedName = new VerifiedName();
        verifiedName.setFirstName("John");
        verifiedName.setOtherName("Michael");
        verifiedName.setSurname("Doe");

        // Устанавливаем пороговое значение distanceRatioThreshold
        dataProcessingService.setDistanceRatioThreshold(0.9);

        // Вызываем метод расчета Стоп-Фактора
        boolean stopFactor = dataProcessingService.calculateStopFactor(regPerson, verifiedName);

        // Ожидаем, что Стоп-Фактор сработает, так как имена совпадают
        assertTrue(stopFactor, "Стоп-Фактор должен быть активирован, так как имена совпадают");
    }


    @Test
    void testCalculateStopFactor_False() {
        // Данные для RegPerson
        RegPerson regPerson = new RegPerson();
        regPerson.setFirstName("Jane");
        regPerson.setMiddleName("Mary");
        regPerson.setLastName("Smith");

        // Данные для VerifiedName
        VerifiedName verifiedName = new VerifiedName();
        verifiedName.setFirstName("John");
        verifiedName.setOtherName("Michael");
        verifiedName.setSurname("Doe");

        // Устанавливаем пороговое значение
        dataProcessingService.setDistanceRatioThreshold(0.1);

        // Вызываем метод расчета Стоп-Фактора
        boolean stopFactor = dataProcessingService.calculateStopFactor(regPerson, verifiedName);

        // Ожидаем, что Стоп-Фактор не сработает, так как имена разные
        assertFalse(stopFactor, "Стоп-Фактор не должен быть активирован, так как имена разные");
    }



    // Тест на алгоритм расчета Левенштейна
    @Test
    void testCalculateLevenshteinDistance() {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

        String string1 = "JohnDoe";
        String string2 = "JohnSmith";

        int distance = levenshteinDistance.apply(string1, string2);

        // Ожидаем, что расстояние Левенштейна будет 4 (Doe -> Smith)
        assertEquals(5, distance, "Расстояние Левенштейна должно быть равно 5");
    }

    // Тест на алгоритм генерации сочетаний
    @Test
    void testGenerateWordPairs() {
        List<String> words = List.of("A", "B", "C");

        // Ожидаемый результат
        List<String> expectedPairs = List.of("AB", "AC", "BC");

        // Вызываем метод генерации пар слов
        List<String> actualPairs = dataProcessingService.generateWordPairs(words);

        // Проверяем, что результат соответствует ожиданиям
        assertEquals(expectedPairs, actualPairs, "Пары слов должны соответствовать ожидаемым");
    }

    // Дополнительный тест на генерацию пар для большего количества слов
    @Test
    void testGenerateWordPairs_MoreWords() {
        List<String> words = List.of("A", "B", "C", "D");

        // Ожидаемый результат
        List<String> expectedPairs = List.of("AB", "AC", "AD", "BC", "BD", "CD");

        // Вызываем метод генерации пар слов
        List<String> actualPairs = dataProcessingService.generateWordPairs(words);

        // Проверяем, что результат соответствует ожиданиям
        assertEquals(expectedPairs, actualPairs, "Пары слов должны соответствовать ожидаемым");
    }
}