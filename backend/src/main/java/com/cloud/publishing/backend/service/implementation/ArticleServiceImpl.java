package com.cloud.publishing.backend.service.implementation;

import static com.cloud.publishing.common.constants.article.ArticleMessage.ACCESS_DENIED_ERROR;
import static com.cloud.publishing.common.constants.article.ArticleMessage.ARTICLE_DELETE_PUBLISHED_ERROR;
import static com.cloud.publishing.common.constants.article.ArticleMessage.ARTICLE_HAS_REVIEWS_ERROR;
import static com.cloud.publishing.common.constants.article.ArticleMessage.ARTICLE_UPDATE_PUBLISHED_ERROR;
import static com.cloud.publishing.common.constants.article.ArticleMessage.INVALID_CO_AUTHORS_ERROR;
import static com.cloud.publishing.common.constants.article.ArticleMessage.SELF_CO_AUTHOR_ERROR;

import com.cloud.publishing.backend.mapper.ArticleMapper;
import com.cloud.publishing.backend.repository.ArticleRepository;
import com.cloud.publishing.backend.security.UserPrincipal;
import com.cloud.publishing.backend.service.ArticleService;
import com.cloud.publishing.backend.service.EmployeeService;
import com.cloud.publishing.backend.service.PublicationService;
import com.cloud.publishing.backend.service.ReviewService;
import com.cloud.publishing.backend.security.SecurityUtils;
import com.cloud.publishing.common.dto.ArticleDTO;
import com.cloud.publishing.common.dto.response.EmployeeShort;
import com.cloud.publishing.model.article.Article;
import com.cloud.publishing.model.article.ArticleShort;
import com.cloud.publishing.model.employee.Type;
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
    private final EmployeeService employeeService;

    @Autowired
    public ArticleServiceImpl(
            ArticleRepository repository,
            ArticleMapper mapper,
            PublicationService publicationService,
            ReviewService reviewService,
            EmployeeService employeeService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.publicationService = publicationService;
        this.reviewService = reviewService;
        this.employeeService = employeeService;
    }

    @Override
    public Article add(ArticleDTO newArticle) {
        UserPrincipal user = SecurityUtils.currentUser();
        validateCoAuthors(newArticle, user.id());
        return repository.add(mapper.toEntity(newArticle, user.id()));
    }

    @Override
    public Article update(int id, ArticleDTO articleUpdate) {
        UserPrincipal user = SecurityUtils.currentUser();
        if (reviewService.isArticlePublished(id)) {
            throw new IllegalStateException(ARTICLE_UPDATE_PUBLISHED_ERROR);
        }
        Article existingArticle = repository.get(id);
        if (!existingArticle.authorId().equals(user.id())) {
            throw new AccessDeniedException(ACCESS_DENIED_ERROR);
        }
        validateCoAuthors(articleUpdate, existingArticle.authorId());
        Article updatedArticle = new Article(
                existingArticle.id(),
                articleUpdate.publicationId(),
                articleUpdate.categoryId(),
                articleUpdate.name(),
                articleUpdate.content(),
                existingArticle.authorId(),
                articleUpdate.coAuthorsIds()
        );
        repository.update(updatedArticle);
        return updatedArticle;
    }

    @Override
    public Article get(int id) {
        UserPrincipal user = SecurityUtils.currentUser();
        if (SecurityUtils.isChiefEditor() || reviewService.isArticlePublished(id)) {
            return repository.get(id);
        }
        Article article = repository.get(id);
        if (article.authorId().equals(user.id())) {
            return article;
        }
        throw new AccessDeniedException(ACCESS_DENIED_ERROR);
    }

    @Override
    public List<ArticleShort> getAll() {
        UserPrincipal user = SecurityUtils.currentUser();
        if (SecurityUtils.isChiefEditor()) {
            return repository.getAll();
        } else {
            return repository.getByAuthorId(user.id());
        }
    }

    @Override
    public void delete(int id) {
        UserPrincipal user = SecurityUtils.currentUser();
        if (reviewService.isArticlePublished(id)) {
            throw new IllegalStateException(ARTICLE_DELETE_PUBLISHED_ERROR);
        }
        if (reviewService.hasReviewsForArticle(id)) {
            throw new IllegalStateException(ARTICLE_HAS_REVIEWS_ERROR);
        }
        Article article = repository.get(id);
        if (!article.authorId().equals(user.id())) {
            throw new AccessDeniedException(ACCESS_DENIED_ERROR);
        }
        repository.delete(id);
    }

    @Override
    public List<EmployeeShort> getCoAuthors(int publicationId) {
        UserPrincipal user = SecurityUtils.currentUser();
        Publication publication = publicationService.get(publicationId);
        return employeeService.getAll().stream()
                .filter(e -> e.type() == Type.JOURNALIST)
                .filter(e -> publication.journalists().contains(e.id()))
                .filter(e -> !e.id().equals(user.id()))
                .map(e -> new EmployeeShort(
                        e.id(),
                        e.firstName(),
                        e.lastName(),
                        e.middleName()
                ))
                .toList();
    }

    private void validateCoAuthors(ArticleDTO article, Integer currentUserId) {
        if (article.coAuthorsIds() == null) {
            return;
        }
        if (article.coAuthorsIds().contains(currentUserId)) {
            throw new IllegalArgumentException(SELF_CO_AUTHOR_ERROR);
        }
        Publication publication = publicationService.get(article.publicationId());
        if (!publication.journalists().containsAll(article.coAuthorsIds())) {
            throw new IllegalArgumentException(INVALID_CO_AUTHORS_ERROR);
        }
    }
}