package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.dto.request.customer.AddressRequest;
import com.example.project_back.dto.response.user.AddressResponse;
import com.example.project_back.entity.User;
import com.example.project_back.entity.UserAddress;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.AddressMapper;
import com.example.project_back.repository.UserAddressRepository;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    @Override
    public List<AddressResponse> getMyAddresses(){
        String username = SecurityUtils.getCurrentUsername();
        if(username == null){
            throw new ApplicationException("Unauthenticated");
        }
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()){
            throw new ApplicationException("User not found");
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
        if(username == null){
            throw new ApplicationException("Unauthenticated");
        }
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()){
            throw new ApplicationException("User not found");
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
        if(username == null){
            throw new ApplicationException("Unauthenticated");
        }
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()){
            throw new ApplicationException("User not found");
        }
        User user = users.get();
        Optional<UserAddress> userAddress = userAddressRepository.findById(id);
        if(userAddress.isEmpty()){
            throw new ApplicationException("Address not found");
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
        if(username == null){
            throw new ApplicationException("Unauthenticated");
        }
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()){
            throw new ApplicationException("User not found");
        }
        User user = users.get();

        Optional<UserAddress> userAddress = userAddressRepository.findById(id);
        if(userAddress.isEmpty()){
            throw new ApplicationException("UserAddress not found");
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
}
