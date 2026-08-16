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
            <form:select path="publicationId" id="publicationId">
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
<script>
  document.addEventListener('DOMContentLoaded', function () {
    const publicationSelect = document.getElementById('publicationId');
    const coAuthorsSelect = document.getElementById('coAuthorsIds');
    publicationSelect.addEventListener('change', function () {
      const publicationId = this.value;
      coAuthorsSelect.innerHTML = '';
      if (!publicationId) {
        return;
      }
      const url = '${pageContext.request.contextPath}/web/articles/coauthors?publicationId='
          + encodeURIComponent(publicationId);
      fetch(url)
      .then(response => {
        if (!response.ok) {
          throw new Error('Не удалось загрузить список соавторов');
        }
        return response.json();
      })
      .then(journalists => {
        journalists.forEach(journalist => {
          const option = document.createElement('option');
          option.value = journalist.id;
          option.textContent = journalist.shortName;
          coAuthorsSelect.appendChild(option);
        });
      })
      .catch(error => {
        console.error(error);
      });
    });
  });
</script>
</body>
</html>