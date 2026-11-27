package entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FicheroCSV {

    public static class ResultadoCSV {
        private Set<String> cabeceras = new LinkedHashSet<>();
        private List< Map<String, String> > filas = new ArrayList<>();

        public Set<String> getCabeceras() {
            return cabeceras;
        }

        public List< Map<String, String> > getFilas() {
            return filas;
        }
    }

    /** Detecta automaticamente si el CSV usa ';' o ',' como separador */
    private static String detectarSeparador(String lineaCabecera) {
        int puntoYComa = lineaCabecera.split(";", -1).length;
        int coma = lineaCabecera.split(",", -1).length;
        return (puntoYComa >= coma) ? ";" : ",";
    }

    /** Divide una linea CSV respetando posibles campos entre comillas ("...") */
    private static String[] parsearLinea(String linea, String separador) {
        String regex = separador + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        String[] partes = linea.split(regex, -1);
        for (int i = 0; i < partes.length; i++) {
            partes[i] = partes[i].trim().replaceAll("^\"|\"$", "");
        }
        return partes;
    }

    /** Lee archivodatos.csv y devuelve cabeceras y filas para AccesoDatosA.jsp */
    public static ResultadoCSV leer(String ruta) throws Exception {
        File fichero = new File(ruta);
        if (!fichero.exists()) {
            throw new Exception("No existe el fichero CSV: " + ruta);
        }

        ResultadoCSV res = new ResultadoCSV();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fichero), StandardCharsets.UTF_8))) {

            String lineaCabecera = br.readLine();
            if (lineaCabecera == null || lineaCabecera.trim().isEmpty()) {
                throw new Exception("El fichero CSV esta vacio");
            }

            // Elimina el caracter BOM (\uFEFF) que tiene archivodatos.csv al inicio
            lineaCabecera = lineaCabecera.replace("\uFEFF", "");
            String separador = detectarSeparador(lineaCabecera);

            String[] cols = parsearLinea(lineaCabecera, separador);
            List<String> listaCabeceras = new ArrayList<>();
            for (String col : cols) {
                res.cabeceras.add(col);
                listaCabeceras.add(col);
            }

            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] valores = parsearLinea(linea, separador);
                Map<String, String> filaMap = new LinkedHashMap<>();

                for (int i = 0; i < listaCabeceras.size(); i++) {
                    String valor = (i < valores.length) ? valores[i] : "";
                    filaMap.put(listaCabeceras.get(i), valor);
                }
                res.filas.add(filaMap);
            }
        }
        return res;
    }

    /** Anade una nueva fila al final de archivodatos.csv */
    public static void escribir(String ruta, String[] valores) throws Exception {
        File fichero = new File(ruta);
        if (!fichero.exists()) {
            throw new Exception("No existe el fichero CSV: " + ruta);
        }

        String separador;
        int numColumnas;

        // 1. Leemos la cabecera para detectar separador (;) y numero de columnas (4)
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fichero), StandardCharsets.UTF_8))) {
            String lineaCabecera = br.readLine();
            if (lineaCabecera == null || lineaCabecera.trim().isEmpty()) {
                throw new Exception("El CSV esta vacio y no se pueden detectar las columnas");
            }
            lineaCabecera = lineaCabecera.replace("\uFEFF", "");
            separador = detectarSeparador(lineaCabecera);
            numColumnas = parsearLinea(lineaCabecera, separador).length;
        }

        // 2. Comprobamos si el fichero termina en salto de linea
        boolean necesitaSalto = false;
        if (fichero.length() > 0) {
            try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {
                raf.seek(fichero.length() - 1);
                byte ultimoByte = raf.readByte();
                if (ultimoByte != '\n' && ultimoByte != '\r') {
                    necesitaSalto = true;
                }
            }
        }

        // 3. Construimos la linea con los 4 datos del formulario separados por ';'
        StringBuilder nuevaLinea = new StringBuilder();
        for (int i = 0; i < numColumnas; i++) {
            if (i > 0) {
                nuevaLinea.append(separador);
            }
            String dato = (i < valores.length && valores[i] != null) ? valores[i].trim() : "";
            dato = dato.replace("\r", " ").replace("\n", " ");
            if (dato.contains(separador)) {
                dato = "\"" + dato + "\"";
            }
            nuevaLinea.append(dato);
        }

        // 4. Escribimos al final del CSV
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fichero, true), StandardCharsets.UTF_8))) {
            if (necesitaSalto) {
                bw.newLine();
            }
            bw.write(nuevaLinea.toString());
            bw.newLine();
        }
    }
}