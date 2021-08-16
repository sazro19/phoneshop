<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ attribute name="numberOfPages" required="true" %>


<c:set var="numberOfButtons" value="${numberOfPages >= 9 ? 9 : numberOfPages}" scope="page"/>

<c:set var="currentPage" value="${not empty param.page ? param.page : 1}" scope="page"/>

<c:set var="pageBegin" value="${not empty param.page ? 1 : 1}" scope="page"/>

<c:set var="pageEnd"
       value="${pageBegin + numberOfButtons > numberOfPages ? numberOfPages : pageBegin + numberOfButtons}"/>

<c:set var="offset" scope="page"/>

<c:set var="threshold" scope="page"/>

<c:choose>
    <c:when test="${numberOfPages < 9 || pageEnd == numberOfPages}">
        <c:set var="threshold" value="${pageEnd + 1}"/>
    </c:when>
    <c:otherwise>
        <c:set var="threshold"
               value="${((pageEnd - pageBegin) / 2) % 1 == 0 ? (pageEnd - pageBegin) / 2 : (pageEnd - pageBegin) / 2 + 0.5}"/>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${currentPage > threshold}">
        <c:choose>
            <c:when test="${currentPage <= numberOfPages}">
                <c:set var="offset" value="${currentPage - threshold}"/>
                <c:set var="pageBegin"
                       value="${pageBegin + offset + numberOfButtons >= numberOfPages ? numberOfPages - numberOfButtons : pageBegin + offset}"/>
                <c:set var="pageEnd" value="${pageEnd + offset > numberOfPages ? numberOfPages : pageEnd + offset}"/>
                <c:set var="threshold" value="${threshold + offset}"/>
            </c:when>
            <c:otherwise>
                <c:set var="offset" value="${currentPage - threshold}"/>
                <c:set var="pageBegin"
                       value="${pageEnd == numberOfPages ? pageEnd - numberOfButtons : pageBegin + offset}"/>
                <c:set var="pageEnd" value="${pageEnd + offset > numberOfPages ? numberOfPages : pageEnd + offset}"/>
                <c:set var="threshold" value="${threshold + offset}"/>
                <c:if test="${currentPage <= numberOfPages}">
                    <c:set var="currentPage" value="${numberOfPages}"/>
                    <c:set var="pageBegin" value="${numberOfPages - numberOfButtons}"/>
                    <c:set var="pageEnd" value="${numberOfPages}"/>
                    <c:set var="threshold" value="${currentPage}"/>
                </c:if>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:set var="pageBegin" value="${currentPage - threshold >= 1 ? currentPage - threshold : 1}"/>
        <c:set var="pageEnd"
               value="${pageBegin + numberOfButtons > numberOfPages ? numberOfPages : pageBegin + numberOfButtons}"/>
    </c:otherwise>
</c:choose>

<nav aria-label="...">
    <ul class="pagination justify-content-center">
        <li class="page-item ${currentPage eq 1 ? 'disabled' : ''}">
            <a class="page-link"
               href="?sort=${param.sort}&order=${param.order}&query=${param.query}&searchCriteria=${param.searchCriteria}&page=${currentPage-1}"
               aria-label="Previous">
                <span aria-hidden="true">&laquo;</span>
                <span class="sr-only">Previous</span>
            </a>
        </li>

        <c:forEach begin="${pageBegin}" end="${pageEnd}" var="pageNum" varStatus="loopStatus">

            <li class="page-item ${pageNum eq currentPage ? 'active' : ''}">
                <a class="page-link"
                   href="?sort=${param.sort}&order=${param.order}&query=${param.query}&searchCriteria=${param.searchCriteria}&page=${pageNum}">
                        ${pageNum}
                </a>
            </li>
        </c:forEach>

        <li class="page-item ${currentPage eq numberOfPages ? 'disabled' : ''}">
            <a class="page-link"
               href="?sort=${param.sort}&order=${param.order}&query=${param.query}&searchCriteria=${param.searchCriteria}&page=${currentPage+1}"
               aria-label="Next">
                <span aria-hidden="true">&raquo;</span>
                <span class="sr-only">Next</span>
            </a>
        </li>
    </ul>
</nav>