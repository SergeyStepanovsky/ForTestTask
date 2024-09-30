# Название проекта
WEB-ZAIM
## Как запустить приложение

1. Клонируйте репозиторий
2. Перейдите в папку проекта
3. Запустите проект с помощью Docker Compose: `docker-compose up --build`
4. Для просмотра содержимого БД можно использовать следующие запросы:

## SQL Queries

```sql
SELECT * FROM loan_request;
SELECT * FROM account_info;
SELECT * FROM credit_bureau;
SELECT * FROM verified_name;

SELECT 
    lr.loan_request_id,
    rp.first_name AS regperson_first_name, 
    rp.middle_name AS regperson_middle_name, 
    rp.last_name AS regperson_last_name,
    vn.first_name AS verifiedname_first_name, 
    vn.other_name AS verifiedname_other_name, 
    vn.surname AS verifiedname_surname,
    cb.id AS credit_bureau_id,
    CASE 
        WHEN levenshtein(concat(rp.first_name, rp.middle_name, rp.last_name), concat(vn.first_name, vn.other_name, vn.surname)) < 5
        THEN 'TRUE'
        ELSE 'FALSE'
    END AS stop_factor_result
FROM 
    loan_request lr
JOIN reg_person rp ON lr.loan_request_id = rp.loan_request_id
JOIN credit_bureau cb ON lr.loan_request_id = cb.loan_request_id
LEFT JOIN verified_name vn ON cb.id = vn.credit_bureau_id;
