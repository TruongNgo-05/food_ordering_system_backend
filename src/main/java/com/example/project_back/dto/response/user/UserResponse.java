package com.example.project_back.dto.response.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
   private Integer id;

    private String Username;

    private String FullName;

    private String Email;

}
