package com.cloud.publishing.backend.service;

import com.cloud.publishing.model.publication.Category;
import java.util.List;
import java.util.Set;

/**
 * Service for accessing category reference data.
 * <p>
 * Category entities are predefined and stored in the database. They are not editable.
 */
public interface CategoryService {
    /**
     * Returns all available entries.
     *
     * @return list of {@link Category}
     */
    List<Category> getAll();

    /**
     * Returns list of categories for specific identifiers.
     *
     * @param ids set of category identifiers
     * @return list of {@link Category}
     */
    List<Category> getById(Set<Integer> ids);
}