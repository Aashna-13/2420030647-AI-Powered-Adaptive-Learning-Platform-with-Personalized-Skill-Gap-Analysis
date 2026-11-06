package com.skillbridge.repository;

import com.skillbridge.model.Course;
import com.skillbridge.model.Lesson;
import com.skillbridge.model.LessonProgress;
import com.skillbridge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    List<LessonProgress> findByUserAndCourse(User user, Course course);

    List<LessonProgress> findByUserAndCourseId(User user, Long courseId);

    Optional<LessonProgress> findByUserAndLesson(User user, Lesson lesson);

    Optional<LessonProgress> findByUserAndLessonId(User user, Long lessonId);

    long countByUserAndCourseAndCompletedTrue(User user, Course course);

    long countByUserAndCourseIdAndCompletedTrue(User user, Long courseId);

    long countByUserAndCompletedTrue(User user);
}
