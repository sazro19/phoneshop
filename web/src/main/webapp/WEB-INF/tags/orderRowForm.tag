<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="customer" required="true" type="com.es.phoneshop.web.controller.dto.CustomerInfoDto" %>
<%@ attribute name="errors" required="true" type="java.util.Map" %>

<div>
  <c:set var="error" value="${errors[name]}"/>
  <label for="${name}">${label}*</label>
  <div class="col-sm-2">
    <input type="text" id="${name}" name="${name}"
           value="${not empty customer ? customer[name] : ""}">
  </div>
  <c:if test="${not empty errors}">
    <div class="error">
        ${error}
    </div>
  </c:if>
</div>