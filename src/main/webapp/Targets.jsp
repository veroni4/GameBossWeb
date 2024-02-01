<%-- 
    Document   : Targets
    Created on : Dec. 22, 2023, 4:32:44 p.m.
    Author     : veronica
--%>

<%@page import="com.gameboss.gamebossweb.Target"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/header.jspf"%>
<%@ include file="/WEB-INF/jspf/common.jspf"%>
<%    if (request.getParameter("setAllTargets") != null) {
        Target.Modes mode = Target.Modes.valueOf(request.getParameter("mode"));
        Target.Scenarios scenario = Target.Scenarios.valueOf(request.getParameter("scenario")); //change to uppercase
       
        //make array with hostNames, iterate through and make target for each hostName....
        ArrayList<String> arr = new ArrayList<>();
        for (Target t : boss.getTargets()) {
            if (request.getParameter(t.getHost()) != null) {
                arr.add(t.getHost());
            }
        }
        for (String name : arr) {
            Target t = boss.getTargetByHost(name);
            t.setMode(mode);
            t.setScenario(scenario);
            boss.publishToTarget(t);
        }
    }

%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <form action="Targets.jsp" >
        <body>
            <table border="1">
                    <% for (int row = 0; row < boss.getRowsNum(); row++) { %>   
                <tr>
                    <% for (int col = 0; col < boss.getColumnsNum(); col++) {%>
                    <td>
                        <%=boss.getTargetAt(col, row).getHost()%>
                        <input type="checkbox" name="<%=boss.getTargetAt(col, row).getHost()%>" value="ON" />
                    </td>
                    <%
                        } %>
                </tr>
                <%
                    }%>


            </table>
            <p>
            <table border="5">
                <thead>
                    <tr>
                        <th>TARGET CONTROL</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Select mode: <select name="mode" size="1">
                                <option value="buffer">---</option>
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
                            </select></td>
                    </tr>
                    <tr>

                        <td>Select scenario: <select name="scenario" size="1">
                                <option value="buffer">---</option>
                                <option value="SINGLE">Single</option>
                                <option value="LOADING">Loading</option>
                                <option value="RANDOM">Random</option>
                            </select></td>

                    </tr>
                    <tr>

                        <td><input type="submit" value="SET" name="setAllTargets"/></td>
                    </tr>
                </tbody>
            </table>
            <p>
                <%@ include file="/WEB-INF/jspf/footer.jspf"%>

        </body>

    </form>
</html>
