package entity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.helpers.AttributesImpl;

/**
 * Lectura y escritura de ficheros XML usando solo SAX.
 *
 *  - LECTURA: SAXParser + ManejadorGenerico (el parser nos avisa de lo que encuentra).
 *  - ESCRITURA: TransformerHandler (nosotros le decimos lo que tiene que escribir,
 *    con los mismos eventos startElement / characters / endElement).
 */
public class FicheroXML {

    /** Lee el XML con SAX y devuelve el handler con cabeceras y filas. */
    public static ManejadorGenerico leer(String ruta) throws Exception {
        File fichero = new File(ruta);
        if (!fichero.exists()) {
            throw new Exception("No existe el fichero XML: " + ruta);
        }
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        ManejadorGenerico manejador = new ManejadorGenerico();
        parser.parse(fichero, manejador);
        return manejador;
    }

    /**
     * Anade un registro nuevo al XML.
     * SAX no puede "anadir al final", asi que: leemos todo, anadimos la fila
     * en memoria y reescribimos el fichero completo.
     *
     * @param valores valores del formulario, en el mismo orden que las columnas del XML
     */
    public static void escribir(String ruta, String[] valores) throws Exception {
        ManejadorGenerico m = leer(ruta);

        if (m.getCabeceras().isEmpty()) {
            throw new Exception("El XML no tiene registros de los que tomar las columnas");
        }

        // Fila nueva: valor 1 -> primera columna, valor 2 -> segunda, etc.
        Map<String, String> nueva = new LinkedHashMap<>();
        int i = 0;
        for (String cabecera : m.getCabeceras()) {
            nueva.put(cabecera, i < valores.length ? valores[i] : "");
            i++;
        }
        m.getFilas().add(nueva);

        // Generamos primero el XML completo en memoria: si algo falla aqui,
        // el fichero original no se toca. Despues lo volcamos de una vez.
        ByteArrayOutputStream memoria = new ByteArrayOutputStream();
        escribirFichero(memoria, m);

        try (FileOutputStream salida = new FileOutputStream(new File(ruta))) {
            salida.write(memoria.toByteArray());
        } // se cierra solo, sin dejar el fichero bloqueado (importante en Windows)
    }

    private static void escribirFichero(OutputStream fichero, ManejadorGenerico m) throws Exception {
        SAXTransformerFactory stf = crearFactoria();
        TransformerHandler th = stf.newTransformerHandler();
        th.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        th.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
        th.setResult(new StreamResult(fichero));

        AttributesImpl vacio = new AttributesImpl(); // etiquetas sin atributos

        th.startDocument();
        th.startElement("", "", m.getNombreRaiz(), vacio);

        for (Map<String, String> fila : m.getFilas()) {
            th.startElement("", "", m.getNombreRegistro(), vacio);
            for (String cabecera : m.getCabeceras()) {
                String valor = fila.getOrDefault(cabecera, "");
                th.startElement("", "", cabecera, vacio);
                th.characters(valor.toCharArray(), 0, valor.length());
                th.endElement("", "", cabecera);
            }
            th.endElement("", "", m.getNombreRegistro());
        }

        th.endElement("", "", m.getNombreRaiz());
        th.endDocument();
    }

    /**
     * Devuelve la factoria de transformacion del propio JDK.
     * Si alguna dependencia del proyecto trae otro motor XML (Xalan, Saxon...),
     * SAXTransformerFactory.newInstance() usaria ese en lugar del del JDK.
     * Forzamos el del JDK; si no se pudiera, usamos el que haya por defecto.
     */
    private static SAXTransformerFactory crearFactoria() {
        try {
            return (SAXTransformerFactory) TransformerFactory.newInstance(
                    "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
        } catch (Throwable e) {
            return (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        }
    }

    /** Escapa texto para pintarlo en HTML sin que se interprete como etiquetas. */
    public static String escapar(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
