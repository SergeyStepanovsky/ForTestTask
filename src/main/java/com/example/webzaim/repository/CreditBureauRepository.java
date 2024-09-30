package com.example.webzaim.repository;

import com.example.webzaim.model.CreditBureau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditBureauRepository extends JpaRepository<CreditBureau, Long> {

}