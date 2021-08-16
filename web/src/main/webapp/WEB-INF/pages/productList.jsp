<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Product list">
  <form>
    <input name="query" value="${param.query}"/>
    <button>
      Search
    </button>
  </form>
  <form method="post">
    <table class="table">
      <thead>
      <tr>
        <td>Image</td>
        <td>Brand</td>
        <td>Model</td>
        <td>Color</td>
        <td>Display size</td>
        <td>Price</td>
        <td>Quantity</td>
        <td>Action</td>
      </tr>
      </thead>
      <c:forEach var="phone" items="${phones}">
        <tr>
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
          <td><input id="${phone.id}" value="1"></td>
          <td><button>Add to</button></td>
        </tr>
      </c:forEach>
    </table>
  </form>
  <tags:pagination numberOfPages="${numberOfPages}"/>
</tags:master>