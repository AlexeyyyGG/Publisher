package com.cloud.publishing.backend.repository;

import com.cloud.publishing.model.publication.Category;
import java.util.List;
import java.util.Set;

public interface CategoryRepository {
    List<Category> getAll();

    List<Category> getById(Set<Integer> ids);
}