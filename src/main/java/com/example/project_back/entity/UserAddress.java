package com.example.project_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name= "receiver_name")
    private String receiverName;

    @Column(name= "receiver_phone")
    private String receiverPhone;

    @Column(columnDefinition = "TEXT")
    private String address;

    private Boolean isDefault;
}