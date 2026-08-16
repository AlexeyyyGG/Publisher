package com.cloud.publishing.common.constants.article;

import com.cloud.publishing.common.constants.Urls;

public class ArticlePages {
    private ArticlePages() {
    }

    public static final String LIST = "articles/articles";
    public static final String NEW = "articles/new";
    public static final String EDIT = "articles/edit";
    public static final String VIEW = "articles/view";
    public static final String REDIRECT_ARTICLES = "redirect:" + Urls.WEB_ARTICLES;
}
