-- Создаем таблицу LoanRequest (корневая сущность)
CREATE TABLE loan_request (
    loan_request_id VARCHAR(255) PRIMARY KEY
);

-- Создаем таблицу RegPerson (ссылается на LoanRequest)
CREATE TABLE reg_person (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    middle_name VARCHAR(255),
    last_name VARCHAR(255),
    loan_request_id VARCHAR(255),
    CONSTRAINT fk_loan_request_reg_person
      FOREIGN KEY (loan_request_id) REFERENCES loan_request(loan_request_id) ON DELETE CASCADE
);

-- Создаем таблицу CreditBureau (ссылается на LoanRequest)
CREATE TABLE credit_bureau (
    id BIGSERIAL PRIMARY KEY,
    loan_request_id VARCHAR(255),
    CONSTRAINT fk_loan_request_credit_bureau
      FOREIGN KEY (loan_request_id) REFERENCES loan_request(loan_request_id) ON DELETE CASCADE
);

-- Создаем таблицу AccountInfo (ссылается на CreditBureau)
CREATE TABLE account_info (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(255),
    account_status VARCHAR(50),
    current_balance DECIMAL(12, 5),
    date_opened DATE,
    days_in_arrears INT,
    delinquency_code VARCHAR(10),
    highest_days_in_arrears INT,
    is_your_account BOOLEAN,
    last_payment_amount DECIMAL(12, 5),
    last_payment_date DATE,
    loaded_at DATE,
    original_amount DECIMAL(12, 5),
    overdue_balance DECIMAL(12, 5),
    overdue_date DATE,
    product_type_id INT,
    credit_bureau_id BIGINT,
    CONSTRAINT fk_credit_bureau
      FOREIGN KEY (credit_bureau_id) REFERENCES credit_bureau(id) ON DELETE CASCADE
);

-- Создаем таблицу VerifiedName (ссылается на CreditBureau)
CREATE TABLE verified_name (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    other_name VARCHAR(255),
    surname VARCHAR(255),
    credit_bureau_id BIGINT,
    CONSTRAINT fk_credit_bureau_verified_name
      FOREIGN KEY (credit_bureau_id) REFERENCES credit_bureau(id) ON DELETE CASCADE
);
