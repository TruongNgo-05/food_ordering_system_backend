package com.example.project_back.service;

import com.example.project_back.dto.request.admin.CategoriesCreateAndUpdate;
import com.example.project_back.dto.request.spec.CategoriesRequestParam;
import com.example.project_back.dto.response.user.CategoriesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoriesService {
    Page<CategoriesResponse> getCategories(CategoriesRequestParam param, Pageable pageable);

    CategoriesResponse getCategoryById(Integer id);

    CategoriesResponse createCategories(CategoriesCreateAndUpdate create);

    CategoriesResponse updateCategories(CategoriesCreateAndUpdate update, Integer id);

    String deleteCategories(Integer id);
}
