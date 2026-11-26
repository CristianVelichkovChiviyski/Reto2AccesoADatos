package entity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;

public class FicheroRDF {
	
	public static class ResultadoRDF {
        private Set<String> cabeceras = new LinkedHashSet<>();
        private List< Map<String, String> > filas = new ArrayList<>();

        public Set<String> getCabeceras() {
            return cabeceras;
        }

        public List< Map<String, String> > getFilas() {
            return filas;
        }
    }
	
	private String rutaArchivo;
    private static final String nameSpace = "http://ejemplo.org/"; // nombre unico del RDF
    private final String[] CAMPOS = {"Variable1", "Valor1", "PERIODO", "VALOR"}; // Cabeceras

	public FicheroRDF(String rutaArchivo) {
		super();
		this.rutaArchivo = rutaArchivo;
	}
	
	/**Lee y añade las cabeceras del archivo para que se muestren**/
	public ResultadoRDF leer() throws Exception {
        File archivo = new File(rutaArchivo);
        
        if (archivo.exists() && archivo.length() > 0) {
            Model model = RDFDataMgr.loadModel(rutaArchivo);
            ResultadoRDF resultado = new ResultadoRDF();

            // Se añaden las 4 cabeceras
            for (String campo : CAMPOS) {
                resultado.getCabeceras().add(campo);
            }

            // obtiene los recursos que sean de tipo registro
            Resource tipoRegistro = model.createResource(nameSpace + "Registro");
            ResIterator iter = model.listSubjectsWithProperty(RDF.type, tipoRegistro);

            // recorre y crea una fila por cada registro
            while (iter.hasNext()) {
                Resource recurso = iter.nextResource();
                Map<String, String> fila = new LinkedHashMap<>();

                // Obtiene el valor de cada campo
                for (String campo : CAMPOS) {
                    Property prop = model.createProperty(nameSpace, campo);
                    Statement stmt = recurso.getProperty(prop);
                    if (stmt != null) {
                        String valor = stmt.getObject().isLiteral() 
                                ? stmt.getObject().asLiteral().getLexicalForm() 
                                : stmt.getObject().toString();
                        fila.put(campo, valor);
                    }
                }
                if (!fila.isEmpty()) {
                    resultado.getFilas().add(fila);
                }
            }
            return resultado;
        } else {
            throw new Exception("Error: El archivo fichero.rdf no existe todavía.");
        }
    }
	
	/**Escribe una nueva linea en el RDF**/
	public void escribir(String[] datos) throws Exception {
		boolean flag = false;
        if (datos != null) {
            for (String dato : datos) {
                if (dato == null || dato.isBlank()) {
                    flag = true;
                    throw new Exception("Error: (*) Los campos no pueden estar vacíos para escribir.");
                }
            }
        } else {
            flag = true;
            throw new Exception("Error: No se proporcionaron datos.");
        }

        if (!flag) {
            Model model = ModelFactory.createDefaultModel();
            File archivo = new File(rutaArchivo);

            if (archivo.exists() && archivo.length() > 0) {
                model = RDFDataMgr.loadModel(rutaArchivo);
            }

            // crea un nuevo registro
            model.setNsPrefix("ex", nameSpace);
            Resource tipoRegistro = model.createResource(nameSpace + "Registro");
            Resource recurso = model.createResource(nameSpace + "registro/" + System.currentTimeMillis());
            recurso.addProperty(RDF.type, tipoRegistro);

            // asocia el valor de cada campo del formulario con el valor de la cabecera
            for (int i = 0; i < datos.length; i++) {
                Property prop = model.createProperty(nameSpace, CAMPOS[i]);
                recurso.addProperty(prop, datos[i]);
            }

            try (FileOutputStream out = new FileOutputStream(rutaArchivo)) {
                RDFDataMgr.write(out, model, Lang.RDFXML);
            } catch (Exception e) {
                throw new Exception("Error al guardar el archivo: " + e.getMessage());
            }
        }    
	}
}
