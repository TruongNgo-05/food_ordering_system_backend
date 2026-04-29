package com.example.project_back.mapper;

import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import com.example.project_back.dto.request.admin.AdminUpdateUserRequest;
import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.UserResponse;
import com.example.project_back.entity.User;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class    UserMapper {

public static UserResponse map(User user){
    UserResponse userResponse = new UserResponse();
    BeanUtils.copyProperties(user, userResponse);
    return userResponse;
}
public static User map(UserCreateRequest userCreateRequest){
    User user = new User();
    BeanUtils.copyProperties(userCreateRequest,user);
    user.setStatus(Status.ACTIVED);
    user.setFailCount(0);
    user.setIsActive(true);
    user.setCreatedDate(LocalDateTime.now());
    return user;
}

    public static void map (UserUpdateRequest userUpdateRequest, User user){
        if(userUpdateRequest.getFullName() !=null){
            user.setFullName(userUpdateRequest.getFullName());
        }
        if(userUpdateRequest.getPhone() !=null){
            user.setPhone(userUpdateRequest.getPhone());
        }
        if(userUpdateRequest.getEmail() !=null){
            user.setEmail(userUpdateRequest.getEmail());
        }
    }


    public static void adminUpdate(AdminUpdateUserRequest request , User user){
    if(request.getEmail() !=null){
        user.setEmail(request.getEmail());
    }
    if(request.getRole() !=null){
        user.setRole(request.getRole());
    }
    }
}
