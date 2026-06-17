package com.cloud.publishing.backend.repository;

import com.cloud.publishing.common.dto.response.ReviewDetailsDTO;
import java.util.List;

public interface ReviewRepository {
    List<ReviewDetailsDTO> getByArticleId(int articleId);

    boolean checkArticleStatus(int articleId, String sql, String errorMessage);
}