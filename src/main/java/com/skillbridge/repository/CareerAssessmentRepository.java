package com.skillbridge.repository;

import com.skillbridge.model.CareerAssessment;
import com.skillbridge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerAssessmentRepository extends JpaRepository<CareerAssessment, Long> {
    List<CareerAssessment> findByUserOrderByCreatedAtDesc(User user);
    List<CareerAssessment> findByStudentEmailIgnoreCaseOrderByCreatedAtDesc(String studentEmail);
    List<CareerAssessment> findAllByOrderByCreatedAtDesc();
}
