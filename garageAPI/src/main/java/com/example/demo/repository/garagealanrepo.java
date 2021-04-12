package com.example.demo.repository;

import com.example.demo.model.garage;
import com.example.demo.model.garagealan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface garagealanrepo extends JpaRepository<garagealan, Integer> {
    Optional<garagealan> findByAlan(int alan);
}
