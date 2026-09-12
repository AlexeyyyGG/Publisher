<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="_csrf" type="org.springframework.security.web.csrf.CsrfToken"--%>
<nav class="navigation">
    <ul style="
        display: flex;
        gap: 16px;
        list-style: none;
        padding: 0;
        margin: 0;
        align-items: center;">
        <li class="navigation_item">
            <a href="${pageContext.request.contextPath}/web/employees">
                Сотрудники
            </a>
        </li>
        <c:if test="${pageContext.request.isUserInRole('ROLE_CHIEF_EDITOR')}">
            <li class="navigation_item">
                <a href="${pageContext.request.contextPath}/web/publications">
                    Журналы/Газеты
                </a>
            </li>
        </c:if>
        <c:if test="${pageContext.request.isUserInRole('ROLE_CHIEF_EDITOR') ||
         pageContext.request.isUserInRole('ROLE_JOURNALIST')}">
            <li class="navigation_item">
                <a href="${pageContext.request.contextPath}/web/articles">
                    Статьи
                </a>
            </li>
        </c:if>
        <li class="navigation_item">
            <form action="${pageContext.request.contextPath}/logout"
                  method="post"
                  style="margin: 0;">
                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}"/>
                <button type="submit">
                    Выйти
                </button>
            </form>
        </li>
    </ul>
</nav>