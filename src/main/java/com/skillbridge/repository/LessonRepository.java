package com.skillbridge.repository;

import com.skillbridge.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByCourseIdOrderByLessonOrderAsc(Long courseId);

    Optional<Lesson> findByCourseIdAndLessonOrder(Long courseId, int lessonOrder);

    long countByCourseId(Long courseId);
}
