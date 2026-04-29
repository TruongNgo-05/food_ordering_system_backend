package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import com.example.project_back.dto.request.admin.AdminUpdateUserRequest;
import com.example.project_back.dto.request.spec.UserRequestParam;
import com.example.project_back.dto.request.user.ChangePasswordRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.response.user.UserResponse;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.UserMapper;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.FileService;
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
private final FileService fileService;
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
            throw new ApplicationException("Không tìm thấy tài khoản người dùng");
        }
        return UserMapper.map(user.get());
    }

    @Transactional
    @Override
    public UserResponse updateMyProfile(UserUpdateRequest userUpdateRequest) {
        String username = SecurityUtils.getCurrentUsername();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ApplicationException("Không tìm thấy tài khoản người dùng");
        }
        User users = user.get();
        UserMapper.map(userUpdateRequest, users);
        return UserMapper.map(userRepository.save(users));
    }
    @Transactional
    @Override
    public UserResponse updateUser(UserUpdateRequest request, MultipartFile avatar) {

        String username = SecurityUtils.getCurrentUsername();
        Optional<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            throw new ApplicationException("Không tìm thấy tài khoản người dùng");
        }
        User user = users.get();
        UserMapper.map(request, user);

        if (avatar != null && !avatar.isEmpty()) {
            if (user.getAvatar() != null && user.getAvatar().startsWith("/uploads/")) {
                fileService.deleteFile(user.getAvatar());
            }
            String url = fileService.uploadFile(avatar);
            user.setAvatar(url);
        } else if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        return UserMapper.map(userRepository.save(user));
    }

    @Override
    public Boolean changePassword(ChangePasswordRequest change) {
        String username = SecurityUtils.getCurrentUsername();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ApplicationException("Bạn phải login");
        }
        User users = user.get();
        if (!passwordEncoder.matches(change.getCurrentPassword(), users.getPassword())) {
            throw new ApplicationException("Mật khẩu không đúng , vui lòng kiểm tra lại");
        }

        if (passwordEncoder.matches(change.getNewPassword(), users.getPassword())) {
            throw new ApplicationException("Bạn hãy thay đổi mật khẩu khác");
        }
        if (!change.getNewPassword().equals(change.getConfirmNewPassword())) {
            throw new ApplicationException("Mật khẩu không hợp lệ");
        }
        users.setPassword(passwordEncoder.encode(change.getNewPassword()));
        userRepository.save(users);
        return true;
    }


    @Transactional
    @Override
    public UserResponse adminUpdateUser(AdminUpdateUserRequest updateUserRequest, Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ApplicationException("Không tìm thấy tài khoản người dùng");
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
            throw new ApplicationException("Không tìm thấy tài khoản người dùng");
        }
        userRepository.deleteById(id);
        return "Xóa tài khoản thành công ";
    }

}
