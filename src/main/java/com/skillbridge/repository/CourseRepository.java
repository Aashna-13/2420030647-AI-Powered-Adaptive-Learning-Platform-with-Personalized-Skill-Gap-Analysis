package com.skillbridge.repository;

import com.skillbridge.model.Course;
import com.skillbridge.model.QuestionDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findBySlugIgnoreCase(String slug);

    List<Course> findByPublishedTrueOrderByTitleAsc();

    List<Course> findByCareerIdIgnoreCaseAndPublishedTrue(String careerId);

    List<Course> findBySkillCategoryIgnoreCaseAndPublishedTrue(String skillCategory);

    List<Course> findByCareerIdIgnoreCaseAndSkillCategoryIgnoreCaseAndPublishedTrue(String careerId, String skillCategory);

    @Query("SELECT c FROM Course c WHERE c.published = true " +
            "AND (:careerId IS NULL OR LOWER(c.careerId) = LOWER(:careerId)) " +
            "AND (:skill IS NULL OR LOWER(c.skillCategory) = LOWER(:skill)) " +
            "AND (:difficulty IS NULL OR c.difficulty = :difficulty) " +
            "AND (:query IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "     OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "     OR LOWER(c.skillCategory) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY c.careerTitle ASC, c.title ASC")
    List<Course> searchCourses(
            @Param("careerId") String careerId,
            @Param("skill") String skill,
            @Param("difficulty") QuestionDifficulty difficulty,
            @Param("query") String query
    );

    long countByCareerIdIgnoreCase(String careerId);
}
