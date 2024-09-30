package com.example.webzaim.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifiedName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String otherName;
    private String surname;

    // Связь с CreditBureau
    @OneToOne
    @JoinColumn(name = "credit_bureau_id")
    private CreditBureau creditBureau;
}


