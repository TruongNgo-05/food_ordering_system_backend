package com.example.project_back.dto.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FAQResponse {

    private Integer id;

    private String question;

    private String answer;
}
