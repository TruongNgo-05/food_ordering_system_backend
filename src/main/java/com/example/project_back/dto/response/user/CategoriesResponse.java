package com.example.project_back.dto.response.user;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"id","name","description"})
public class CategoriesResponse {

    private Integer id;

    private String name;


}
