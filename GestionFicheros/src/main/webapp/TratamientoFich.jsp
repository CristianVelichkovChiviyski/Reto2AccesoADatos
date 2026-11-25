<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Tratamiento de ficheros</title>
<style>
  body { font-family: serif; margin: 30px; }
  td { vertical-align: top; padding: 2px 6px; }
  .aviso { color: red; font-weight: bold; }
</style>
</head>
<body>
  <h2>TRATAMIENTO FICHEROS</h2>

  <form action="ServletFich" method="post">
    <table>
      <tr>
        <td>
          <table>
            <tr>
              <td>Formato del fichero:</td>
              <td>
                <select name="formato">
                  <option value="XML">XML</option>
                  <!-- Resto del grupo: XLS, CSV, JSON, RDF... -->
                </select>
              </td>
            </tr>
            <tr><td colspan="2"><hr></td></tr>
            <tr><td colspan="2">¿Qué quiere hacer con el fichero?</td></tr>
            <tr>
              <td>Lectura:</td>
              <td><input type="radio" name="accion" value="lectura" checked></td>
            </tr>
            <tr>
              <td>Escritura:</td>
              <td><input type="radio" name="accion" value="escritura"></td>
            </tr>
          </table>
        </td>
        <td>
          <table>
            <tr><td>DATO1 (año):</td><td><input type="text" name="dato1"></td></tr>
            <tr><td>DATO2 (sector):</td><td><input type="text" name="dato2"></td></tr>
            <tr><td>DATO3 (gas):</td><td><input type="text" name="dato3"></td></tr>
            <tr><td>DATO4 (toneladas):</td><td><input type="text" name="dato4"></td></tr>
            <tr><td>DATO5 (municipio):</td><td><textarea name="dato5" rows="2" cols="18"></textarea></td></tr>
            <tr><td>DATO6 (observaciones):</td><td><input type="text" name="dato6"></td></tr>
            <tr><td colspan="2" class="aviso">(*) Los campos no pueden estar vacíos al escribir</td></tr>
          </table>
        </td>
      </tr>
    </table>
    <br>
    <input type="submit" value="Enviar">
  </form>
</body>
</html>
