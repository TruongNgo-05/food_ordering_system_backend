package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.UserResponseDTO;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.UserMapper;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

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

    @Transactional
    @Override
    public UserResponseDTO createUser(UserCreateRequest createUserRequest) {
        if(userRepository.findByEmailOrUsername(createUserRequest.getEmail(), createUserRequest.getUsername()).isPresent()){
            throw new ApplicationException("User da ton tai");
        }
        if(!createUserRequest.getPassWord().equals(createUserRequest.getConfirmPassword())){
            throw new ApplicationException("Password không khớp");
        }
        User user =UserMapper.map(createUserRequest);
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassWord()));
        User savedUser = userRepository.save(user);
        UserResponseDTO userResponseDTO = UserMapper.map(savedUser);
        return userResponseDTO;
    }

    @Override
    public UserResponseDTO getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new ApplicationException("User not found");
        }
        return UserMapper.map(user.get());
    }

    @Transactional
    @Override
    public UserResponseDTO updateMyProfile( UserUpdateRequest userUpdateRequest) {
        String username = SecurityUtils.getCurrentUsername();
        Optional<User> user = userRepository.findByUsername(username);
       if(user.isEmpty()){
           throw new ApplicationException("User not found");
       }
        User users = user.get();
        UserMapper.map(userUpdateRequest, users);
        return UserMapper.map( userRepository.save(users));
    }

    @Override
    public String uploadAvatar(Long id, MultipartFile file) throws IOException {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // XÓA ẢNH CŨ
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            String oldAvatarUrl = user.getAvatar();

            String oldFileName = oldAvatarUrl.substring(oldAvatarUrl.lastIndexOf("/") + 1);

            Path oldFilePath = Paths.get("uploads").resolve(oldFileName);

            Files.deleteIfExists(oldFilePath);
        }

        // VALIDATE FILE
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new RuntimeException("File must be image");
        }

        // TẠO TÊN FILE
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // TẠO FOLDER nếu chưa có
        Path uploadPath = Paths.get("uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // LƯU FILE
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        // URL
        String avatarUrl = "http://localhost:8080/uploads/" + fileName;

        user.setAvatar(avatarUrl);
        userRepository.save(user);

        return avatarUrl;
    }


@Transactional
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
