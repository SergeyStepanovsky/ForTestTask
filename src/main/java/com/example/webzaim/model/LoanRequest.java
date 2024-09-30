package com.example.webzaim.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {

    @Id
    @Column(name = "loan_request_id")
    private String loanRequestID;  // Это будет основным ключом
}