package com.example.project_back.service;

import com.example.project_back.dto.request.admin.AdminUpdateUserRequest;
import com.example.project_back.dto.request.customer.AddressRequest;
import com.example.project_back.dto.request.spec.UserRequestParam;
import com.example.project_back.dto.request.user.ChangePasswordRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.response.user.AddressResponse;
import com.example.project_back.dto.response.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {


//ADMIN
Page<UserResponse> findAllUsers(UserRequestParam param, Pageable pageable);

UserResponse findUserById(Long id);

UserResponse adminUpdateUser(AdminUpdateUserRequest updateUserRequest, Long id);

    String unlockAccount(Long userId );

    String lockAccount(Long userId );

    String deleteUser(Long id);
//CUSTOMER

//    address
List<AddressResponse> getMyAddresses();

    AddressResponse createAddress(AddressRequest request);

    AddressResponse updateAddress(Integer id,AddressRequest request);

    String deleteAddress(Integer id);


    //USER
    UserResponse createUser(UserCreateRequest createUserRequest);

    UserResponse getCurrentUser();

    UserResponse updateUser(UserUpdateRequest request, MultipartFile avatar);
    Boolean changePassword(ChangePasswordRequest change);

}
