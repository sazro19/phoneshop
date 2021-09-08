<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tags:master pageTitle="OrderOverview">
  <h1>Order number: ${order.id}</h1>
  <h3>Order status: ${order.status}</h3>
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
  <form action="${pageContext.request.contextPath}/admin/orders">
    <button>
      Back
    </button>
  </form>
  <form method="post">
    <button>
      Delivered
    </button>
    <input type="hidden" name="status" value="DELIVERED">
  </form>
  <form method="post">
    <button>
      Rejected
    </button>
    <input type="hidden" name="status" value="REJECTED">
  </form>
</tags:master>