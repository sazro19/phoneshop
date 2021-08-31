<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tags:master pageTitle="Order">
  <h1>Order</h1>
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

  <form method="post" action="${pageContext.request.contextPath}/order">
    <tags:orderRowForm name="firstName" label="First name" order="${order}" customer="${customerInfo}"
                       errors="${customerErrors}"/>
    <tags:orderRowForm name="lastName" label="Last name" order="${order}" customer="${customerInfo}"
                       errors="${customerErrors}"/>
    <tags:orderRowForm name="deliveryAddress" label="Address" order="${order}" customer="${customerInfo}"
                       errors="${customerErrors}"/>
    <tags:orderRowForm name="contactPhoneNo" label="Phone" order="${order}" customer="${customerInfo}"
                       errors="${customerErrors}"/>
    <tags:orderRowForm name="firstname" label="First name" order="${order}" customer="${customerInfo}"
                       errors="${customerErrors}"/>
    <div>
      <label>
        <textarea name="additionalInformation" rows="5" placeholder="Additional information"></textarea>
      </label>
    </div>
    <button type="submit" class="order-btn btn btn-outline-success">Order</button>
  </form>
</tags:master>