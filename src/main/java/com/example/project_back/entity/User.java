package com.example.project_back.entity;

import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String username;

    @JsonIgnore
    private String password;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "LONGTEXT")
    private String avatar;

    @Column(name="is_active")
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "create_date")
    private LocalDateTime createdDate;
// thêm cột này để đếm số lần bị khóa
    @Column(name = "fail_count")
    private Integer failCount;
// hiển thị thời gian bị khóa
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

}
