import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Gestionarchivo {

    private static final String CARPETA_DATOS = "datos_garantias";
    private static final String ARCHIVO_COMPUTADORAS = CARPETA_DATOS + File.separator + "computadoras.txt";

    public Gestionarchivo() {
        File carpeta = new File(CARPETA_DATOS);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }

    public void guardarComputadoras(List<Compus> computadoras) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_COMPUTADORAS))) {
            for (Compus comp : computadoras) {
                writer.write(comp.toFileString());
                writer.newLine();
            }
            System.out.println("Datos de computadoras guardados exitosamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar las computadoras: " + e.getMessage());
        }
    }

    public List<Compus> cargarComputadoras() {
        List<Compus> computadoras = new ArrayList<>();
        File archivo = new File(ARCHIVO_COMPUTADORAS);
        if (!archivo.exists()) {
            System.out.println("No se encontró el archivo de computadoras. Creando uno nuevo.");
            return computadoras; 
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Compus comp = Compus.fromFileString(line);
                if (comp != null) {
                    computadoras.add(comp);
                }
            }
            System.out.println("Datos de computadoras cargados exitosamente.");
        } catch (IOException e) {
            System.err.println("Error al cargar las computadoras: " + e.getMessage());
        }
        return computadoras;
    }

    private String getRutaArchivoHistorial(String serviceTag) {
        return CARPETA_DATOS + File.separator + "historial_" + serviceTag.replace(" ", "_") + ".txt";
    }

    public void guardarHistorial(String serviceTag, List<Historicofcompus> historial) {
        String rutaArchivo = getRutaArchivoHistorial(serviceTag);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (Historicofcompus fase : historial) {
                writer.write(fase.toFileString());
                writer.newLine();
            }
            // System.out.println("Historial para " + serviceTag + " guardado.");
        } catch (IOException e) {
            System.err.println("Error al guardar historial para " + serviceTag + ": " + e.getMessage());
        }
    }

    public List<Historicofcompus> cargarHistorial(String serviceTag) {
        List<Historicofcompus> historial = new ArrayList<>();
        String rutaArchivo = getRutaArchivoHistorial(serviceTag);
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            return historial; 
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Historicofcompus fase = Historicofcompus.fromFileString(line);
                if (fase != null) {
                    historial.add(fase);
                }
            }
            // System.out.println("Historial para " + serviceTag + " cargado.");
        } catch (IOException e) {
            System.err.println("Error al cargar historial para " + serviceTag + ": " + e.getMessage());
        }
        return historial;
    }

    public void eliminarArchivoHistorial(String serviceTag) {
        File archivoHistorial = new File(getRutaArchivoHistorial(serviceTag));
        if (archivoHistorial.exists()) {
            if (archivoHistorial.delete()) {
                System.out.println("Archivo de historial para " + serviceTag + " eliminado.");
            } else {
                System.err.println("No se pudo eliminar el archivo de historial para " + serviceTag + ".");
            }
        }
    }
}