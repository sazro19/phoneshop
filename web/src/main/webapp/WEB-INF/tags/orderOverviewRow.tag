<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="order" required="true" type="com.es.core.model.order.Order" %>

<div>
  <label for="${name}">${label}</label>
  <div class="col-sm-2">
    <input type="text" id="${name}" name="${name}"
           value="${order[name]}">
  </div>
</div>