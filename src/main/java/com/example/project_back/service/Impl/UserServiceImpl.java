package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import com.example.project_back.dto.request.admin.AdminUpdateUserRequest;
import com.example.project_back.dto.request.customer.AddressRequest;
import com.example.project_back.dto.request.spec.UserRequestParam;
import com.example.project_back.dto.request.user.ChangePasswordRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.response.user.AddressResponse;
import com.example.project_back.dto.response.user.UserResponse;
import com.example.project_back.entity.User;
import com.example.project_back.entity.UserAddress;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.AddressMapper;
import com.example.project_back.mapper.UserMapper;
import com.example.project_back.repository.UserAddressRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final MailService mailService;

    //ADMIN
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
    public UserResponse adminUpdateUser(AdminUpdateUserRequest updateUserRequest, Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ApplicationException("Không tìm thấy tài khoản người dùng");
        }
        User users = user.get();
        UserMapper.adminUpdate(updateUserRequest, users);
        return UserMapper.map(userRepository.save(users));
    }

    @Override
    public String unlockAccount(Long userId) {

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ApplicationException("Không tìm thấy tài khoản người dùng");
        }
        User users = user.get();
        users.setStatus(Status.ACTIVED);
        users.setFailCount(0);
        users.setLockTime(null);

        userRepository.save(users);

        mailService.sendEmail(
                users.getEmail(),
                "Tài khoản của bạn đã được mở khóa",
                "Xin chào " + users.getUsername() + ",\n\n"
                        + "Tài khoản của bạn đã được quản trị viên mở khóa thành công.\n"
                        + "Bạn hiện có thể đăng nhập và tiếp tục sử dụng hệ thống như bình thường.\n\n"
                        + "Nếu bạn gặp bất kỳ vấn đề nào khi đăng nhập, vui lòng liên hệ admin.\n\n"
                        + "Trân trọng"
        );
        return users.getEmail();
    }

    @Override
    public String lockAccount(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ApplicationException("Không tìm thấy mã tài khoản người dùng");
        }
        User users = user.get();
        users.setStatus(Status.LOCKED);
        users.setFailCount(5);
        users.setLockTime(LocalDateTime.now());

        userRepository.save(users);
        mailService.sendEmail(
                users.getEmail(),
                "Tài khoản của bạn đã bị khóa",
                "Xin chào " + users.getUsername() + ",\n\n"
                        + "Tài khoản của bạn đã bị admin khóa.\n"
                        + "Vui lòng liên hệ quản trị viên để được hỗ trợ mở khóa."
        );

        return users.getEmail();
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

//   CUSTOMER

//    address
    @Override
    public List<AddressResponse> getMyAddresses(){
        String username = SecurityUtils.getCurrentUsername();
        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()) {
            throw new ApplicationException("User không tồn tại");
        }
        User user = users.get();
        List<AddressResponse> responseList = new ArrayList<>();
        List<UserAddress> addresses = userAddressRepository.findByUserId(user.getId());
        for(UserAddress userAddress : addresses){
            AddressResponse res = AddressMapper.toAddressResponse(userAddress);
            responseList.add(res);
        }
        return responseList;
    }

    @Override
    public AddressResponse createAddress(AddressRequest request){
        String username = SecurityUtils.getCurrentUsername();
        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()) {
            throw new ApplicationException("User không tồn tại");
        }
        User user = users.get();
        Long userId = user.getId();
        if(Boolean.TRUE.equals(request.getIsDefault())){
            userAddressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(addr -> {
                        addr.setIsDefault(false);
                        userAddressRepository.save(addr);
                    });
        }
        UserAddress address = AddressMapper.addressCreate(request);
        address.setUserId(userId);
        if(userAddressRepository.findByUserId(userId).isEmpty()){
            address.setIsDefault(true);
        }
        UserAddress savedAddress = userAddressRepository.save(address);
        return AddressMapper.toAddressResponse(savedAddress);
    }

    @Override
    public AddressResponse updateAddress(Integer id,AddressRequest request){
        String username = SecurityUtils.getCurrentUsername();
        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()) {
            throw new ApplicationException("User không tồn tại");
        }
        User user = users.get();
        Optional<UserAddress> userAddress = userAddressRepository.findById(id);
        if(userAddress.isEmpty()){
            throw new ApplicationException("Address không tồn tại");
        }
        UserAddress address= userAddress.get();
        if(!address.getUserId().equals(user.getId())){
            throw new ApplicationException("Forbidden");
        }

        if(Boolean.TRUE.equals(request.getIsDefault())){
            List<UserAddress> list = userAddressRepository.findByUserId(user.getId());
            for(UserAddress addr : list){
                if(Boolean.TRUE.equals(addr.getIsDefault())){
                    addr.setIsDefault(false);
                    userAddressRepository.save(addr);
                }
            }
            address.setIsDefault(true);
        }

        AddressMapper.addressUpdate(request, address);

        userAddressRepository.save(address);

        return AddressMapper.toAddressResponse(address);
    }

    @Override
    public String deleteAddress(Integer id){
        String username = SecurityUtils.getCurrentUsername();
        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()) {
            throw new ApplicationException("User không tồn tại");
        }
        User user = users.get();

        Optional<UserAddress> userAddress = userAddressRepository.findById(id);
        if(userAddress.isEmpty()){
            throw new ApplicationException("UserAddress không tồn tại");
        }
        UserAddress address = userAddress.get();
        if(!address.getUserId().equals(user.getId())){
            throw new ApplicationException("Forbidden");
        }
        boolean isDefault = Boolean.TRUE.equals(address.getIsDefault());
        userAddressRepository.deleteById(id);
        if(isDefault){
            List<UserAddress> list = userAddressRepository.findByUserId(user.getId());

            if(!list.isEmpty()){
                UserAddress newDefault = list.get(0); // lấy cái đầu
                newDefault.setIsDefault(true);
                userAddressRepository.save(newDefault);
            }
        }
        return "deleted";
    }


//    USER
    @Transactional
    @Override
    public UserResponse createUser(UserCreateRequest createUserRequest) {
        if (userRepository.findByUsername(createUserRequest.getUsername()).isPresent()) {
        throw new ApplicationException("Username đã tồn tại");
    }
        if (userRepository.findByEmail(createUserRequest.getEmail()).isPresent()) {
            throw new ApplicationException("Email đã tồn tại");
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
            String url = fileService.uploadFile(avatar, "avatars");
            user.setAvatar(url);
            user.setAvatar(url);
        } else if (request.getAvatar() != null) {

            if(request.getAvatar().isEmpty()){
                user.setAvatar(null);
            }else{
                user.setAvatar(request.getAvatar());
            }

        }
        return UserMapper.map(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponse deleteAvatar() {

        String username = SecurityUtils.getCurrentUsername();

        Optional<User> users = userRepository.findByUsername(username);

        if (users.isEmpty()) {
            throw new ApplicationException(
                    "Không tìm thấy tài khoản người dùng"
            );
        }

        User user = users.get();


        // Xóa file trong server
        if (user.getAvatar() != null && user.getAvatar().startsWith("/uploads/")) {

            fileService.deleteFile(user.getAvatar());
        }


        // Xóa đường dẫn trong database
        user.setAvatar(null);


        return UserMapper.map(
                userRepository.save(user)
        );
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


}
