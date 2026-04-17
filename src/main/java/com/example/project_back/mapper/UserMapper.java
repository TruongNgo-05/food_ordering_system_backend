package com.example.project_back.mapper;

import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.UserResponseDTO;
import com.example.project_back.entity.User;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

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
    user.setIsActive(true);
    user.setCreatedDate(LocalDateTime.now());
    return user;
}

//    public static void map (UserUpdateRequest userUpdateRequest, User user){
//        if(userUpdateRequest.getAvatar() !=null){
//            user.setAvatar(userUpdateRequest.getAvatar());
//        }
//        if(userUpdateRequest.getFirstName() !=null){
//            user.setFirstName(userUpdateRequest.getFirstName());
//        }
//        if(userUpdateRequest.getLastName() !=null){
//            user.setLastName(userUpdateRequest.getLastName());
//        }
//        if(userUpdateRequest.getEmail() !=null){
//            user.setEmail(userUpdateRequest.getEmail());
//        }
//    }
public static void map(UserUpdateRequest request, User user){
    if(request.getAvatar() != null){
        user.setAvatar(request.getAvatar());
    }
    //  Tách fullName
    if(request.getFullName() != null){

        String fullName = request.getFullName().trim();

        String[] parts = fullName.split(" ");

        String firstName = parts[0];

        String lastName = "";

        if(parts.length > 1){
            lastName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        }

        user.setFirstName(firstName);

        user.setLastName(lastName);
    }

    if(request.getEmail() != null){
        user.setEmail(request.getEmail());
    }
}
}
