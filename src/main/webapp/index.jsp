<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- Redirect incoming requests to the controller servlet --%>
<%
    response.sendRedirect(request.getContextPath() + "/users");
%>
