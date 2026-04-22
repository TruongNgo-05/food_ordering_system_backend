package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import com.example.project_back.dto.request.admin.AdminUpdateUserRequest;
import com.example.project_back.dto.request.spec.UserRequestParam;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.response.user.UserResponse;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.UserMapper;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.UserService;
import com.example.project_back.specification.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + "/uploads";
    private static final String BASE_URL = "http://localhost:8080/uploads/";
    @Override
    public Page<UserResponse> findAllUsers(UserRequestParam param, Pageable pageable) {
        String email = param.getEmail();
        String fullName = param.getFullName();
        Role role = param.getRole();
        Status status = param.getStatus();
        LocalDate minDate = param.getMinDate();
        LocalDate maxDate = param.getMaxDate();

        Specification<User> spec = Specification.unrestricted();

        if(email!=null && !email.trim().isEmpty() ){
            spec=spec.and(UserSpecification.hasEmail(email));
        }
        if(fullName!=null && !fullName.trim().isEmpty() ){
            spec=spec.and(UserSpecification.hasFullName(fullName));
        }
        if(role!=null){
            spec=spec.and(UserSpecification.hasRole(role));
        }
        if(status!=null){
            spec=spec.and(UserSpecification.hasStatus(status));
        }
        if (minDate != null && maxDate != null) {
            spec = spec.and(UserSpecification.hasCreateDate(minDate, maxDate));
        }
        return userRepository.findAll(spec,pageable).map(UserMapper::map);
//        Page<User> users = userRepository.findAll(pageable);
//        return users.map(UserMapper::map);
    }

    @Override
    public UserResponse findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ApplicationException("User not found");
        }
        return UserMapper.map(user.get());
    }

    @Transactional
    @Override
    public UserResponse createUser(UserCreateRequest createUserRequest) {
        if (userRepository.findByEmailOrUsername(createUserRequest.getEmail(), createUserRequest.getUsername()).isPresent()) {
            throw new ApplicationException("User đã tồn tại ");
        }
        if (!createUserRequest.getPassWord().equals(createUserRequest.getConfirmPassword())) {
            throw new ApplicationException("Password không khớp");
        }
        if (userRepository.findByPhone(createUserRequest.getPhone()).isPresent()) {
            throw new ApplicationException("Số điện thoại đã tồn tại");
        }
        User user = UserMapper.map(createUserRequest);
        if (createUserRequest.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassWord()));
        User savedUser = userRepository.save(user);

        UserResponse userResponse = UserMapper.map(savedUser);
        return userResponse;
    }

    @Override
    public UserResponse getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ApplicationException("User not found");
        }
        return UserMapper.map(user.get());
    }

    @Transactional
    @Override
    public UserResponse updateMyProfile(UserUpdateRequest userUpdateRequest) {
        String username = SecurityUtils.getCurrentUsername();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ApplicationException("User not found");
        }
        User users = user.get();
        UserMapper.map(userUpdateRequest, users);
        return UserMapper.map(userRepository.save(users));
    }

//    @Override
//    public String uploadAvatar(Long id, MultipartFile file) throws IOException {
//
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 1. validate file
//        if (file.isEmpty()) {
//            throw new RuntimeException("File is empty");
//        }
//
//        List<String> allowed = List.of("image/png", "image/jpeg", "image/jpg", "image/webp");
//        if (!allowed.contains(file.getContentType())) {
//            throw new RuntimeException("Invalid image type");
//        }
//
//        // 2. delete old avatar safely
//        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
//            try {
//                URI uri = URI.create(user.getAvatar());
//                String oldFileName = Paths.get(uri.getPath()).getFileName().toString();
//
//                Path oldFilePath = Paths.get("uploads").resolve(oldFileName);
//                Files.deleteIfExists(oldFilePath);
//            } catch (Exception e) {
//                // không crash nếu xóa fail
//                System.out.println("Cannot delete old avatar: " + e.getMessage());
//            }
//        }
//
//        // 3. create file name
//        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//        // 4. create folder
//        Path uploadPath = Paths.get("uploads");
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        // 5. save file
//        Path filePath = uploadPath.resolve(fileName);
//        Files.copy(file.getInputStream(), filePath);
//
//        // 6. build URL
//        String avatarUrl = "http://localhost:8080/uploads/" + fileName;
//
//        // 7. save DB
//        user.setAvatar(avatarUrl);
//        userRepository.save(user);
//
//        return avatarUrl;
//    }
    @Override
    public String uploadAvatar(Long id, MultipartFile file) throws IOException {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        List<String> allowedTypes = List.of("image/png", "image/jpeg", "image/jpg", "image/webp");

        if (!allowedTypes.contains(file.getContentType())) {
            throw new RuntimeException("Invalid image type");
        }

        // 🔥 FIX ROOT PATH
        Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 🔥 DELETE OLD FILE (ĐÃ FIX ĐÚNG TARGET)
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            try {
                Path oldFilePath = uploadPath.resolve(user.getAvatar()).normalize();

                System.out.println("DELETE OLD FILE: " + oldFilePath);

                Files.deleteIfExists(oldFilePath);

            } catch (Exception e) {
                System.out.println("DELETE FAIL: " + e.getMessage());
            }
        }

        // SAVE NEW FILE
        String fileName = UUID.randomUUID() + "_" +
                file.getOriginalFilename().replaceAll("\\s+", "_");

        Path newFilePath = uploadPath.resolve(fileName).normalize();

        Files.copy(file.getInputStream(), newFilePath);

        user.setAvatar(fileName);
        userRepository.save(user);

        return BASE_URL + fileName;
    }

    @Transactional
    @Override
    public UserResponse adminUpdateUser(AdminUpdateUserRequest updateUserRequest, Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ApplicationException("User not found");
        }
        User users = user.get();
        UserMapper.adminUpdate(updateUserRequest, users);
        return UserMapper.map(userRepository.save(users));
    }

    @Transactional
    @Override
    public String deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ApplicationException("User not found");
        }
        userRepository.deleteById(id);
        return "User has been deleted";
    }

}
