<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>홈</title>
</head>
<style>
.center {
	text-align: center;
}

.pagination {
  display: inline-block;
}

.pagination a {
  color: black;
  float: left;
  padding: 8px 16px;
  text-decoration: none;
  transition: background-color .3s;
}

.pagination a.active {
  background-color: #4CAF50;
  color: white;
}

.pagination a:hover:not(.active) {background-color: #ddd;}
</style>
<script type="text/javascript">
	function goPage(page) {
		location.href = '${pageContext.request.contextPath}/paging.do?pageNo='+page;
	}
</script>
<jsp:include page="../common/menu.jsp" />
<body>

<c:forEach var="vo" items="${list }">
 <div align="center">
	 <p>id: ${vo.employeeId } / ${vo.firstName } / ${vo.lastName } / ${vo.salary }</p>
 </div>
</c:forEach>

<jsp:include page="../common/paging.jsp" />
</body>
</html>