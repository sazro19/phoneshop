<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tags:master pageTitle="OrderOverview">
  <h1>Thank you for your order</h1>
  <form action="${pageContext.request.contextPath}/productList">
    <button>
      Back to product list
    </button>
  </form>
    <table class="table">
      <thead>
        <tr>
          <td>
            Brand
          </td>
          <td>
            Model
          </td>
          <td>Color</td>
          <td>
            Display size
          </td>
          <td>
            Price
          </td>
          <td>Quantity</td>
        </tr>
      </thead>
      <c:forEach var="orderItem" items="${order.orderItems}" varStatus="statusOrderItems">
        <tr class="row-${statusOrderItems.index % 2 == 0 ? "even" : ""}">
          <td>${orderItem.phone.brand}</td>
          <td><a href="${pageContext.request.contextPath}/productDetails?id=${orderItem.phone.id}">${orderItem.phone.model}</a></td>
          <td>
            <c:forEach var="color" items="${orderItem.phone.colors}">
              ${color.toString()}
            </c:forEach>
          </td>
          <td>${orderItem.phone.displaySizeInches}"</td>
          <td>${orderItem.phone.price}$</td>
          <td>
            <fmt:formatNumber value="${orderItem.quantity}" var="quantity"/>
            ${quantity}
          </td>
        </tr>
      </c:forEach>
      <tr>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td>Subtotal</td>
        <td>${order.subtotal}$</td>
      </tr>
      <tr>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td>Delivery</td>
        <td>${order.deliveryPrice}$</td>
      </tr>
      <tr>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td>TOTAL</td>
        <td>${order.totalPrice}$</td>
      </tr>
    </table>

    <tags:orderOverviewRow name="firstName" label="First name" order="${order}"/>
    <tags:orderOverviewRow name="lastName" label="Last name" order="${order}"/>
    <tags:orderOverviewRow name="deliveryAddress" label="Address" order="${order}"/>
    <tags:orderOverviewRow name="contactPhoneNo" label="Phone" order="${order}"/>
  Additional information:
  <div class="description">
      ${order.additionalInformation}
  </div>
  <form action="${pageContext.request.contextPath}/productList">
    <button>
      Back to shopping
    </button>
  </form>
</tags:master>