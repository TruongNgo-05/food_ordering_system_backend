package com.example.project_back.mapper;

import com.example.project_back.dto.response.customer.cart.CartItemResponse;
import com.example.project_back.dto.response.customer.cart.CartResponse;
import com.example.project_back.entity.Cart;
import com.example.project_back.entity.CartItem;


import java.util.ArrayList;
import java.util.List;

public class CartMapper {
    public static CartResponse toResponse(Cart cart) {

        CartResponse response = new CartResponse();

        List<CartItemResponse> list = new ArrayList<>();
        double total = 0;

        if (cart.getItems() != null) {

            for (CartItem item : cart.getItems()) {

                CartItemResponse dto = new CartItemResponse();
                dto.setItemId(item.getId());
                dto.setFoodId(item.getFood().getId());
                dto.setFoodName(item.getFood().getName());
                dto.setPrice(item.getFood().getPrice());
                dto.setQuantity(item.getQuantity());
                dto.setImage(item.getFood().getImage());

                double itemTotal = item.getFood().getPrice() * item.getQuantity();

                total += itemTotal;
                list.add(dto);
            }
        }

        response.setCartId(cart.getId());
        response.setItems(list);
        response.setTotalPrice(total);

        return response;
    }

    public static CartResponse emptyCart() {

        CartResponse res = new CartResponse();
        res.setItems(new ArrayList<>());
        res.setTotalPrice(0.0);

        return res;
    }
}
