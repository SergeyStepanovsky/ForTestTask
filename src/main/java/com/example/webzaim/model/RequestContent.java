package com.example.webzaim.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_request_id", nullable = false) // Внешний ключ на LoanRequest
    private LoanRequest loanRequest;

    @Column(columnDefinition = "json")
    private String content;  // Поле для хранения исходного JSON
}