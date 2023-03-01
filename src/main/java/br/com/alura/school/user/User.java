package br.com.alura.school.user;

import br.com.alura.school.enrollment.Enrollment;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Size(max=20)
    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL
    )
    private List<Enrollment> courses;

    @Deprecated
    protected User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    String getUsername() {
        return username;
    }

    String getEmail() {
        return email;
    }

    List<Enrollment> getCourses() {
        return courses;
    }
}
