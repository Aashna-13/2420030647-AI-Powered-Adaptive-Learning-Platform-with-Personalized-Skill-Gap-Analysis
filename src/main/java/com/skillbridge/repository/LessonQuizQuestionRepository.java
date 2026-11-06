package com.skillbridge.repository;

import com.skillbridge.model.LessonQuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonQuizQuestionRepository extends JpaRepository<LessonQuizQuestion, Long> {

    List<LessonQuizQuestion> findByLessonIdOrderByQuestionOrderAsc(Long lessonId);

    void deleteByLessonId(Long lessonId);

    long countByLessonId(Long lessonId);
}
