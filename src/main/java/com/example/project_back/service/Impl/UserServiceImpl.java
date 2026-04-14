package com.example.project_back.service.Impl;

import com.example.project_back.dto.request.customer.CustomerUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.UserResponseDTO;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.UserMapper;
import com.example.project_back.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements com.example.project_back.service.UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponseDTO> findAllUsers(Pageable pageable){
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserMapper::map);
    }

    @Override
    public UserResponseDTO findUserById(Long id){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new ApplicationException("User not found");
        }
        return UserMapper.map(user.get());
    }

    @Override
    public UserResponseDTO createUser(UserCreateRequest createUserRequest) {
        if(userRepository.findByEmailOrUsername(createUserRequest.getEmail(), createUserRequest.getUsername()).isEmpty()){
            throw new ApplicationException("User da ton tai");
        }
        if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
            throw new ApplicationException("Password không khớp");
        }
        User user =UserMapper.map(createUserRequest);
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        User savedUser = userRepository.save(user);
        UserResponseDTO userResponseDTO = UserMapper.map(savedUser);
        return userResponseDTO;
    }

    @Override
    public UserResponseDTO updateUser(Long id, CustomerUpdateRequest customerUpdateRequest) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new ApplicationException("User not found");
        }

        Optional<User> updatedUser = userRepository.findByUsername(customerUpdateRequest.getEmail());
        if(updatedUser.isEmpty()){
            throw new ApplicationException("Email da ton tai");
        }

        User users = user.get();
        UserMapper.map(customerUpdateRequest, users);
        users.setPassword(passwordEncoder.encode(customerUpdateRequest.getPassword()));
        UserResponseDTO userResponseDTO = UserMapper.map( userRepository.save(users));
        return userResponseDTO;
    }
    @Override
    public String deleteUser(Long id){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new ApplicationException("User not found");
        }
        userRepository.deleteById(id);
        return "User has been deleted";
    }
}
