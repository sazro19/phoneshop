<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.core.cart.Cart" scope="session"/>

<form class="form-inline my-2 my-lg-0" action="${pageContext.request.contextPath}/cart">
  <button class="btn btn-outline-success my-2 my-sm-0" type="submit" id="minicart">
    cart: ${cart.totalQuantity} items ${cart.totalCost} $
  </button>
</form>