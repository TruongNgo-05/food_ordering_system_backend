package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.dto.request.customer.cart.AddToCartRequest;
import com.example.project_back.dto.request.customer.cart.UpdateCartRequest;
import com.example.project_back.dto.response.customer.cart.CartResponse;
import com.example.project_back.entity.Cart;
import com.example.project_back.entity.CartItem;
import com.example.project_back.entity.Food;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.CartMapper;
import com.example.project_back.repository.CartRepository;
import com.example.project_back.repository.FoodRepository;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.CartService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;


    @Override
    public CartResponse getCart() {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()) {
            throw new ApplicationException("User không tồn tại");
        }
        User user = users.get();

        Optional<Cart> cartOptional = cartRepository.findByUser_Id(user.getId());

        if (cartOptional.isEmpty()) {
            return CartMapper.emptyCart();
        }

        return CartMapper.toResponse(cartOptional.get());
    }


    @Transactional
    @Override
    public CartResponse addToCart(AddToCartRequest request) {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()) {
            throw new ApplicationException("User không tồn tại");
        }
        User user = users.get();
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    c.setCreatedAt(LocalDateTime.now());
                    c.setItems(new ArrayList<>());
                    return c;
                });

        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new ApplicationException("Food not found"));

        CartItem existItem = null;

        for (CartItem item : cart.getItems()) {
            if (item.getFood().getId().equals(food.getId())) {
                existItem = item;
                break;
            }
        }

        if (existItem != null) {
            existItem.setQuantity(existItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setFood(food);
            newItem.setQuantity(request.getQuantity());

            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);

        return CartMapper.toResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse updateCartItem(Integer cartItemId, UpdateCartRequest request) {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()) {
            throw new ApplicationException("User không tồn tại");
        }
        User user = users.get();
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ApplicationException("Cart not found"));

        for (CartItem item : cart.getItems()) {
            if (item.getId().equals(cartItemId)) {
                item.setQuantity(request.getQuantity());
                break;
            }
        }

        cartRepository.save(cart);

        return CartMapper.toResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse removeCartItem(Integer cartItemId) {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Unauthenticated");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("User not found"));

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ApplicationException("Cart not found"));

        CartItem removeItem = null;

        for (CartItem item : cart.getItems()) {
            if (item.getId().equals(cartItemId)) {
                removeItem = item;
                break;
            }
        }

        if (removeItem != null) {
            cart.getItems().remove(removeItem);
        }

        cartRepository.save(cart);

        return CartMapper.toResponse(cart);
    }
}