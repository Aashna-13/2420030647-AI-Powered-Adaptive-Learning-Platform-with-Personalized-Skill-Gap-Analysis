package com.skillbridge.repository;

import com.skillbridge.model.Assessment;
import com.skillbridge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByLearnerNameIgnoreCaseOrderByCreatedAtDesc(String learnerName);
    List<Assessment> findByUserOrderByCreatedAtDesc(User user);
    List<Assessment> findByUserOrLearnerNameIgnoreCaseOrderByCreatedAtDesc(User user, String learnerName);
    List<Assessment> findAllByOrderByCreatedAtDesc();
}

