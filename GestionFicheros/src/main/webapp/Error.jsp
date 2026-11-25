<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entity.FicheroXML"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Error</title>
</head>
<body style="font-family: serif; margin: 30px;">
  <hr>
  <h2 style="color: red; text-align: center;">TIPO DE ERROR</h2>
  <p style="text-align: center; color: red;">
    <%= FicheroXML.escapar((String) request.getAttribute("error")) %>
  </p>
  <p style="text-align: center"><a href="TratamientoFich.jsp">Volver</a></p>
</body>
</html>
