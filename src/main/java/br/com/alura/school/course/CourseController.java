package br.com.alura.school.course;

import br.com.alura.school.enrollment.Enrollment;
import br.com.alura.school.enrollment.EnrollmentRepository;
import br.com.alura.school.enrollment.NewEnrollmentRequest;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserEnrollmentsResponse;
import br.com.alura.school.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;

@RestController
class CourseController {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final EnrollmentRepository enrollmentRepository;

    CourseController(CourseRepository courseRepository, UserRepository userRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping("/courses")
    ResponseEntity<List<CourseResponse>> allCourses() {
        return ResponseEntity.ok(courseRepository.findAll().stream().map(CourseResponse::new).collect(Collectors.toList()));
    }

    @GetMapping("/courses/{code}")
    ResponseEntity<CourseResponse> courseByCode(@PathVariable("code") String code) {
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, format("Course with code %s not found", code)));
        return ResponseEntity.ok(new CourseResponse(course));
    }

    @PostMapping("/courses")
    ResponseEntity<Void> newCourse(@RequestBody @Valid NewCourseRequest newCourseRequest) {
        courseRepository.save(newCourseRequest.toEntity());
        URI location = URI.create(format("/courses/%s", newCourseRequest.getCode()));
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/courses/{courseCode}/enroll")
    ResponseEntity<Void> newEnrollment(@PathVariable("courseCode") String courseCode, @RequestBody @Valid NewEnrollmentRequest newEnrollmentRequest) {
        User user = userRepository.findByUsername(newEnrollmentRequest.getUsername()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, format("User with username %s not found", newEnrollmentRequest.getUsername())));
        Course course = courseRepository.findByCode(courseCode).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, format("Course with code %s not found", courseCode)));

        Optional<Enrollment> enrollmentOptional = enrollmentRepository.findEnrollmentByUserAndCourse(user, course);

        if(enrollmentOptional.isEmpty()) {
            enrollmentRepository.save(new Enrollment(user, course));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(BAD_REQUEST);
        }

    }

    @GetMapping("/courses/enroll/report")
    ResponseEntity<List<UserEnrollmentsResponse>> teste(){
        List<User> users = userRepository.findDistinctByCoursesIsNotNull().orElseThrow(() -> new ResponseStatusException(NO_CONTENT, "No users found enrolled in any course"));
        if(users.size() == 0) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(users.stream().map(UserEnrollmentsResponse::new).sorted(Comparator.comparing(UserEnrollmentsResponse::getEnrollmentsCount).reversed()).collect(Collectors.toList()));
    }
}
