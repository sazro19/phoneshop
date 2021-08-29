<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tags:master pageTitle="Product Details">
  <form action="${pageContext.request.contextPath}/productList">
    <button>
      Back to product list
    </button>
  </form>
    <h1>
      ${phone.model}
    </h1>
    <div class="row">
      <div class="column"><img src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}"></div>
      <div class="column">
        <b>Display</b>
        <table class="table">
          <tr>
            <td>
              Size
            </td>
            <td>
              ${phone.displaySizeInches}"
            </td>
          </tr>
          <tr>
            <td>
              Resolution
            </td>
            <td>
              ${phone.displayResolution}
            </td>
          </tr>
          <tr>
            <td>
              Technology
            </td>
            <td>
              ${phone.displayTechnology}
            </td>
          </tr>
          <tr>
            <td>
              Pixel density
            </td>
            <td>
              ${phone.pixelDensity}
            </td>
          </tr>
        </table>
      </div>
      <div>&nbsp;&nbsp;</div>
      <div class="column">
        <b>Dimensions & weight</b>
        <table class="table">
          <tr>
            <td>
              Length
            </td>
            <td>
              ${phone.lengthMm}mm
            </td>
          </tr>
          <tr>
            <td>
              Width
            </td>
            <td>
              ${phone.widthMm}mm
            </td>
          </tr>
          <tr>
            <td>
              Color
            </td>
            <td>
                ${phone.colors.iterator().next()}
            </td>
          </tr>
          <tr>
            <td>
              Weight
            </td>
            <td>
              ${phone.weightGr}
            </td>
          </tr>
        </table>
      </div>
      <div>&nbsp;&nbsp;</div>
      <div class="column">
        <b>Camera</b>
        <table class="table">
          <tr>
            <td>
              Front
            </td>
            <td>
              ${phone.frontCameraMegapixels} megapixels
            </td>
          </tr>
          <tr>
            <td>
              Back
            </td>
            <td>
              ${phone.backCameraMegapixels} megapixels
            </td>
          </tr>
        </table>
      </div>
      <div>&nbsp;&nbsp;</div>
      <div class="column">
        <b>Battery</b>
        <table class="table">
          <tr>
            <td>
              Talk time
            </td>
            <td>
                ${phone.talkTimeHours} hours
            </td>
          </tr>
          <tr>
            <td>
              Stand by time
            </td>
            <td>
                ${phone.standByTimeHours} hours
            </td>
          </tr>
          <tr>
            <td>
              Battery capacity
            </td>
            <td>
                ${phone.batteryCapacityMah}mAh
            </td>
          </tr>
        </table>
      </div>
      <div>&nbsp;&nbsp;</div>
      <div class="column">
        <b>Other</b>
        <table class="table">
          <tr>
            <td>
              Colors
            </td>
            <td>
              <c:forEach items="${phone.colors}" var="color">
                ${color}
              </c:forEach>
            </td>
          </tr>
          <tr>
            <td>
              Device type
            </td>
            <td>
                ${phone.deviceType}
            </td>
          </tr>
          <tr>
            <td>
              Bluetooth
            </td>
            <td>
                ${phone.bluetooth}
            </td>
          </tr>
        </table>
      </div>
    </div>
  <div class="description">
    ${phone.description}
  </div>
  <div>
    <h3>Price: ${phone.price}$</h3>
      <input id="quantity-${phone.id}"
             class="quantityInput quantity"
             type="text"
             name="quantity"
             value="1"/>
    <button id="btn-addPhoneToCart-${phone.id}" onclick="addPhoneToCart(${phone.id})"
            class="btn btn-light">
      Add
    </button>
      <div id="quantityInputMessage-${phone.id}">
      </div>
  </div>
</tags:master>