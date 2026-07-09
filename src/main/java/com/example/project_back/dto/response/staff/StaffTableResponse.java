package com.example.project_back.dto.response.staff;

import com.example.project_back.constant.TableStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffTableResponse {

    private Integer id;

    private String tableNumber;

    private Integer capacity;

    private TableStatus status;

    // Chuỗi hiển thị
    private String statusText;
}