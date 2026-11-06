package com.skillbridge.repository;

import com.skillbridge.model.CareerQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerQuestionRepository extends JpaRepository<CareerQuestion, Long> {
    List<CareerQuestion> findByCareerIdOrderByIdAsc(String careerId);
    List<CareerQuestion> findByCareerIdAndSkillCategoryOrderByIdAsc(String careerId, String skillCategory);
    long countByCareerId(String careerId);
}
