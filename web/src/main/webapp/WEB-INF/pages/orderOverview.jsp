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

  <tags:orderDetails order="${order}"/>

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