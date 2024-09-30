CREATE TABLE request_content (
    id BIGSERIAL PRIMARY KEY,
    content JSON NOT NULL,
    loan_request_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_loan_request FOREIGN KEY (loan_request_id) REFERENCES loan_request(loan_request_id) ON DELETE CASCADE
);
