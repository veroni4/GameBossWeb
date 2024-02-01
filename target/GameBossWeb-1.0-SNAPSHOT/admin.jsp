<%-- 
    Document   : admin
    Created on : Jan. 5, 2024, 1:19:15 p.m.
    Author     : veronica
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/common.jspf"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <jsp:include page="/WEB-INF/jspf/header.jspf"/>
        <form action="admin.jsp">
            <input type="submit" value="Reload & Reconnect" />
        </form>
    <p>
   <%@ include file="/WEB-INF/jspf/footer.jspf"%>
    </body>

</html>
