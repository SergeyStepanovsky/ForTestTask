package com.example.webzaim.repository;

import com.example.webzaim.model.VerifiedName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerifiedNameRepository extends JpaRepository<VerifiedName, Long> {
    Optional<VerifiedName> findByFirstNameAndOtherNameAndSurname(String firstName, String otherName, String surname);
}
