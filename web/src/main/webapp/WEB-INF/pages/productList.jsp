<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tags:master pageTitle="Product list">
  <form>
    <input name="query" value="${param.query}"/>
    <select name="searchCriteria">
      <c:choose>
        <c:when test="${param.searchCriteria eq 'Description'}">
          <option>Model</option>
          <option selected>Description</option>
        </c:when>
        <c:otherwise>
          <option selected>Model</option>
          <option>Description</option>
        </c:otherwise>
      </c:choose>
    </select>
    <button>
      Search
    </button>
  </form>
    <table class="table">
      <thead>
        <tr>
          <td>Image</td>
          <td>
            Brand
            <tags:sortLink sort="BRAND" order="ASC"/>
            <tags:sortLink sort="BRAND" order="DESC"/>
          </td>
          <td>
            Model
            <tags:sortLink sort="MODEL" order="ASC"/>
            <tags:sortLink sort="MODEL" order="DESC"/>
          </td>
          <td>Color</td>
          <td>
            Display size
            <tags:sortLink sort="DISPLAY_SIZE_INCHES" order="ASC"/>
            <tags:sortLink sort="DISPLAY_SIZE_INCHES" order="DESC"/>
          </td>
          <td>
            Price
            <tags:sortLink sort="PRICE" order="ASC"/>
            <tags:sortLink sort="PRICE" order="DESC"/>
          </td>
          <td>Quantity</td>
          <td>Action</td>
        </tr>
      </thead>
      <c:forEach var="phone" items="${phones}" varStatus="statusCartItems">
        <tr class="row-${statusCartItems.index % 2 == 0 ? "even" : ""}">
          <td>
            <img src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}">
          </td>
          <td>${phone.brand}</td>
          <td>${phone.model}</td>
          <td>
            <c:forEach var="color" items="${phone.colors}">
              ${color.toString()}
            </c:forEach>
          </td>
          <td>${phone.displaySizeInches}"</td>
          <td>${phone.price}$</td>
          <td> <input id="quantity-${phone.id}"
                      class="quantityInput quantity"
                      type="text"
                      name="quantity"
                      value="1"/>
            <div id="quantityInputMessage-${phone.id}">
            </div>
          </td>
          <td><button id="btn-addPhoneToCart-${phone.id}" onclick="addPhoneToCart(${phone.id})"
                      class="btn btn-light">
            Add
          </button></td>
        </tr>
      </c:forEach>
    </table>
  <tags:pagination numberOfPages="${numberOfPages}"/>
</tags:master>