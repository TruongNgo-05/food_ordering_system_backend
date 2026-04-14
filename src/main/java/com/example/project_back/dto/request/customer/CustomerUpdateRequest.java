package com.example.project_back.dto.request.customer;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerUpdateRequest {
    private String firstName;

    private String lastName;

    private String email;

    private String password;

}
