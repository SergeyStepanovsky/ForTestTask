package com.example.webzaim.repository;

import com.example.webzaim.model.RegPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegPersonRepository extends JpaRepository<RegPerson, Long> {

}