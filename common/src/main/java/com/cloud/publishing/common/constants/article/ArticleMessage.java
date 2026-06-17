package com.cloud.publishing.common.constants.article;

public class ArticleMessage {
    private ArticleMessage() {
    }

    public static final String FAILED_TO_ADD = "Не удалось создать статью";
    public static final String FAILED_CREATING = "Не удалось создать статью, идентификатор не получен";
    public static final String ARTICLE_NOT_FOUND = "Статья не найдена";
    public static final String FAILED_TO_GET = "Не удалось получить данные статьи";
    public static final String FAILED_TO_GET_CO_AUTHOR = "Не удалось получить соавторов";
    public static final String FAILED_TO_UPDATE_ARTICLE = "Не удалось обновить статью";
    public static final String FAILED_TO_DELETE = "Не удалось удалить статью";
    public static final String FAILED_TO_CHECK = "Не удалось проверить существование статьи";
    public static final String SELF_CO_AUTHOR_ERROR = "Текущий пользователь не может быть соавтором собственной статьи";
    public static final String INVALID_CO_AUTHORS_ERROR = "В соавторы можно добавлять только журналистов, работающих в выбранном издании";
    public static final String ACCESS_DENIED_ERROR = "Вы не можете производить действия над чужой статьей!";
    public static final String ARTICLE_UPDATE_PUBLISHED_ERROR = "Нельзя редактировать уже опубликованную статью";
    public static final String ARTICLE_DELETE_PUBLISHED_ERROR = "Нельзя удалить уже опубликованную статью";
    public static final String ARTICLE_HAS_REVIEWS_ERROR = "Нельзя удалить статью, к которой добавлены рецензии";
}