package com.cloud.publishing.backend.service;

import com.cloud.publishing.common.dto.request.PublicationRequest;
import com.cloud.publishing.model.publication.Publication;
import java.util.List;
import java.util.Set;

/**
 * Service interface for managing publications. Provides operations for creating, updating,
 * retrieving and deleting publications.
 */
public interface PublicationService {
    /**
     * Creates a new publication.
     *
     * @param request {@link PublicationRequest} containing publication data
     * @return created {@link Publication}
     */
    Publication add(PublicationRequest request);

    /**
     * Updates an existing publication.
     *
     * @param id      identifier of the publication to update
     * @param request {@link PublicationRequest} containing updated publication data
     * @return updated {@link Publication}
     */
    Publication update(int id, PublicationRequest request);

    /**
     * Returns publication by id.
     *
     * @param id identifier of the publication
     * @return {@link Publication}
     */
    Publication get(int id);

    /**
     * Returns list of all publications.
     *
     * @return list of {@link Publication}
     */
    List<Publication> getAll();

    /**
     * Deletes publication by id.
     *
     * @param id identifier of the publication to delete
     */
    void delete(int id);

    /**
     * Returns list of publications for specific identifiers.
     *
     * @param ids set of publication identifiers
     * @return list of {@link Publication}
     */
    List<Publication> getByIds(Set<Integer> ids);
}