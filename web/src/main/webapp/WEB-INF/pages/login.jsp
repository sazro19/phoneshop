<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tags:master pageTitle="Login">

  <c:if test="${not empty loginError}">
    <div class="alert alert-danger" role="alert">
        ${loginError}
    </div>
  </c:if>
  <div>
    <form action="${pageContext.request.contextPath}/login" method="post">
      <h1>Log in</h1>
      <input type="text" name="username" placeholder="User name" required>
      <br/>
      <input type="password" name="password" placeholder="Password" required>
      <br/>
      <div>
        <button type="submit">Log in</button>
      </div>
    </form>
  </div>
</tags:master>