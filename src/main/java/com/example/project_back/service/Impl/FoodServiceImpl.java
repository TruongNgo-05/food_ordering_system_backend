    package com.example.project_back.service.Impl;

    import com.example.project_back.dto.request.admin.FoodCreateAndUpdateRequest;
    import com.example.project_back.dto.request.spec.FoodRequestParam;
    import com.example.project_back.dto.response.admin.FoodAdminResponse;
    import com.example.project_back.dto.response.admin.FoodDetailAdminRespone;
    import com.example.project_back.dto.response.user.FoodDetailResponse;
    import com.example.project_back.dto.response.user.FoodOderTableResponse;
    import com.example.project_back.dto.response.user.FoodResponse;
    import com.example.project_back.entity.Categories;
    import com.example.project_back.entity.Food;
    import com.example.project_back.entity.FoodImage;
    import com.example.project_back.exception.ApplicationException;
    import com.example.project_back.mapper.FoodImageMapper;
    import com.example.project_back.mapper.FoodMapper;
    import com.example.project_back.repository.CategoriesRepository;
    import com.example.project_back.repository.FoodRepository;
    import com.example.project_back.repository.ReviewRepository;
    import com.example.project_back.service.FoodService;
    import com.example.project_back.specification.FoodSpecification;
    import jakarta.transaction.Transactional;
    import lombok.AllArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.domain.PredicateSpecification;
    import org.springframework.data.jpa.domain.Specification;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;


    @Service
    @AllArgsConstructor
    public class FoodServiceImpl implements FoodService {

        private final FoodRepository foodRepository;
        private final CategoriesRepository categoriesRepository;
        private final ReviewRepository reviewRepository;
        private final UploadService  uploadService;


        @Override
        public Page<FoodResponse> getAllFoodCustomer(
                FoodRequestParam param,
                Pageable pageable
        ) {
            String name = param.getName();
            Double minPrice = param.getMinPrice();
            Double maxPrice = param.getMaxPrice();
            Double minRating = param.getMinRating();
            Double maxRating = param.getMaxRating();
            Integer categories = param.getCategoryId();

            Specification<Food> spec = Specification.where(FoodSpecification.hasStatus(true));

            if (name != null && !name.isEmpty()) {
                spec = spec.and(FoodSpecification.hasName(name));
            }
            if (minPrice != null && maxPrice != null) {
                spec = spec.and(FoodSpecification.hasPrice(minPrice, maxPrice));
            }
            if (minRating != null && maxRating != null) {
                spec = spec.and(FoodSpecification.hasRating(minRating, maxRating));
            }
            if (categories != null) {
                spec = spec.and(FoodSpecification.hasCategoryId(categories));
            }
            return foodRepository.findAll(spec, pageable)
                    .map(food -> {
                        FoodResponse res = FoodMapper.toMapperCustomer(food);
                        res.setRating(reviewRepository.getAverageRatingByFoodId(food.getId()));
                        return res;
                    });
        }

        Page<FoodOderTableResponse> getAllFoodToTable(Pageable pageable) {
            Page<Food>  foods = foodRepository.findAll(pageable);
            return foods.map(FoodMapper::toMapTable);
        }

        @Override
        public Page<FoodAdminResponse> getAllFoodAdmin(FoodRequestParam param, Pageable pageable){
            String name = param.getName();
            Double minPrice =  param.getMinPrice();
            Double maxPrice =  param.getMaxPrice();
            Double minRating =  param.getMinRating();
            Double maxRating =  param.getMaxRating();
            Integer categories = param.getCategoryId();

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
                spec=spec.and( FoodSpecification.hasCategoryId(categories));
            }
            return foodRepository.findAll(spec,pageable).map(FoodMapper::toMapperAdmin);
        }

        @Override
        public FoodDetailAdminRespone getById(Long id){
            Optional<Food> foods = foodRepository.findById(id);
            if(foods.isEmpty()){
                throw new ApplicationException(" Không tìm thấy food");
            }
            Food food = foods.get();
            FoodDetailAdminRespone response = FoodMapper.toMapperAdminDetail(food);

            response.setImages(FoodImageMapper.toUrlList(food.getImages()));

            response.setRating(reviewRepository.getAverageRatingByFoodId(id));
            return response;
        }

        @Override
        public FoodDetailResponse getFoodDetail(Long id){
           Optional<Food> foods = foodRepository.findById(id);
           if(foods.isEmpty()){
               throw new ApplicationException("Không tìm thấy food");
           }
           Food food = foods.get();
            FoodDetailResponse response = FoodMapper.toMapperDetail(food);

            //set categories
            if (food.getCategories() != null) {
                response.setCategoryName(food.getCategories().getName());
            }
            // set images
            if (food.getImages() != null && !food.getImages().isEmpty()) {
                List<String> images = new ArrayList<>();
                for (FoodImage img : food.getImages()) {
                    images.add(img.getImageUrl());
                }
                response.setImages(images);
            }
            response.setRating(reviewRepository.getAverageRatingByFoodId(id));
            response.setReviewCount(reviewRepository.countByFoodId(id));
            return response;
        }

//        @Transactional
//        @Override
//        public FoodAdminResponse createFood(FoodCreateAndUpdateRequest create){
//            Food food = FoodMapper.toCreate(create);
//            if(create.getCategoryId() != null){
//                Optional<Categories> category = categoriesRepository.findById(create.getCategoryId());
//                if(category.isEmpty()){
//                    throw new ApplicationException("Không tìm thấy Danh mục");
//                }
//                food.setCategories(category.get());
//            }
//
//            if (create.getImages() != null && !create.getImages().isEmpty()) {
//                food.setImages(
//                        FoodImageMapper.toEntityList(create.getImages(), food)
//                );
//            }
//            Food savedFood = foodRepository.save(food);
//            return FoodMapper.toMapperAdmin(savedFood);
//        }
@Transactional
@Override
public FoodAdminResponse createFood(
        FoodCreateAndUpdateRequest create,
        MultipartFile image,
        List<MultipartFile> images
) {

    Food food = FoodMapper.toCreate(create);

    // CATEGORY
    if (create.getCategoryId() != null) {
        Categories category = categoriesRepository.findById(create.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        food.setCategories(category);
    }

    // ===== MAIN IMAGE (FILE + URL) =====
    if (image != null && !image.isEmpty()) {
        String url = uploadService.saveFile(image);
        food.setImage(url);
    } else if (create.getImageUrl() != null) {
        food.setImage(create.getImageUrl());
    }

    // LIST IMAGES
    if (images != null && !images.isEmpty()) {
        List<FoodImage> imageList = new ArrayList<>();

        for (MultipartFile file : images) {
            if (file.isEmpty()) continue;

            String url = uploadService.saveFile(file);

            FoodImage img = new FoodImage();
            img.setImageUrl(url);
            img.setFood(food);
            img.setIsPrimary(false);

            imageList.add(img);
        }

        // URL LIST
        if (create.getImageUrls() != null) {
            for (String url : create.getImageUrls()) {

                FoodImage img = new FoodImage();
                img.setImageUrl(url);
                img.setFood(food);
                img.setIsPrimary(false);

                imageList.add(img);
            }
        }

        if (!imageList.isEmpty()) {
            food.setImages(imageList);
        }
    }

    Food saved = foodRepository.save(food);

    return FoodMapper.toMapperAdmin(saved);
}
//        @Transactional
//        @Override
//        public FoodAdminResponse updateFood(FoodCreateAndUpdateRequest update, Long id){
//            Optional<Food> foods = foodRepository.findById(id);
//            if(foods.isEmpty()){
//                throw new ApplicationException("Không tìm thấy món ăn");
//            }
//            Food food = foods.get();
//            FoodMapper.toUpdate(update,food);
//            if(update.getCategoryId() != null){
//                Optional<Categories> category = categoriesRepository.findById(update.getCategoryId());
//                if(category.isEmpty()){
//                    throw new ApplicationException("Không tìm thấy danh mục");
//                }
//                food.setCategories(category.get());
//            }
////
////            if (update.getImages() != null) {
////                food.getImages().clear();
////                food.getImages().addAll(
////                        FoodImageMapper.toEntityList(update.getImages(), food)
////                );
////            }
//
//            return FoodMapper.toMapperAdmin(foodRepository.save(food));
//        }

@Transactional
@Override
public FoodAdminResponse updateFood(
        Long id,
        FoodCreateAndUpdateRequest update,
        MultipartFile image,
        List<MultipartFile> images
) {

    Food food = foodRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy food"));

    // BASIC INFO
    FoodMapper.toUpdate(update, food);

    // CATEGORY
    if (update.getCategoryId() != null) {
        Categories category = categoriesRepository.findById(update.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        food.setCategories(category);
    }

    // MAIN IMAGE
    if (image != null && !image.isEmpty()) {
        food.setImage(uploadService.saveFile(image));
    } else if (update.getImageUrl() != null) {
        food.setImage(update.getImageUrl());
    }

    // ===== GALLERY IMAGE REPLACE =====
    if ((images != null && !images.isEmpty()) ||
            (update.getImageUrls() != null && !update.getImageUrls().isEmpty())) {
        food.getImages().clear();
        List<FoodImage> newImages = new ArrayList<>();

        // FILE
        if (images != null) {
            for (MultipartFile file : images) {
                if (file.isEmpty()) continue;

                FoodImage img = new FoodImage();
                img.setImageUrl(uploadService.saveFile(file));
                img.setFood(food);

                newImages.add(img);
            }
        }

        // URL
        if (update.getImageUrls() != null) {
            for (String url : update.getImageUrls()) {

                FoodImage img = new FoodImage();
                img.setImageUrl(url);
                img.setFood(food);

                newImages.add(img);
            }
        }

        // 🔥 ADD ALL (KHÔNG SET NEW LIST)
        food.getImages().addAll(newImages);
    }

    return FoodMapper.toMapperAdmin(foodRepository.save(food));
}

        @Transactional
        @Override
        public String deleteFood(Long id){
            Optional<Food> foods = foodRepository.findById(id);
            if(foods.isEmpty()){
                throw new ApplicationException("Không tìm thấy món ăn ");
            }
            foodRepository.deleteById(id);
            return "delete successfully";
        }
    }
