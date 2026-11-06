package com.skillbridge.repository;

import com.skillbridge.model.Course;
import com.skillbridge.model.CourseEnrollment;
import com.skillbridge.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    @EntityGraph(attributePaths = {"course"})
    List<CourseEnrollment> findByUserOrderByEnrolledAtDesc(User user);

    Optional<CourseEnrollment> findByUserAndCourse(User user, Course course);

    Optional<CourseEnrollment> findByUserAndCourseId(User user, Long courseId);

    boolean existsByUserAndCourse(User user, Course course);

    long countByUser(User user);
}
