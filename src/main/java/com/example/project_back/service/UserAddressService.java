package com.example.project_back.service;

import com.example.project_back.dto.request.customer.AddressRequest;
import com.example.project_back.dto.response.user.AddressResponse;

import java.util.List;

public interface UserAddressService {
    List<AddressResponse> getMyAddresses();

    AddressResponse createAddress(AddressRequest request);

    AddressResponse updateAddress(Integer id,AddressRequest request);

    String deleteAddress(Integer id);
}
