<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tags:master pageTitle="Orders">
  <h1>Orders</h1>
    <table class="table">
      <thead>
        <tr>
          <td>
            Order number
          </td>
          <td>
            Customer
          </td>
          <td>
            Phone
          </td>
          <td>
            Address
          </td>
          <td>
            Date
          </td>
          <td>
            Total Price
          </td>
          <td>
            Status
          </td>
        </tr>
      </thead>
      <c:forEach var="order" items="${orderList}">
        <tr>
          <td><a href="${pageContext.request.contextPath}/admin/orders/${order.id}">${order.id}</a></td>
          <td>${order.firstName} ${order.lastName}</td>
          <td>${order.contactPhoneNo}</td>
          <td>${order.deliveryAddress}</td>
          <td>${order.creationDate.toLocalDate()} ${order.creationDate.hour}:${order.creationDate.minute}</td>
          <td>${order.totalPrice}</td>
          <td>${order.status}</td>
        </tr>
      </c:forEach>
</tags:master>