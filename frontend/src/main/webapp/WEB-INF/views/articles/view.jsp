<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8"/>
    <title>Просмотр статьи</title>
</head>
<body>
<%--@elvariable id="articleRequest" type="com.cloud.publishing.common.dto.ArticleDTO"--%>
<%--@elvariable id="publications" type="java.util.List"--%>
<%--@elvariable id="categories" type="java.util.List"--%>
<%--@elvariable id="journalists" type="java.util.List"--%>
<main>
    <div>
        <strong>Журнал</strong>
        <c:forEach var="publication" items="${publications}">
            <c:if test="${publication.id == articleRequest.publicationId}">
                <span>${publication.name}</span>
            </c:if>
        </c:forEach>
    </div>
    <div>
        <strong>Рубрика</strong>
        <c:forEach var="category" items="${categories}">
            <c:if test="${category.id == articleRequest.categoryId}">
                <span>${category.name}</span>
            </c:if>
        </c:forEach>
    </div>
    <div>
        <strong>Название</strong>
        <p>${articleRequest.name}</p>
    </div>
    <div>
        <strong>Содержание</strong>
        <p>${articleRequest.content}</p>
    </div>
    <div>
        <strong>Соавторы</strong>
        <c:choose>
            <c:when test="${empty coAuthors}">
                <p>Нет соавторов</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="coAuthor" items="${coAuthors}" varStatus="status">
                    ${coAuthor}
                    <c:if test="${!status.last}">, </c:if>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    <div>
        <button type="button"
                onclick="location.href='${pageContext.request.contextPath}/web/articles'">
            Назад
        </button>
    </div>
</main>
</body>
</html>