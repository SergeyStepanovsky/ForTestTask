package com.example.webzaim.repository;

import com.example.webzaim.model.RequestContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestContentRepository extends JpaRepository<RequestContent, Long> {

}
