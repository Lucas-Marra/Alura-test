package br.com.alura.school.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserEnrollmentsResponse {

    @JsonProperty
    private final String email;

    @JsonProperty(value = "quantidade_matriculas")
    private final Integer enrollmentsCount;

    public UserEnrollmentsResponse(User user) {
        this.email = user.getEmail();
        this.enrollmentsCount = user.getCourses().size();
    }

    public Integer getEnrollmentsCount() {
        return enrollmentsCount;
    }
}
