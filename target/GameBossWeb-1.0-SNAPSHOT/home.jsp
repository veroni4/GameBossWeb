<%-- 
    Document   : home
    Created on : Dec. 20, 2023, 1:44:09 p.m.
    Author     : veronica
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="com.gameboss.gamebossweb.Target"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="org.eclipse.paho.client.mqttv3.MqttException"%>
<%@page import="com.gameboss.gamebossweb.Game"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/common.jspf"%>
<%    //SETUP
    String resultMsg = "";
    if (request.getParameter("game") != null && request.getParameter("duration") != null) {
        String gameName = request.getParameter("game");
        int duration = Integer.parseInt(request.getParameter("duration"));
        switch (request.getParameter("proc")) {
            case "start":
                resultMsg = boss.startGame(gameName, duration); 
                break;
            case "end":
                resultMsg = boss.endGame();
                break;
            case "setAll":
                boss.publishToAll(Target.Modes.valueOf(request.getParameter("mode")), Target.Scenarios.valueOf(request.getParameter("scenario")));
                resultMsg = boss.setAllTargets(request.getParameter("mode"), request.getParameter("scenario").toLowerCase());
                break;
        }
    }

%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <% if(boss.getCurrentGame() != null && boss.getCurrentGame().getCurrentGameSecondsRem() > 0){ %>
            <meta http-equiv="refresh" content="<%= boss.getCurrentGame().getCurrentGameSecondsRem() %>; url=home.jsp">
        <% } %>
        
       
        <title>JSP Page</title>
    </head>
    <body>

        <jsp:include page="/WEB-INF/jspf/header.jspf"/>

        <text>
        <script>
            function formSubmit(v) {
                form = document.getElementById("form1");
                proc = document.getElementById("proc");
                proc.value = v;
                form.submit();
            }
        </script>
        <form action="home.jsp" id="form1">

            <table border="5" width="600">
                <thead>
                    <tr>
                        <th>GAME CONTROL</th>
                        <th>TARGET CONTROL</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Select game: 
                            <select name="game" size="1">
                                <% 
                                ArrayList<String> arr = boss.getGamesbyName();
                                for(String name : arr){ %>
                                    <option value=<%=name%>><%=name%></option>
                                    <% } %>

                            </select>
                        </td>
                        <td>Select mode: 
                            <select name="mode" size="1">
                                <option value="off">Off</option>
                                <option value="white">White</option>
                                <option value="magenta">Magenta</option>
                                <option value="blue">Blue</option>
                                <option value="green">Green</option>
                                <option value="sabotage">Sabotage</option>
                                <option value="hit">Hit</option>
                                <option value="cWhite">const. White</option>
                                <option value="cBlue">const. Blue</option>
                                <option value="cGreen">const. Green</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Duration:
                            <input type="text" name="duration" value="2" />
                        </td>
                        <td>Select scenario: <select name="scenario" size="1">
                                <option value="SINGLE">Single</option>
                                <option value="LOADING">Loading</option>
                                <option value="RANDOM">Random</option>
                            </select>
                        </td>

                    </tr>
                    <tr>
                        <td>
                            <% if (boss.getCurrentGame() == null) { %>
                            <input type="button" value="START" onclick="formSubmit('start');"/>
                            <% } else { %>
                                    <input type="button" value="END" onclick="formSubmit('end');"/>
                               <% } %>
                        </td>
                        <td>
                            <input type="button" value="SET ALL" onclick="formSubmit('setAll');"/>
                        </td>
                    </tr>
                </tbody>
            </table>   
            <input id="proc" type="hidden" name="proc" value="" />
        </form>
        <p>
            <%=resultMsg%>
        </p>
        </text>
        <%@ include file="/WEB-INF/jspf/footer.jspf"%>
    </body>

</html>
