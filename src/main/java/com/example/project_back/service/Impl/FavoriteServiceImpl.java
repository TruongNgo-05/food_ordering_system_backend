package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.dto.response.customer.FavoriteResponse;
import com.example.project_back.dto.response.user.FoodResponse;
import com.example.project_back.entity.Favorite;
import com.example.project_back.entity.Food;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.repository.FavoriteRepository;
import com.example.project_back.repository.FoodRepository;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.FavoriteService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private FavoriteRepository favoriteRepository;
    private UserRepository userRepository;
    private FoodRepository foodRepository;

    @Override
    public FavoriteResponse getMyFavorite() {

        String username = SecurityUtils.getCurrentUsername();


        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new ApplicationException("User không tồn tại");
        }

        User user = userOpt.get();

        //  Lấy danh sách favorite của user
        List<Favorite> list = favoriteRepository.findByUser_Id(user.getId());

        // Tạo danh sách ID food
        List<Long> ids = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Favorite f = list.get(i);
            ids.add(f.getFood().getId());
        }

        FavoriteResponse res = new FavoriteResponse();
        res.setFavoriteIds(ids);
        return res;
    }
    @Transactional
    @Override
    public String toggleFavorite(Long foodId) {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new ApplicationException("User không tồn tại");
        }
        User user = userOpt.get();

        //  Kiểm tra đã favorite chưa
        Optional<Favorite> exist = favoriteRepository.findByUser_IdAndFood_Id(user.getId(), foodId);

        //  Nếu đã tồn tại thì xóa
        if (exist.isPresent()) {
            favoriteRepository.delete(exist.get());
            return "đã hủy yêu thích";
        }

        // Nếu chưa thì  thêm mới
        Optional<Food> foodOpt = foodRepository.findById(foodId);
        if (foodOpt.isEmpty()) {
            throw new ApplicationException("Food không tồn tại");
        }

        Favorite fav = new Favorite();
        fav.setUser(user);
        fav.setFood(foodOpt.get());

        favoriteRepository.save(fav);

        return "đã thêm yêu thích";
    }
}
