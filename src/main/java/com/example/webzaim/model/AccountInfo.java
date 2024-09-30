package com.example.webzaim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private String accountStatus;
    private BigDecimal currentBalance;
    private LocalDate dateOpened;
    private int daysInArrears;
    private String delinquencyCode;
    private int highestDaysInArrears;

    @Column(name = "is_your_account")
    @JsonProperty("is_your_account")  // Преобразование для JSON
    private boolean isYourAccount;

    private BigDecimal lastPaymentAmount;
    private LocalDate lastPaymentDate;
    private LocalDate loadedAt;
    private BigDecimal originalAmount;
    private BigDecimal overdueBalance;
    private LocalDate overdueDate;
    private int productTypeId;

    // Внешний ключ на CreditBureau
    @ManyToOne
    @JoinColumn(name = "credit_bureau_id")
    private CreditBureau creditBureau;
}

