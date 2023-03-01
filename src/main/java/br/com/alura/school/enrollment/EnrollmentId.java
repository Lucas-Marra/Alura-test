package br.com.alura.school.enrollment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
class EnrollmentId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "course_id")
    private Long courseId;

    @Deprecated
    protected EnrollmentId() {}

    EnrollmentId(Long userId, Long courseId) {
        this.courseId = courseId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentId that = (EnrollmentId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, courseId);
    }
}
