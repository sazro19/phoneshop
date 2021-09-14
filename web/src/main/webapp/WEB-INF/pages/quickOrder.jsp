<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tags:master pageTitle="B2B order">
  <h1>B2B order</h1>
    <table class="table">
      <thead>
        <tr>
          <td>
            Phone model
          </td>
          <td>
            Quantity
          </td>
        </tr>
      </thead>
      <form:form modelAttribute="quickOrderDto" method="post">
        <c:forEach begin="0" end="7" var="i">
          <tr>
            <td>
              <form:input id="inputModel-${i}" path="rows[${i}].phoneModel"/>
              <form:hidden path="rows[${i}].rowId" value="${i}"/>
              <div class="error">
                <form:errors path="rows[${i}].phoneModel"/>
              </div>
            </td>
            <td>
              <form:input id="inputQuantity-${i}" path="rows[${i}].quantity"/>
              <div class="error">
                <form:errors path="rows[${i}].quantity"/>
              </div>
            </td>
          </tr>
        </c:forEach>
        <button type="submit">
          Add to cart
        </button>
      </form:form>
    </table>
</tags:master>