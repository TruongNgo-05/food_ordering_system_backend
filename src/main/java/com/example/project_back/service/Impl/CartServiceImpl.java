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
        if (users == null) {
            throw new ApplicationException("User không tồn tại");
        }
        User user = users.get();
        //  Tìm cart theo user
        Optional<Cart> carts = cartRepository.findByUser_Id(user.getId());
        Cart cart = carts.get();
        // Nếu chưa có cart thì tạo mới
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCreatedAt(LocalDateTime.now());
            cart.setItems(new ArrayList<>());
        }

        // Tìm food
        Optional<Food> foods = foodRepository.findById(request.getFoodId());
        Food food = foods.get();
        if (food == null) {
            throw new ApplicationException("Food không tồn tại");
        }

        //  Kiểm tra item đã tồn tại trong cart chưa
        CartItem existItem = null;

        for (CartItem item : cart.getItems()) {
            if (item.getFood().getId().equals(food.getId())) {
                existItem = item;
                break;
            }
        }
        // Nếu đã có thì tăng số lượng
        if (existItem != null) {
            int newQuantity = existItem.getQuantity() + request.getQuantity();
            existItem.setQuantity(newQuantity);
        }
        // Nếu chưa có thì thêm mới
        else {
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
        // 3. Tìm cart
        Optional<Cart> carts = cartRepository.findByUser_Id(user.getId());
        Cart cart = carts.get();
        if (cart == null) {
            throw new ApplicationException("Cart không tồn tại");
        }

        //  Tìm item trong cart
        CartItem foundItem = null;

        for (CartItem item : cart.getItems()) {
            if (item.getId().equals(cartItemId)) {
                foundItem = item;
                break;
            }
        }
        if (foundItem == null) {
            throw new ApplicationException("Cart item không tồn tại");
        }
        //  Update số lượng
        foundItem.setQuantity(request.getQuantity());
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
        Optional<User> users = userRepository.findByUsername(username);
        if(users.isEmpty()) {
            throw new ApplicationException("User không tồn tại");
        }
        User user = users.get();
        // 3. Tìm cart
        Optional<Cart> carts = cartRepository.findByUser_Id(user.getId());
        Cart cart = carts.get();
        if (cart == null) {
            throw new ApplicationException("Cart không tồn tại");
        }
        // Tìm item cần xóa
        CartItem removeItem = null;

        for (CartItem item : cart.getItems()) {
            if (item.getId().equals(cartItemId)) {
                removeItem = item;
                break;
            }
        }
        //  Nếu không tìm thấy
        if (removeItem == null) {
            throw new ApplicationException("Cart item không tồn tại");
        }
        //  Xóa item
        cart.getItems().remove(removeItem);
        cartRepository.save(cart);
        return CartMapper.toResponse(cart);
    }
}