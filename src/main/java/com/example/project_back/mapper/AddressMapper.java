package com.example.project_back.mapper;

import com.example.project_back.dto.request.customer.AddressRequest;
import com.example.project_back.dto.response.user.AddressResponse;
import com.example.project_back.entity.UserAddress;
import org.springframework.beans.BeanUtils;

public class AddressMapper {
    public static AddressResponse toAddressResponse(UserAddress userAddress){
        AddressResponse addressResponse = new AddressResponse();
        BeanUtils.copyProperties(userAddress, addressResponse);
        return addressResponse;
    }


    public static UserAddress addressCreate(AddressRequest addressRequest){
        UserAddress userAddress = new UserAddress();
        userAddress.setAddress(addressRequest.getAddress());
        userAddress.setIsDefault(addressRequest.getIsDefault());
        return userAddress;
    }

    public static void addressUpdate(AddressRequest addressRequest,UserAddress userAddress){
        if(addressRequest.getReceiverName() != null){
            userAddress.setReceiverName(addressRequest.getReceiverName());
        }
        if(addressRequest.getReceiverPhone() != null){
            userAddress.setReceiverPhone(addressRequest.getReceiverPhone());
        }
        if(addressRequest.getIsDefault() != null){
            userAddress.setIsDefault(addressRequest.getIsDefault());
        }
    }
}
