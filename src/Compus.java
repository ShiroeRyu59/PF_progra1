
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Compus {
    private String serviceTag;
    private String descripcionProblema;
    private LocalDate fechaRecepcion;
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private String estadoActual;
    private List<Historicofcompus> historial;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Compus(String serviceTag, String descripcionProblema, LocalDate fechaRecepcion,
                       String nombreCliente, String correoCliente, String telefonoCliente) {
        this.serviceTag = serviceTag;
        this.descripcionProblema = descripcionProblema;
        this.fechaRecepcion = fechaRecepcion;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;
        this.telefonoCliente = telefonoCliente;
        this.estadoActual = "Recepción"; 
        this.historial = new ArrayList<>();
        this.historial.add(new Historicofcompus(fechaRecepcion, "Recepción", "Computadora recibida."));
    }

    public String getServiceTag() {
        return serviceTag;
    }

    public void setServiceTag(String serviceTag) {
        this.serviceTag = serviceTag;
    }

    public String getDescripcionProblema() {
        return descripcionProblema;
    }

    public void setDescripcionProblema(String descripcionProblema) {
        this.descripcionProblema = descripcionProblema;
    }

    public LocalDate getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(LocalDate fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public List<Historicofcompus> getHistorial() {
        return historial;
    }

    public void agregarFaseHistorial(Historicofcompus fase) {
        this.historial.add(fase);
    }

    public String toFileString() {
        return serviceTag + ";" +
               descripcionProblema + ";" +
               fechaRecepcion.format(DateTimeFormatter.ISO_LOCAL_DATE) + ";" +
               nombreCliente + ";" +
               correoCliente + ";" +
               telefonoCliente + ";" +
               estadoActual;
    }


        public static Compus fromFileString(String line) {
        String[] parts = line.split(";", 7);
        if (parts.length == 7) {
            String serviceTag = parts[0];
            String descripcionProblema = parts[1];
            LocalDate fechaRecepcion = null;
            try {
                fechaRecepcion = LocalDate.parse(parts[2], DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                System.err.println("Error al parsear fecha de recepción desde archivo: " + parts[2] + " - " + e.getMessage());
                return null;
            }
            String nombreCliente = parts[3];
            String correoCliente = parts[4];
            String telefonoCliente = parts[5];
            String estadoActual = parts[6];

            Compus comp = new Compus(serviceTag, descripcionProblema, fechaRecepcion,
                                               nombreCliente, correoCliente, telefonoCliente);
            comp.setEstadoActual(estadoActual);
            comp.historial.clear();
            return comp;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Service Tag: " + serviceTag +
               "\n  Cliente: " + nombreCliente +
               "\n  Problema: " + descripcionProblema +
               "\n  Fecha Recepción: " + fechaRecepcion.format(DATE_FORMATTER) +
               "\n  Estado Actual: " + estadoActual;
    }
}