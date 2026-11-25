package entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler SAX generico: no necesita conocer los nombres de las etiquetas.
 *
 * Se guia por el nivel de anidamiento:
 *   nivel 1 -> raiz           (<emisiones>)
 *   nivel 2 -> un registro    (<registro>)   = una fila
 *   nivel 3 -> un dato        (<anio>2022</anio>) = una celda
 *              el nombre de la etiqueta es la cabecera de la columna
 */
public class ManejadorGenerico extends DefaultHandler {

    private int profundidad = 0;
    private StringBuilder texto = new StringBuilder();

    private String nombreRaiz;
    private String nombreRegistro;

    private Set<String> cabeceras = new LinkedHashSet<>();
    private List<Map<String, String>> filas = new ArrayList<>();
    private Map<String, String> filaActual;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs) {
        profundidad++;
        texto.setLength(0); // vaciamos el texto acumulado

        if (profundidad == 1) {
            nombreRaiz = qName;
        } else if (profundidad == 2) {
            nombreRegistro = qName;
            filaActual = new LinkedHashMap<>(); // empieza una fila nueva
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        // El parser puede trocear el texto en varias llamadas: acumulamos
        texto.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (profundidad == 3) { // termina un dato
            cabeceras.add(qName);
            filaActual.put(qName, texto.toString().trim());
        } else if (profundidad == 2) { // termina un registro
            filas.add(filaActual);
        }
        profundidad--;
    }

    public String getNombreRaiz() {
        return nombreRaiz;
    }

    public String getNombreRegistro() {
        return nombreRegistro;
    }

    public Set<String> getCabeceras() {
        return cabeceras;
    }

    public List<Map<String, String>> getFilas() {
        return filas;
    }
}