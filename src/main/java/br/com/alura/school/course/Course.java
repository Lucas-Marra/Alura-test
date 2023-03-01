package br.com.alura.school.course;

import br.com.alura.school.enrollment.Enrollment;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Size(max=10)
    @NotBlank
    @Column(nullable = false, unique = true)
    private String code;

    @Size(max=20)
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL
    )
    private List<Enrollment> users;

    @Deprecated
    protected Course() { }

    Course(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    String getCode() {
        return code;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    List<Enrollment> getUsers() {
        return users;
    }
}
