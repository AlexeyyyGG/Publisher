package com.cloud.publishing.backend.repository;

import com.cloud.publishing.model.publication.Publication;
import java.util.List;
import java.util.Set;

public interface PublicationRepository extends IRepository<Publication, Integer> {
    List<Publication> getAll();

    List<Publication> getByIds(Set<Integer> ids);
}