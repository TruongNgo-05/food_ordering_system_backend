package com.example.project_back.dto.response.staff;

import com.example.project_back.constant.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationStaffResponse {

    private Integer id;

    private String reservationCode;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String tableNumber;

    private LocalDateTime reservationTime;

    private BookingStatus status;
}
