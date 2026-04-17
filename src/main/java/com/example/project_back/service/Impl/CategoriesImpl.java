package com.example.project_back.service.Impl;

import com.example.project_back.dto.request.admin.CategoriesCreateAndUpdate;
import com.example.project_back.dto.response.user.CategoriesResponse;
import com.example.project_back.entity.Categories;
import com.example.project_back.mapper.CategoriesMapper;
import com.example.project_back.repository.CategoriesRepository;
import com.example.project_back.service.CategoriesService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoriesImpl implements CategoriesService {
    private final CategoriesRepository  categoriesRepository;

@Override
public Page<CategoriesResponse> getCategories(Pageable pageable){
    Page<Categories> categories = categoriesRepository.findAll(pageable);
    return categories.map(CategoriesMapper::toResponse);
}

@Override
public CategoriesResponse createCategories(CategoriesCreateAndUpdate create){
    if(categoriesRepository.existsByName(create.getName())){
        throw new RuntimeException("Category already exists");
    }
    Categories categories = CategoriesMapper.toEntity(create);
    Categories Saved = categoriesRepository.save(categories);
    CategoriesResponse categoriesResponse = CategoriesMapper.toResponse(Saved);
    return categoriesResponse;
}

@Override
public CategoriesResponse updateCategories(CategoriesCreateAndUpdate update, Integer id){
    Optional<Categories> categorie = categoriesRepository.findById(id);
    if(categorie.isEmpty()){
        throw new RuntimeException("Category does not exist");
    }
    if (categoriesRepository.existsByNameAndIdNot(update.getName(),id)){
        throw new RuntimeException("Category already exists");
    }
    Categories categories = categorie.get();
    CategoriesMapper.updateEntity(update,categories);
    return  CategoriesMapper.toResponse(categoriesRepository.save(categories));
}

@Override
public String deleteCategories(Integer id){
    Optional<Categories> categorie = categoriesRepository.findById(id);
    if(categorie.isEmpty()){
        throw new RuntimeException("Category does not exist");
    }
    categoriesRepository.deleteById(id);
    return "Deleted";
}
}
