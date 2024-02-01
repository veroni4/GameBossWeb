<%-- 
    Document   : settings
    Created on : Jan. 20, 2024, 1:47:01 p.m.
    Author     : veronica
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/common.jspf"%>
<!DOCTYPE html>
<%  String broker = "";
    String user = "";
    String psw = "";
    if (request.getParameter("submit") != null) {
        // Change mqttConfig
        if ((request.getParameter("broker") != null) && (request.getParameter("user") != null) && (request.getParameter("password") != null)) {
            broker = request.getParameter("broker");
            user = request.getParameter("user");
            psw = request.getParameter("password");
            boss.updateMqttConfig(broker, user, psw);
        }
    }
    else{
        //Original mqttConfig
        broker = boss.getBroker();
        user = boss.getUser();
        psw = boss.getPsw();
    }


%>
<html>
    <body>
        <jsp:include page="/WEB-INF/jspf/header.jspf"/>
        <form action="settings.jsp">
            <p>
                Broker:
                <input type="text" name="broker" value="<%=broker%>" />
            <p>
                User:
                <input type="text" name="user" value="<%=user%>" />
            <p>
                Password:
                <input type="text" name="password" value="<%=psw%>" />
            <p>
                <input type="submit" value="submit" name="submit" />
        </form>
    </body>
</html>
