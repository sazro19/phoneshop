<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tags:master pageTitle="Cart">
  <h1>Cart</h1>
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
          <td>Action</td>
        </tr>
      </thead>
      <c:forEach var="cartItem" items="${cart.itemList}" varStatus="statusCartItems">
        <tr class="row-${statusCartItems.index % 2 == 0 ? "even" : ""}">
          <td>${cartItem.phone.brand}</td>
          <td><a href="${pageContext.request.contextPath}/productDetails?id=${cartItem.phone.id}">${cartItem.phone.model}</a></td>
          <td>
            <c:forEach var="color" items="${cartItem.phone.colors}">
              ${color.toString()}
            </c:forEach>
          </td>
          <td>${cartItem.phone.displaySizeInches}"</td>
          <td>${cartItem.phone.price}$</td>
          <td>
            <fmt:formatNumber value="${cartItem.quantity}" var="quantity"/>
            <c:set var="error" value="${errors[cartItem.phone.id]}"/>
            <input id="quantity-${cartItem.phone.id}"
                   form="updateForm"
                   class="quantityInput quantity"
                   type="text"
                   name="quantity"
                   value="${not empty error ? paramValues['quantity'][statusCartItems.index] : cartItem.quantity}"/>
            <input type="hidden"
                   form="updateForm"
                   name="phoneId"
                   value="${cartItem.phone.id}"/>
            <c:choose>
              <c:when test="${not empty error}">
                <div class="error">
                    ${error}
                </div>
              </c:when>
              <c:otherwise>
                <div class="success">
                    ${not empty updated ? "Successfully updated" : ""}
                </div>
              </c:otherwise>
            </c:choose>
            <div id="quantityInputMessage-${cartItem.phone.id}">
            </div>
          </td>
          <td>
            <form action="${pageContext.servletContext.contextPath}/cart/${cartItem.phone.id}" method="post">
              <button id="btn-addPhoneToCart-${cartItem.phone.id}"
                      type="submit"
                      class="btn btn-outline-danger">
                Delete
              </button>
              <input type="hidden" name="_method" value="delete"/>
            </form>
          </td>
        </tr>
      </c:forEach>
    </table>
  <div class="d-flex flex-row-reverse">
    <div class="p-2">
      <a href="${pageContext.request.contextPath}/order"
         class="order-btn btn btn-outline-success justify-content-lg-end my-2 my-sm-0">
        Order
      </a>
    </div>
    <div class="p-2">
      <button type="submit"
              form="updateForm"
              class="btn btn-outline-dark justify-content-lg-end my-2 my-lg-0">
        Update
      </button>
    </div>
  <form method="post" action="${pageContext.request.contextPath}/cart" id="deleteForm">
    <input type="hidden" name="_method" value="delete"/>
  </form>
    <form method="post" action="${pageContext.request.contextPath}/cart" id="updateForm">
      <input type="hidden" name="_method" value="put"/>
    </form>
</tags:master>