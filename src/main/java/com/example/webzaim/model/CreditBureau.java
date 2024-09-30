package com.example.webzaim.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditBureau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь с AccountInfo
    @OneToMany(mappedBy = "creditBureau", cascade = CascadeType.ALL)
    private List<AccountInfo> accountInfo;

    // Связь с VerifiedName
    @OneToOne(mappedBy = "creditBureau", cascade = CascadeType.ALL)
    private VerifiedName verifiedName;

    // Внешний ключ на LoanRequest
    @ManyToOne
    @JoinColumn(name = "loan_request_id")
    private LoanRequest loanRequest;
}

