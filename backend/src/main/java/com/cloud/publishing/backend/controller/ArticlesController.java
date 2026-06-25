package com.cloud.publishing.backend.controller;

import static com.cloud.publishing.backend.security.SecurityConstants.ROLE_CHIEF_EDITOR;

import com.cloud.publishing.backend.mapper.ArticleMapper;
import com.cloud.publishing.backend.security.UserPrincipal;
import com.cloud.publishing.backend.service.ArticleService;
import com.cloud.publishing.backend.service.CategoryService;
import com.cloud.publishing.backend.service.EmployeeService;
import com.cloud.publishing.backend.service.PublicationService;
import com.cloud.publishing.backend.service.ReviewService;
import com.cloud.publishing.common.constants.Parameters;
import com.cloud.publishing.common.constants.Urls;
import com.cloud.publishing.common.dto.ArticleDTO;
import com.cloud.publishing.common.dto.response.ArticleGetAllDTO;
import com.cloud.publishing.common.dto.response.EmployeeShort;
import com.cloud.publishing.model.Article;
import com.cloud.publishing.model.publication.Category;
import com.cloud.publishing.model.publication.Publication;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Urls.ARTICLES)
public class ArticlesController {
    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final PublicationService publicationService;
    private final EmployeeService employeeService;
    private final ReviewService reviewService;
    private final ArticleMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(ArticlesController.class);

    @Autowired
    public ArticlesController(
            ArticleService articleService,
            CategoryService categoryService,
            PublicationService publicationService,
            EmployeeService employeeService,
            ReviewService reviewService,
            ArticleMapper mapper
    ) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.publicationService = publicationService;
        this.employeeService = employeeService;
        this.reviewService = reviewService;
        this.mapper = mapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('CHIEF_EDITOR', 'JOURNALIST')")
    public ResponseEntity<List<ArticleGetAllDTO>> getAll(
            @AuthenticationPrincipal UserPrincipal user,
            Authentication authentication
    ) {
        logger.info("getAll called");
        boolean isChiefEditor = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(ROLE_CHIEF_EDITOR));
        List<Article> articles = articleService.getAll(user.id(), isChiefEditor);
        Set<Integer> publicationIds = articles.stream()
                .map(Article::publicationId)
                .collect(Collectors.toSet());
        Set<Integer> categoryIds = articles.stream()
                .map(Article::categoryId)
                .collect(Collectors.toSet());
        Set<Integer> employeeIds = articles.stream()
                .flatMap(article -> {
                    Stream.Builder<Integer> builder = Stream.builder();
                    if (isChiefEditor) {
                        builder.add(article.authorId());
                    }
                    if (article.coAuthorsIds() != null) {
                        article.coAuthorsIds().forEach(builder::add);
                    }
                    return builder.build();
                })
                .collect(Collectors.toSet());
        List<EmployeeShort> employees = employeeService.getByIds(employeeIds);
        List<Publication> publications = publicationService.getById(publicationIds);
        List<Category> categories = categoryService.getById(categoryIds);
        List<ArticleGetAllDTO> dtos = articles.stream()
                .map(article -> assembleArticleGetAllDTO(
                        article,
                        publications,
                        categories,
                        employees,
                        isChiefEditor
                ))
                .toList();
        logger.debug("Found {} articles", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = Urls.ID, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('CHIEF_EDITOR', 'JOURNALIST')")
    public ResponseEntity<ArticleDTO> get(
            @PathVariable(Parameters.ID) int id,
            @AuthenticationPrincipal UserPrincipal user,
            Authentication authentication
    ) {
        logger.info("get called with id={}", id);
        boolean isChiefEditor = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(ROLE_CHIEF_EDITOR));
        Article article = articleService.get(id, user.id(), isChiefEditor);
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toResponse(article));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('JOURNALIST')")
    public ResponseEntity<ArticleDTO> add(
            @Valid @RequestBody ArticleDTO request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        logger.info("add called with: {}", request);
        Article article = articleService.add(request, user.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(article));
    }

    @PutMapping(value = Urls.ID, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('JOURNALIST')")
    public ResponseEntity<ArticleDTO> update(
            @PathVariable(Parameters.ID) int id,
            @Valid @RequestBody ArticleDTO request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        logger.info("update called for id {}", id);
        Article article = articleService.update(id, request, user.id());
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toResponse(article));
    }

    @DeleteMapping(value = {Urls.ID})
    @PreAuthorize("hasRole('JOURNALIST')")
    public ResponseEntity<Void> delete(
            @PathVariable(Parameters.ID) int id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        logger.info("delete called with id {}", id);
        articleService.delete(id, user.id());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private ArticleGetAllDTO assembleArticleGetAllDTO(
            Article article,
            List<Publication> publications,
            List<Category> categories,
            List<EmployeeShort> employees,
            boolean isChiefEditor
    ) {
        Publication publication = publications.stream()
                .filter(p -> p.id().equals(article.publicationId()))
                .findFirst()
                .orElseThrow();
        Category category = categories.stream()
                .filter(c -> c.id().equals(article.categoryId()))
                .findFirst()
                .orElseThrow();
        String author = null;
        if (isChiefEditor) {
            author = employees.stream()
                    .filter(e -> e.id().equals(article.authorId()))
                    .map(EmployeeShort::getShortName)
                    .findFirst()
                    .orElseThrow();
        }
        List<String> coAuthors = employees.stream()
                .filter(e -> article.coAuthorsIds() != null && article.coAuthorsIds()
                        .contains(e.id()))
                .map(EmployeeShort::getShortName)
                .toList();
        boolean published = reviewService.isArticlePublished(article.id());
        boolean hasReviews = reviewService.hasReviewsForArticle(article.id());
        return mapper.toGetDTO(
                article,
                publication,
                category,
                author,
                coAuthors,
                published,
                hasReviews
        );
    }
}