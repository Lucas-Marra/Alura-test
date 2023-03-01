package br.com.alura.school.course;

import br.com.alura.school.enrollment.Enrollment;
import br.com.alura.school.enrollment.EnrollmentRepository;
import br.com.alura.school.enrollment.NewEnrollmentRequest;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CourseControllerTest {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void should_retrieve_course_by_code() throws Exception {
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));

        mockMvc.perform(get("/courses/java-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("java-1")))
                .andExpect(jsonPath("$.name", is("Java OO")))
                .andExpect(jsonPath("$.shortDescription", is("Java and O...")));
    }

    @Test
    void should_retrieve_all_courses() throws Exception {
        courseRepository.save(new Course("spring-1", "Spring Basics", "Spring Core and Spring MVC."));
        courseRepository.save(new Course("spring-2", "Spring Boot", "Spring Boot"));

        mockMvc.perform(get("/courses")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].code", is("spring-1")))
                .andExpect(jsonPath("$[0].name", is("Spring Basics")))
                .andExpect(jsonPath("$[0].shortDescription", is("Spring Cor...")))
                .andExpect(jsonPath("$[1].code", is("spring-2")))
                .andExpect(jsonPath("$[1].name", is("Spring Boot")))
                .andExpect(jsonPath("$[1].shortDescription", is("Spring Boot")));
    }

    @Test
    void should_add_new_course() throws Exception {
        NewCourseRequest newCourseRequest = new NewCourseRequest("java-2", "Java Collections", "Java Collections: Lists, Sets, Maps and more.");

        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newCourseRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/courses/java-2"));
    }

    @Test
    void not_found_when_user_does_not_exist_for_enrollment() throws Exception {
        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("non-existent");
        Course course = new Course("spring-3", "Spring Data", "Spring Data: JPA and Hibernate");

        courseRepository.save(course);

        mockMvc.perform(post("/courses/spring-3/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void not_found_when_course_does_not_exist_for_enrollment() throws Exception {
        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("Paul");
        User user = new User("Paul", "paul@email.com");

        userRepository.save(user);

        mockMvc.perform(post("/courses/invalid/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_add_new_enrollement() throws Exception {
        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("lucas");

        userRepository.save(new User("lucas", "lucas@email.com"));
        courseRepository.save(new Course("sql-1", "MySql Basics", "MySql Basics"));

        mockMvc.perform(post("/courses/sql-1/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void bad_request_when_user_already_enrolled_in_course_mentioned() throws Exception {
        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("carlos");

        userRepository.save(new User("carlos", "carlos@email.com"));
        courseRepository.save(new Course("java-3", "Streams in java", "Streams in java"));

        mockMvc.perform(post("/courses/java-3/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/courses/java-3/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_retrieve_in_first_position_the_user_with_more_enrolled_courses() throws Exception {
        User jack = new User("jack", "jack@email.com");
        User julia = new User("julia", "julia@email.com");

        Course sql2 = new Course("sql-2", "SELECT", "SELECT statement");
        Course sql3 = new Course("sql-3", "INSERT", "INSERT INTO statement");
        Course sql4 = new Course("sql-4", "DELETE", "DELETE statement");

        userRepository.save(julia);
        userRepository.save(jack);

        courseRepository.save(sql2);
        courseRepository.save(sql3);
        courseRepository.save(sql4);

        enrollmentRepository.save(new Enrollment(julia, sql2));
        enrollmentRepository.save(new Enrollment(julia, sql3));
        enrollmentRepository.save(new Enrollment(julia, sql4));

        enrollmentRepository.save(new Enrollment(jack, sql2));

        mockMvc.perform(get("/courses/enroll/report")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].email", is("julia@email.com")))
                .andExpect(jsonPath("$[0].quantidade_matriculas", is(3)))
                .andExpect(jsonPath("$[1].email", is("jack@email.com")))
                .andExpect(jsonPath("$[1].quantidade_matriculas", is(1)));
    }

    @Test
    void no_content_when_no_enrollments_found() throws Exception {
        User alicia = new User("alicia", "alicia@email.com");
        Course sql2 = new Course("Python-1", "Python Basics", "Python Basics");

        userRepository.save(alicia);
        courseRepository.save(sql2);

        mockMvc.perform(get("/courses/enroll/report")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}