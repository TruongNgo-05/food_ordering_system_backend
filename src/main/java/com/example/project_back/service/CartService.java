package com.example.project_back.service;

import com.example.project_back.dto.request.customer.cart.AddToCartRequest;
import com.example.project_back.dto.request.customer.cart.UpdateCartRequest;
import com.example.project_back.dto.response.customer.cart.CartResponse;

public interface CartService {
    CartResponse getCart();

    CartResponse addToCart(AddToCartRequest request);

    CartResponse updateCartItem(Integer cartItemId, UpdateCartRequest request);

    CartResponse removeCartItem(Integer cartItemId);
}
