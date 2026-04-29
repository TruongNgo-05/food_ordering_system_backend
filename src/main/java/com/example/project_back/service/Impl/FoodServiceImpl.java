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
    import com.example.project_back.repository.FoodImageRepository;
    import com.example.project_back.repository.FoodRepository;
    import com.example.project_back.repository.ReviewRepository;
    import com.example.project_back.service.FileService;
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
        private final FoodImageRepository foodImageRepository;
        private final FileService fileService;

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
                            Double rating = reviewRepository.getAverageRatingByFoodId(food.getId());
                            res.setRating(rating != null ? rating : 0);
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

            response.setImages(FoodImageMapper.toResponseList(food.getImages()));

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

            // ===== MAIN IMAGE =====
            if (image != null && !image.isEmpty()) {
                food.setImage(fileService.uploadFile(image));
            } else if (create.getImageUrl() != null) {
                food.setImage(create.getImageUrl());
            }

            // ===== GALLERY IMAGES =====
            List<FoodImage> imageList = new ArrayList<>();

            // FILE
            if (images != null) {
                for (MultipartFile file : images) {
                    if (file.isEmpty()) continue;

                    FoodImage img = new FoodImage();
                    img.setImageUrl(fileService.uploadFile(file));
                    img.setFood(food);
                    imageList.add(img);
                }
            }

            // URL
            if (create.getImageUrls() != null) {
                for (String url : create.getImageUrls()) {
                    FoodImage img = new FoodImage();
                    img.setImageUrl(url);
                    img.setFood(food);


                    imageList.add(img);
                }
            }
            if (!imageList.isEmpty()) {
                food.setImages(imageList);
            }

            Food saved = foodRepository.save(food);

            return FoodMapper.toMapperAdmin(saved);
        }
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

            // ===== BASIC =====
            FoodMapper.toUpdate(update, food);

            // ===== CATEGORY =====
            if (update.getCategoryId() != null) {
                Categories category = categoriesRepository.findById(update.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
                food.setCategories(category);
            }

            // ================= MAIN IMAGE =================

            // ❌ XÓA ẢNH
            if (Boolean.TRUE.equals(update.getRemoveImage())) {
                if (food.getImage() != null) {
                    fileService.deleteFile(food.getImage());
                }
                food.setImage(null);
            }

            // 📤 UPLOAD FILE
            else if (image != null && !image.isEmpty()) {
                if (food.getImage() != null) {
                    fileService.deleteFile(food.getImage());
                }
                food.setImage(fileService.uploadFile(image));
            }

            // 🔗 SET URL
            else if (update.getImageUrl() != null && !update.getImageUrl().isEmpty()) {
                food.setImage(update.getImageUrl());
            }

            // ================= GALLERY =================

            List<String> newUrls = update.getImageUrls() != null
                    ? update.getImageUrls()
                    : new ArrayList<>();

            if (food.getImages() == null) {
                food.setImages(new ArrayList<>());
            }

            List<FoodImage> currentImages = food.getImages();

            // ===== 1. REMOVE OLD =====
            for (FoodImage oldImg : new ArrayList<>(currentImages)) {

                if (!newUrls.contains(oldImg.getImageUrl())) {

                    fileService.deleteFile(oldImg.getImageUrl());
                    foodImageRepository.delete(oldImg);
                    currentImages.remove(oldImg);
                }
            }

            // ===== 2. ADD FILE =====
            if (images != null) {
                for (MultipartFile file : images) {
                    if (file.isEmpty()) continue;

                    String url = fileService.uploadFile(file);

                    FoodImage img = new FoodImage();
                    img.setImageUrl(url);
                    img.setFood(food);

                    currentImages.add(img);
                }
            }

            // ===== 3. ADD URL =====
            for (String url : newUrls) {

                if (url == null || url.isEmpty()) continue;

                boolean exists = currentImages.stream()
                        .anyMatch(img -> img.getImageUrl().equals(url));

                if (!exists) {
                    FoodImage img = new FoodImage();
                    img.setImageUrl(url);
                    img.setFood(food);

                    currentImages.add(img);
                }
            }

            food.setImages(currentImages);

            // ===== SAVE =====
            Food saved = foodRepository.save(food);

            return FoodMapper.toMapperAdmin(saved);
        }

        @Transactional
        @Override
        public String deleteFood(Long id){
            Food food = foodRepository.findById(id)
                    .orElseThrow(() -> new ApplicationException("Không tìm thấy món ăn"));

            // ===== DELETE MAIN IMAGE =====
            if (food.getImage() != null) {
                fileService.deleteFile(food.getImage());
            }

            // ===== DELETE GALLERY IMAGES =====
            if (food.getImages() != null && !food.getImages().isEmpty()) {
                List<String> urls = new ArrayList<>();

                for (FoodImage img : food.getImages()) {
                    urls.add(img.getImageUrl());
                }

                // xóa file thật
                fileService.deleteFiles(urls);

                // xóa DB
                foodImageRepository.deleteByFoodId(id);
            }

            // ===== DELETE FOOD =====
            foodRepository.delete(food);

            return "delete successfully";
        }

    }
