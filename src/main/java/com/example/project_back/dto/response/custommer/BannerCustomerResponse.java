package com.example.project_back.dto.response.custommer;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"id","title","description","imageUrl"})
public class BannerCustomerResponse {

    private Integer id;

    private String title;

    private String description;

    private String imageUrl;

}
