package com.cloud.publishing.frontend.controller;

import com.cloud.publishing.common.constants.Parameters;
import com.cloud.publishing.common.constants.Urls;
import com.cloud.publishing.common.constants.article.ArticleModelAttrs;
import com.cloud.publishing.common.constants.article.ArticlePages;
import com.cloud.publishing.common.dto.ArticleDTO;
import com.cloud.publishing.common.dto.response.ArticleGetAllDTO;
import com.cloud.publishing.common.dto.response.EmployeeShort;
import com.cloud.publishing.frontend.client.ArticleClient;
import com.cloud.publishing.frontend.client.CategoryClient;
import com.cloud.publishing.frontend.client.PublicationClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("webArticlesController")
@RequestMapping(Urls.WEB_ARTICLES)
public class ArticlesController {
    private final ArticleClient articleClient;
    private final PublicationClient publicationClient;
    private final CategoryClient categoryClient;

    public ArticlesController(
            ArticleClient articleClient,
            PublicationClient publicationClient,
            CategoryClient categoryClient
    ) {
        this.articleClient = articleClient;
        this.publicationClient = publicationClient;
        this.categoryClient = categoryClient;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CHIEF_EDITOR', 'JOURNALIST')")
    public String getAll(HttpServletRequest request, Model model) {
        List<ArticleGetAllDTO> articles = articleClient.getAll();
        model.addAttribute(ArticleModelAttrs.ARTICLES, articles);
        boolean isChiefEditor = request.isUserInRole("ROLE_CHIEF_EDITOR");
        boolean isJournalist = request.isUserInRole("ROLE_JOURNALIST");
        model.addAttribute(ArticleModelAttrs.IS_CHIEF_EDITOR, isChiefEditor);
        model.addAttribute(ArticleModelAttrs.IS_JOURNALIST, isJournalist);
        return ArticlePages.LIST;
    }

    @GetMapping(Urls.NEW)
    @PreAuthorize("hasRole('JOURNALIST')")
    public String showAddForm(Model model) {
        model.addAttribute(ArticleModelAttrs.ARTICLE_REQUEST, ArticleDTO.empty());
        fillCommonModel(model, null);
        return ArticlePages.NEW;
    }

    @GetMapping(Urls.CO_AUTHORS)
    @ResponseBody
    @PreAuthorize("hasRole('JOURNALIST')")
    public List<EmployeeShort> getCoAuthors(
            @RequestParam(Parameters.PUBLICATION_ID) int publicationId
    ) {
        return articleClient.getCoAuthors(publicationId);
    }

    @PostMapping
    @PreAuthorize("hasRole('JOURNALIST')")
    public String add(
            @Valid @ModelAttribute(ArticleModelAttrs.ARTICLE_REQUEST) ArticleDTO request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            fillCommonModel(model, request.publicationId());
            return ArticlePages.NEW;
        }
        articleClient.add(request);
        return ArticlePages.REDIRECT_ARTICLES;
    }

    @GetMapping(Urls.ID + Urls.EDIT)
    @PreAuthorize("hasRole('JOURNALIST')")
    public String showUpdateForm(
            Model model,
            @PathVariable(Parameters.ID) int id
    ) {
        ArticleDTO article = articleClient.get(id);
        model.addAttribute(ArticleModelAttrs.ARTICLE_REQUEST, article);
        model.addAttribute(ArticleModelAttrs.ARTICLE_ID, id);
        fillCommonModel(model, article.publicationId());
        return ArticlePages.EDIT;
    }

    @PostMapping(Urls.ID)
    @PreAuthorize("hasRole('JOURNALIST')")
    public String update(
            @Valid @ModelAttribute(ArticleModelAttrs.ARTICLE_REQUEST) ArticleDTO request,
            BindingResult bindingResult,
            @PathVariable(Parameters.ID) int id,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(ArticleModelAttrs.ARTICLE_ID, id);
            fillCommonModel(model, request.publicationId());
            return ArticlePages.EDIT;
        }
        articleClient.update(id, request);
        return ArticlePages.REDIRECT_ARTICLES;
    }

    @DeleteMapping(Urls.ID)
    @PreAuthorize("hasRole('JOURNALIST')")
    public String delete(@PathVariable(Parameters.ID) int id) {
        articleClient.delete(id);
        return ArticlePages.REDIRECT_ARTICLES;
    }

    @GetMapping(Urls.ID + Urls.VIEW)
    @PreAuthorize("hasRole('CHIEF_EDITOR')")
    public String view(@PathVariable(Parameters.ID) int id, Model model) {
        ArticleDTO article = articleClient.get(id);
        List<EmployeeShort> journalists = articleClient.getCoAuthors(article.publicationId());
        List<String> coAuthors = journalists.stream()
                .filter(journalist -> article.coAuthorsIds().contains(journalist.id()))
                .map(EmployeeShort::getShortName)
                .toList();
        model.addAttribute(ArticleModelAttrs.ARTICLE_REQUEST, article);
        model.addAttribute(ArticleModelAttrs.PUBLICATIONS, publicationClient.getAll());
        model.addAttribute(ArticleModelAttrs.CATEGORIES, categoryClient.getAll());
        model.addAttribute(ArticleModelAttrs.CO_AUTHORS, coAuthors);
        return ArticlePages.VIEW;
    }

    private void fillCommonModel(Model model, Integer publicationId) {
        model.addAttribute(ArticleModelAttrs.PUBLICATIONS, publicationClient.getAll());
        model.addAttribute(ArticleModelAttrs.CATEGORIES, categoryClient.getAll());
        List<EmployeeShort> journalists;
        if (publicationId == null) {
            journalists = Collections.emptyList();
        } else {
            journalists = articleClient.getCoAuthors(publicationId);
        }
        model.addAttribute(ArticleModelAttrs.JOURNALISTS, journalists);
    }
}