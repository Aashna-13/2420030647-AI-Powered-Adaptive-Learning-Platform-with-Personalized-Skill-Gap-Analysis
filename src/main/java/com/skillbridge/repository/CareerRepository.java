package com.skillbridge.repository;

import com.skillbridge.model.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CareerRepository extends JpaRepository<Career, String> {
    Optional<Career> findByIdIgnoreCase(String id);
}
