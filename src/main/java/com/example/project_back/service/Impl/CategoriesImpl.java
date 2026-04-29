package com.example.project_back.service.Impl;

import com.example.project_back.dto.request.admin.CategoriesCreateAndUpdate;
import com.example.project_back.dto.request.spec.CategoriesRequestParam;
import com.example.project_back.dto.response.user.CategoriesResponse;
import com.example.project_back.entity.Categories;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.CategoriesMapper;
import com.example.project_back.repository.CategoriesRepository;
import com.example.project_back.service.CategoriesService;
import com.example.project_back.specification.CategoriesSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoriesImpl implements CategoriesService {
    private final CategoriesRepository  categoriesRepository;

@Override
public Page<CategoriesResponse> getCategories(CategoriesRequestParam param , Pageable pageable){

    String name = param.getName();

    Specification<Categories> spec = Specification.unrestricted();

    if(param.getName()!=null  && !name.trim().isEmpty() ){
        spec=spec.and(CategoriesSpecification.hasCategoriesName(name));
    }
    return categoriesRepository.findAll(spec,pageable).map(CategoriesMapper::toResponse);
}

@Override
public CategoriesResponse getCategoryById(Integer id){
    Optional<Categories> categories = categoriesRepository.findById(id);
    if(categories.isEmpty()){
       throw new ApplicationException("Category not found");
    }
    return CategoriesMapper.toResponse(categories.get());
}
@Transactional
@Override
public CategoriesResponse createCategories(CategoriesCreateAndUpdate create){
    if(categoriesRepository.existsByName(create.getName())){
        throw new ApplicationException("Category already exists");
    }
    Categories categories = CategoriesMapper.toEntity(create);
    Categories Saved = categoriesRepository.save(categories);
    return CategoriesMapper.toResponse(Saved);
}

@Transactional
@Override
public CategoriesResponse updateCategories(CategoriesCreateAndUpdate update, Integer id){
    Optional<Categories> categories = categoriesRepository.findById(id);
    if(categories.isEmpty()){
        throw new ApplicationException("Category does not exist");
    }
    if (categoriesRepository.existsByNameAndIdNot(update.getName(),id)){
        throw new ApplicationException("Category already exists");
    }
    Categories category = categories.get();
    CategoriesMapper.updateEntity(update,category);
    return CategoriesMapper.toResponse(categoriesRepository.save(category));
}

@Transactional
@Override
public String deleteCategories(Integer id){
    Optional<Categories> categories = categoriesRepository.findById(id);
    if(categories.isEmpty()){
        throw new ApplicationException("Category does not exist");
    }
    categoriesRepository.deleteById(id);
    return "Deleted";
}
}
