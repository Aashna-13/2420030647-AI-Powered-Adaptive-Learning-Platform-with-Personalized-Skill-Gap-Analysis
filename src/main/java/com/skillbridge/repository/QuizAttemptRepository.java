package com.skillbridge.repository;

import com.skillbridge.model.Lesson;
import com.skillbridge.model.QuizAttempt;
import com.skillbridge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByUserAndLessonOrderByAttemptedAtDesc(User user, Lesson lesson);

    Optional<QuizAttempt> findTopByUserAndLessonOrderByAttemptedAtDesc(User user, Lesson lesson);

    Optional<QuizAttempt> findTopByUserAndLessonIdOrderByAttemptedAtDesc(User user, Long lessonId);

    long countByUserAndPassedTrue(User user);
}
