package com.example.project_back.dto.response.custommer;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"id","avatar","email","username","fullName"})
public class CustomerResponseDTO {

    private Long id;

    private String avatar;

    private String email;

    private String username;

    private String fullName;
}
