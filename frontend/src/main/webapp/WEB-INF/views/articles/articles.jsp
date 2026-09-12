<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8"/>
    <title>Статьи</title>
</head>
<body>
<%@ include file="/WEB-INF/views/layout/navigation.jsp" %>
<%--@elvariable id="_csrf" type="org.springframework.security.web.csrf.CsrfToken"--%>
<%--@elvariable id="articles" type="java.util.List"--%>
<main>
    <div style="display: inline-block;">
        <c:if test="${isJournalist}">
            <div style="text-align: right; margin-bottom: 10px;">
                <a href="${pageContext.request.contextPath}/web/articles/new">
                    Новая статья
                </a>
            </div>
        </c:if>
        <c:choose>
            <c:when test="${empty articles}">
                <p>Добавленных статей пока нет.</p>
            </c:when>
            <c:otherwise>
                <table border="1">
                    <tr>
                        <th>Название</th>
                        <th>Журнал/газета</th>
                        <th>Рубрика</th>
                        <c:if test="${isChiefEditor}">
                            <th>Автор</th>
                        </c:if>
                        <th>Соавторы</th>
                        <th></th>
                    </tr>
                    <c:forEach var="article" items="${articles}">
                        <tr>
                            <td>${article.name}</td>
                            <td>${article.publicationName}</td>
                            <td>${article.categoryName}</td>
                            <c:if test="${isChiefEditor}">
                                <td>${article.author}</td>
                            </c:if>
                            <td>
                                <c:if test="${not empty article.coAuthors}">
                                    <c:forEach var="coAuthor"
                                               items="${article.coAuthors}"
                                               varStatus="status">
                                        ${coAuthor}
                                        <c:if test="${!status.last}">, <br/></c:if>
                                    </c:forEach>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${isChiefEditor}">
                                    <a href="${pageContext.request.contextPath}/web/articles/${article.id}/view"
                                       style="margin-right: 10px;">
                                        Просмотреть
                                    </a>
                                </c:if>
                                <c:if test="${isJournalist}">
                                    <c:if test="${!article.published}">
                                        <a href="${pageContext.request.contextPath}/web/articles/${article.id}/edit"
                                           style="margin-right: 10px;">
                                            Редактировать
                                        </a>
                                    </c:if>
                                    <c:if test="${!article.hasReviews}">
                                        <form action="${pageContext.request.contextPath}/web/articles/${article.id}"
                                              method="post"
                                              style="display:inline;">
                                            <input type="hidden" name="_method" value="delete"/>
                                            <input type="hidden"
                                                   name="${_csrf.parameterName}"
                                                   value="${_csrf.token}"/>
                                            <button type="submit"
                                                    onclick="return confirm('Вы уверены?');">
                                                Удалить
                                            </button>
                                        </form>
                                    </c:if>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</main>
</body>
</html>