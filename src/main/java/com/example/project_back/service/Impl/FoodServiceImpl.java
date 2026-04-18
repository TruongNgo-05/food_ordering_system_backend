    package com.example.project_back.service.Impl;

    import com.example.project_back.dto.request.admin.FoodCreateAndUpdateRequest;
    import com.example.project_back.dto.request.spec.FoodRequestParam;
    import com.example.project_back.dto.response.admin.FoodAdminResponse;
    import com.example.project_back.dto.response.user.FoodResponse;
    import com.example.project_back.entity.Categories;
    import com.example.project_back.entity.Food;
    import com.example.project_back.exception.ApplicationException;
    import com.example.project_back.mapper.FoodMapper;
    import com.example.project_back.repository.CategoriesRepository;
    import com.example.project_back.repository.FoodRepository;
    import com.example.project_back.service.FoodService;
    import com.example.project_back.specification.FoodSpecification;
    import jakarta.transaction.Transactional;
    import lombok.AllArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.domain.Specification;
    import org.springframework.stereotype.Service;

    import java.util.Optional;


    @Service
    @AllArgsConstructor
    public class FoodServiceImpl implements FoodService {

        private final FoodRepository foodRepository;
        private final CategoriesRepository categoriesRepository;

        @Override
        public Page<FoodResponse> getAllFoodCustomer(FoodRequestParam param, Pageable pageable){
                String name = param.getName();
                Double minPrice =  param.getMinPrice();
                Double maxPrice =  param.getMaxPrice();
                Double minRating =  param.getMinRating();
                Double maxRating =  param.getMaxRating();
                String categories = param.getCategories();

            Specification<Food> spec =Specification.unrestricted();
            if(name!=null && !name.isEmpty()){
                spec=spec.and(FoodSpecification.hasName(name));
            }
            if(minPrice != null && maxPrice != null){
                spec=spec.and(FoodSpecification.hasPrice(minPrice, maxPrice));
            }
            if(minRating != null && maxRating != null){
                spec=spec.and(FoodSpecification.hasRating(minRating, maxRating));
            }
            if(categories != null){
                spec=spec.and(FoodSpecification.hasCategories(categories));
            }
            return foodRepository.findAll(spec,pageable).map(FoodMapper::toMapperCustomer);
        }

        @Override
        public Page<FoodAdminResponse> getAllFoodAdmin(FoodRequestParam param, Pageable pageable){
            String name = param.getName();
            Double minPrice =  param.getMinPrice();
            Double maxPrice =  param.getMaxPrice();
            Double minRating =  param.getMinRating();
            Double maxRating =  param.getMaxRating();
            String categories = param.getCategories();

            Specification<Food> spec =Specification.unrestricted();
            if(name!=null && !name.isEmpty()){
                spec=spec.and(FoodSpecification.hasName(name));
            }
            if(minPrice != null && maxPrice != null){
                spec=spec.and(FoodSpecification.hasPrice(minPrice, maxPrice));
            }
            if(minRating != null && maxRating != null){
                spec=spec.and(FoodSpecification.hasRating(minRating, maxRating));
            }
            if(categories != null){
                spec=spec.and(FoodSpecification.hasCategories(categories));
            }
            return foodRepository.findAll(spec,pageable).map(FoodMapper::toMapperAdmin);
        }

        @Override
        public FoodAdminResponse getById(Long id){
            Optional<Food> food = foodRepository.findById(id);
            if(food.isEmpty()){
                throw new ApplicationException(" K Tim thay id ");
            }
            return FoodMapper.toMapperAdmin(food.get());
        }

        @Transactional
        @Override
        public FoodAdminResponse createFood(FoodCreateAndUpdateRequest create){
            Food food = FoodMapper.toCreate(create);
            if(create.getCategoryId() != null){
                Optional<Categories> category = categoriesRepository.findById(create.getCategoryId());
                if(category.isEmpty()){
                    throw new ApplicationException("k tim thay id category");
                }
                food.setCategories(category.get());
            }
            Food SavedFood = foodRepository.save(food);
            return FoodMapper.toMapperAdmin(SavedFood);
        }

        @Transactional
        @Override
        public FoodAdminResponse updateFood(FoodCreateAndUpdateRequest update, Long id){
            Optional<Food> foods = foodRepository.findById(id);
            if(foods.isEmpty()){
                throw new ApplicationException("k tim thay id");
            }
            Food food = foods.get();
            FoodMapper.toUpdate(update,food);
            if(update.getCategoryId() != null){
                Optional<Categories> category = categoriesRepository.findById(update.getCategoryId());
                if(category.isEmpty()){
                    throw new ApplicationException("k tim thay id category");
                }
                food.setCategories(category.get());
            }
            return FoodMapper.toMapperAdmin(foodRepository.save(food));
        }

        @Transactional
        @Override
        public String deleteFood(Long id){
            Optional<Food> foods = foodRepository.findById(id);
            if(foods.isEmpty()){
                throw new ApplicationException("k tim thay id");
            }
            foodRepository.deleteById(id);
            return "delete successfully";
        }
    }
