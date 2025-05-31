import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Historicofcompus {
    private LocalDate fecha;
    private String fase;
    private String detalles; 

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Historicofcompus(LocalDate fecha, String fase, String detalles) {
        this.fecha = fecha;
        this.fase = fase;
        this.detalles = detalles;
    }

    
    public LocalDate getFecha() {
        return fecha;
    }

    public String getFase() {
        return fase;
    }

    public String getDetalles() {
        return detalles;
    }

    public String toFileString() {
        return fecha.format(DateTimeFormatter.ISO_LOCAL_DATE) + ";" + fase + ";" + detalles;
    }

         public static Historicofcompus fromFileString(String line) {
        String[] parts = line.split(";", 3);
        if (parts.length == 3) {
            try {
                LocalDate fecha = LocalDate.parse(parts[0], DateTimeFormatter.ISO_LOCAL_DATE);
                String fase = parts[1];
                String detalles = parts[2];
                return new Historicofcompus(fecha, fase, detalles);
            } catch (DateTimeParseException e) {
                System.err.println("Error al parsear fecha de historial desde archivo: " + parts[0] + " - " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Fecha: " + fecha.format(DATE_FORMATTER) +
               ", Fase: " + fase +
               ", Detalles: " + detalles;
    }
}
