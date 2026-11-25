package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import entity.FicheroXML;
import entity.ManejadorGenerico;

/*
 * NOTA: si usas Tomcat 10 o superior, cambia "javax.servlet" por "jakarta.servlet".
 *
 * Flujo: TratamientoFich.jsp -> ServletFich -> AccesoDatosA.jsp (o Error.jsp si falla)
 */
@WebServlet("/ServletFich")
public class ServletFich extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("TratamientoFich.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            String formato = request.getParameter("formato");
            String accion = request.getParameter("accion"); // "lectura" o "escritura"

            if (formato == null || accion == null) {
                throw new Exception("Faltan el formato o la accion en el formulario");
            }

            if (formato.equals("XML")) {
                String ruta = getServletContext().getRealPath("/datos/datos.xml");
                if (ruta == null) {
                    throw new Exception("No se pudo localizar el fichero datos.xml en el servidor");
                }

                if (accion.equals("lectura")) {
                    // LECTURA con SAX -> tabla en AccesoDatosA.jsp
                    ManejadorGenerico m = FicheroXML.leer(ruta);
                    request.setAttribute("cabeceras", m.getCabeceras());
                    request.setAttribute("filas", m.getFilas());
                    request.getRequestDispatcher("AccesoDatosA.jsp").forward(request, response);

                } else if (accion.equals("escritura")) {
                    // ESCRITURA con SAX -> se abre el fichero para ver el registro nuevo
                    String[] valores = new String[6];
                    for (int i = 0; i < valores.length; i++) {
                        String v = request.getParameter("dato" + (i + 1));
                        if (v == null || v.trim().isEmpty()) {
                            throw new Exception("Error de validacion: el campo DATO" + (i + 1)
                                    + " esta vacio. Los campos no pueden estar vacios");
                        }
                        valores[i] = v.trim();
                    }
                    FicheroXML.escribir(ruta, valores);
                    response.setContentType("text/xml;charset=UTF-8");
                    java.nio.file.Files.copy(java.nio.file.Paths.get(ruta), response.getOutputStream());

                } else {
                    throw new Exception("Accion no valida: " + accion);
                }

            } else {
                // Aqui el resto del grupo anadira CSV, JSON, XLS, RDF...
                throw new Exception("Formato no soportado todavia: " + formato);
            }

        } catch (Exception e) {
            request.setAttribute("error", e.getClass().getSimpleName() + ": " + e.getMessage());
            request.getRequestDispatcher("Error.jsp").forward(request, response);
        }
    }
}
