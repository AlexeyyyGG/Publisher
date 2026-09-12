<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8"/>
    <title></title>
</head>
<body>
<%--@elvariable id="articleRequest" type="dto"--%>
<%--@elvariable id="publications" type="java.util.List"--%>
<%--@elvariable id="categories" type="java.util.List"--%>
<%--@elvariable id="journalists" type="java.util.List"--%>
<%--@elvariable id="articleId" type="java.lang.Integer"--%>
<main>
    <form:form method="post"
               action="${pageContext.request.contextPath}/web/articles/${articleId}"
               novalidate="true"
               modelAttribute="articleRequest">
        <div>
            <label for="publicationId">Журнал</label>
            <form:select path="publicationId" id="publicationId" data-url="${pageContext.request.contextPath}/web/articles/coauthors">
                <form:options items="${publications}" itemValue="id" itemLabel="name"/>
            </form:select>
            <form:errors path="publicationId" cssStyle="color:red"/>
        </div>
        <div>
            <label for="categoryId">Рубрика</label>
            <form:select path="categoryId" id="categoryId">
                <form:options items="${categories}" itemValue="id" itemLabel="name"/>
            </form:select>
            <form:errors path="categoryId" cssStyle="color:red"/>
        </div>
        <div>
            <label for="name">Название</label>
            <form:input path="name" id="name"/>
            <form:errors path="name" cssStyle="color:red"/>
        </div>
        <div>
            <label for="content">Содержание</label>
            <form:textarea
                    path="content"
                    id="content"
                    rows="4"
                    maxlength="100"/>
            <form:errors path="content" cssStyle="color:red"/>
        </div>
        <div>
            <label for="coAuthorsIds">Соавторы</label>
            <form:select path="coAuthorsIds" id="coAuthorsIds" multiple="true">
                <form:options items="${journalists}" itemValue="id" itemLabel="shortName"/>
            </form:select>
            <form:errors path="coAuthorsIds" cssStyle="color:red"/>
        </div>
        <div>
            <button type="button"
                    onclick="location.href='${pageContext.request.contextPath}/web/articles'">
                Отменить
            </button>
            <button type="submit">Сохранить</button>
        </div>
    </form:form>
</main>
<script src="${pageContext.request.contextPath}/articles/js/coauthors.js"></script>
</body>
</html>