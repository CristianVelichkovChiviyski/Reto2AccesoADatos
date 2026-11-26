package entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class FicheroJSON {

    public static class ResultadoJSON {
        private Set<String> cabeceras = new LinkedHashSet<>();
        private List< Map<String, String> > filas = new ArrayList<>();

        public Set<String> getCabeceras() {
            return cabeceras;
        }

        public List< Map<String, String> > getFilas() {
            return filas;
        }
    }

    /** Leer archivodatos.json y adaptar cabeceras y filas */
    public static ResultadoJSON leer(String ruta) throws Exception {
        File fichero = new File(ruta);
        if (!fichero.exists()) {
            throw new Exception("No existe el fichero JSON: " + ruta);
        }

        Gson gson = new Gson();
        Type tipoLista = new TypeToken< List< LinkedHashMap<String, Object> > >() {}.getType();

        try (Reader reader = new InputStreamReader(new FileInputStream(fichero), StandardCharsets.UTF_8)) {
            List< Map<String, Object> > listaCruda = gson.fromJson(reader, tipoLista);
            if (listaCruda == null) {
                listaCruda = new ArrayList<>();
            }

            ResultadoJSON res = new ResultadoJSON();

            if (!listaCruda.isEmpty()) {
                res.cabeceras.addAll(listaCruda.get(0).keySet());
            }

            for (Map<String, Object> item : listaCruda) {
                Map<String, String> filaString = new LinkedHashMap<>();
                for (String clave : res.cabeceras) {
                    Object val = item.get(clave);
                    if (val == null) {
                        filaString.put(clave, "");
                    } else if (val instanceof Double && ((Double) val) % 1 == 0) {
                        filaString.put(clave, String.valueOf(((Double) val).longValue()));
                    } else {
                        filaString.put(clave, String.valueOf(val));
                    }
                }
                res.filas.add(filaString);
            }
            return res;
        }
    }

    /** Añade un registro nuevo formato JSON */
    public static void escribir(String ruta, String[] valores) throws Exception {
        File fichero = new File(ruta);
        if (!fichero.exists()) {
            throw new Exception("No existe el fichero JSON: " + ruta);
        }

        Gson gson = new Gson();
        Type tipoLista = new TypeToken< List< LinkedHashMap<String, Object> > >() {}.getType();
        List< Map<String, Object> > lista;

        try (Reader reader = new InputStreamReader(new FileInputStream(fichero), StandardCharsets.UTF_8)) {
            lista = gson.fromJson(reader, tipoLista);
            if (lista == null || lista.isEmpty()) {
                throw new Exception("El JSON esta vacio y no se pueden detectar las columnas");
            }
        }

        for (Map<String, Object> fila : lista) {
            for (Map.Entry<String, Object> entry : fila.entrySet()) {
                if (entry.getValue() instanceof Double && ((Double) entry.getValue()) % 1 == 0) {
                    entry.setValue(((Double) entry.getValue()).longValue());
                }
            }
        }

        Set<String> claves = lista.get(0).keySet();
        Map<String, Object> nuevoRegistro = new LinkedHashMap<>();
        int i = 0;
        for (String clave : claves) {
            String dato = (i < valores.length) ? valores[i] : "";
            nuevoRegistro.put(clave, convertirTipo(dato));
            i++;
        }
        lista.add(nuevoRegistro);

        Gson gsonBonito = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(fichero), StandardCharsets.UTF_8)) {
            gsonBonito.toJson(lista, writer);
        }
    }

    /** Convierte a entero o decimal si el usuario escribió un número (formato del JSON) */
    private static Object convertirTipo(String valor) {
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e1) {
            try {
                return Double.parseDouble(valor.replace(",", "."));
            } catch (NumberFormatException e2) {
                return valor;
            }
        }
    }
}