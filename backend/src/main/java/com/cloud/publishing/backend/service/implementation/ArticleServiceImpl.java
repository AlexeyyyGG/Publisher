package com.cloud.publishing.backend.service.implementation;

import static com.cloud.publishing.common.constants.article.ArticleMessage.ACCESS_DENIED_ERROR;
import static com.cloud.publishing.common.constants.article.ArticleMessage.ARTICLE_DELETE_PUBLISHED_ERROR;
import static com.cloud.publishing.common.constants.article.ArticleMessage.ARTICLE_HAS_REVIEWS_ERROR;
import static com.cloud.publishing.common.constants.article.ArticleMessage.ARTICLE_UPDATE_PUBLISHED_ERROR;
import static com.cloud.publishing.common.constants.article.ArticleMessage.INVALID_CO_AUTHORS_ERROR;
import static com.cloud.publishing.common.constants.article.ArticleMessage.SELF_CO_AUTHOR_ERROR;

import com.cloud.publishing.backend.mapper.ArticleMapper;
import com.cloud.publishing.backend.repository.ArticleRepository;
import com.cloud.publishing.backend.service.ArticleService;
import com.cloud.publishing.backend.service.PublicationService;
import com.cloud.publishing.backend.service.ReviewService;
import com.cloud.publishing.common.dto.ArticleDTO;
import com.cloud.publishing.model.Article;
import com.cloud.publishing.model.publication.Publication;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository repository;
    private final ArticleMapper mapper;
    private final PublicationService publicationService;
    private final ReviewService reviewService;

    @Autowired
    public ArticleServiceImpl(
            ArticleRepository repository,
            ArticleMapper mapper,
            PublicationService publicationService,
            ReviewService reviewService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.publicationService = publicationService;
        this.reviewService = reviewService;
    }

    @Override
    public Article add(ArticleDTO request, Integer currentUserId) {
        validateCoAuthors(request, currentUserId);
        return repository.add(mapper.toEntity(request, currentUserId));
    }

    @Override
    public Article update(int id, ArticleDTO request, Integer currentUserId) {
        Article existing = repository.get(id);
        if (!existing.authorId().equals(currentUserId)) {
            throw new AccessDeniedException(ACCESS_DENIED_ERROR);
        }
        if (reviewService.isArticlePublished(id)) {
            throw new IllegalStateException(ARTICLE_UPDATE_PUBLISHED_ERROR);
        }
        validateCoAuthors(request, existing.authorId());
        Article updated = new Article(
                existing.id(),
                request.publicationId(),
                request.categoryId(),
                request.name(),
                request.content(),
                existing.authorId(),
                request.coAuthorsIds()
        );
        repository.update(updated);
        return updated;
    }

    @Override
    public Article get(int id, Integer currentUserId, boolean isChiefEditor) {
        Article article = repository.get(id);
        if (isChiefEditor) {
            return article;
        }
        if (article.authorId().equals(currentUserId)) {
            return article;
        }
        if (reviewService.isArticlePublished(id)) {
            return article;
        }
        throw new AccessDeniedException(ACCESS_DENIED_ERROR);
    }

    @Override
    public List<Article> getAll(Integer currentUserId, boolean isChiefEditor) {
        if (isChiefEditor) {
            return repository.getAll();
        } else {
            return repository.getByAuthorId(currentUserId);
        }
    }

    @Override
    public void delete(int id, Integer currentUserId) {
        Article article = repository.get(id);
        if (!article.authorId().equals(currentUserId)) {
            throw new AccessDeniedException(ACCESS_DENIED_ERROR);
        }
        if (reviewService.isArticlePublished(id)) {
            throw new IllegalStateException(ARTICLE_DELETE_PUBLISHED_ERROR);
        }
        if (reviewService.hasReviewsForArticle(id)) {
            throw new IllegalStateException(ARTICLE_HAS_REVIEWS_ERROR);
        }
        repository.delete(id);
    }

    private void validateCoAuthors(ArticleDTO request, Integer currentUserId) {
        if (request.coAuthorsIds() == null) {
            return;
        }
        if (request.coAuthorsIds().contains(currentUserId)) {
            throw new IllegalArgumentException(SELF_CO_AUTHOR_ERROR);
        }
        Publication publication = publicationService.get(request.publicationId());
        if (!publication.journalists().containsAll(request.coAuthorsIds())) {
            throw new IllegalArgumentException(INVALID_CO_AUTHORS_ERROR);
        }
    }
}