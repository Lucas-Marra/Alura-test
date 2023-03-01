package br.com.alura.school.enrollment;

import br.com.alura.school.course.Course;
import br.com.alura.school.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findEnrollmentByUserAndCourse(User user, Course course);
}
