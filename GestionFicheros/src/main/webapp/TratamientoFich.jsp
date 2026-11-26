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
<script>
  function actualizarCampos() {
    const formato = document.getElementById("formato").value;

    // Definimos las etiquetas de cada formato (null = se oculta esa fila)
    const etiquetasPorFormato = {
      "XML":  ["DATO1 (año):", "DATO2 (sector):", "DATO3 (gas):", "DATO4 (toneladas):", "DATO5 (municipio):", "DATO6 (observaciones):"],
      "JSON": ["DATO1 (Nombre):", "DATO2 (ODS):", "DATO3 (Periodo):", "DATO4 (Valor):", null, null]
    };

    const etiquetas = etiquetasPorFormato[formato] || ["DATO1:", "DATO2:", "DATO3:", "DATO4:", "DATO5:", "DATO6:"];

    for (let i = 1; i <= 6; i++) {
      const fila = document.getElementById("fila-dato" + i);
      const label = document.getElementById("label-dato" + i);
      const texto = etiquetas[i - 1];

      if (texto === null) {
        fila.style.display = "none";
      } else {
        fila.style.display = "";
        label.textContent = texto;
      }
    }
  }

  window.onload = actualizarCampos;
</script>
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
                <select name="formato" id="formato" onchange="actualizarCampos()">
                  <option value="XML">XML</option>
                  <option value="JSON">JSON</option>
                  <!-- Resto del grupo: XLS, CSV, RDF... -->
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
            <tr id="fila-dato1"><td id="label-dato1">DATO1 (año):</td><td><input type="text" name="dato1"></td></tr>
            <tr id="fila-dato2"><td id="label-dato2">DATO2 (sector):</td><td><input type="text" name="dato2"></td></tr>
            <tr id="fila-dato3"><td id="label-dato3">DATO3 (gas):</td><td><input type="text" name="dato3"></td></tr>
            <tr id="fila-dato4"><td id="label-dato4">DATO4 (toneladas):</td><td><input type="text" name="dato4"></td></tr>
            <tr id="fila-dato5"><td id="label-dato5">DATO5 (municipio):</td><td><textarea name="dato5" rows="2" cols="18"></textarea></td></tr>
            <tr id="fila-dato6"><td id="label-dato6">DATO6 (observaciones):</td><td><input type="text" name="dato6"></td></tr>
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