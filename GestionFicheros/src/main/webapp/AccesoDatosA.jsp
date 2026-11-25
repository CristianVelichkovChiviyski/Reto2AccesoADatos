<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map, java.util.Set, entity.FicheroXML"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Datos</title>
<style>
  body { font-family: serif; margin: 30px; }
  h2 { text-align: center; }
  table { margin: auto; border-collapse: collapse; }
  th, td { border: 1px solid #888; padding: 4px 10px; }
  th { background: #eee; }
</style>
</head>
<body>
<%
    Set<String> cabeceras = (Set<String>) request.getAttribute("cabeceras");
    List<Map<String, String>> filas = (List<Map<String, String>>) request.getAttribute("filas");
%>
  <h2>DATOS</h2>
  <table>
    <tr>
      <% for (String c : cabeceras) { %>
        <th><%= FicheroXML.escapar(c.toUpperCase()) %></th>
      <% } %>
    </tr>
    <% for (Map<String, String> fila : filas) { %>
      <tr>
        <% for (String c : cabeceras) { %>
          <td><%= FicheroXML.escapar(fila.getOrDefault(c, "")) %></td>
        <% } %>
      </tr>
    <% } %>
  </table>
  <p style="text-align:center"><a href="TratamientoFich.jsp">Volver</a></p>
</body>
</html>
