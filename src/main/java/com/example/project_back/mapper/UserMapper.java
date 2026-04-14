package com.example.project_back.mapper;

import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import com.example.project_back.dto.request.customer.CustomerUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.UserResponseDTO;
import com.example.project_back.entity.User;
import org.springframework.beans.BeanUtils;

public class UserMapper {
public static UserResponseDTO map(User user){
    UserResponseDTO userResponseDTO = new UserResponseDTO();
    BeanUtils.copyProperties(user,userResponseDTO);
    userResponseDTO.setFullName(user.getFirstName()+" "+user.getLastName());
    return userResponseDTO;
}
public static User map(UserCreateRequest userCreateRequest){
    User user = new User();
    BeanUtils.copyProperties(userCreateRequest,user);
    user.setStatus(Status.ACTIVED);
    user.setFailCount(0);
    user.setRole(Role.CUSTOMER);
    return user;
}

public static void map(CustomerUpdateRequest  customerUpdateRequest,User  user){
    if(customerUpdateRequest.getFirstName()!=null){
        user.setFirstName(customerUpdateRequest.getFirstName());
    }
    if(customerUpdateRequest.getLastName()!=null){
        user.setLastName(customerUpdateRequest.getLastName());
    }
    if(customerUpdateRequest.getEmail()!=null){
        user.setEmail(customerUpdateRequest.getEmail());
    }
    if(customerUpdateRequest.getPassword()!=null){
        user.setPassword(customerUpdateRequest.getPassword());
    }
}
}
