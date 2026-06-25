package com.cloud.publishing.backend.service.implementation;

import com.cloud.publishing.backend.repository.implementation.CategoryRepositoryImpl;
import com.cloud.publishing.backend.service.CategoryService;
import com.cloud.publishing.model.publication.Category;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepositoryImpl categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepositoryImpl categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.getAll();
    }

    @Override
    public List<Category> getById(Set<Integer> ids) {
        return categoryRepository.getById(ids);
    }
}