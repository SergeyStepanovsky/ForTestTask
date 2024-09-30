package com.example.webzaim.repository;

import com.example.webzaim.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Setting, Long> {
    Setting findByKey(String key);
}
