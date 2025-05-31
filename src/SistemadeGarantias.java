import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class SistemadeGarantias {

    private Scanner scanner;
    private Gestionarchivo archivoManager;

    private Queue<Compus> colaInspeccion;
    private Queue<Compus> colaReparacion;
    private Queue<Compus> colaControlCalidad;
    private Queue<Compus> colaEntrega;

    private List<Compus> todasLasComputadoras;

    public SistemadeGarantias() {
        scanner = new Scanner(System.in);
        archivoManager = new Gestionarchivo();
        colaInspeccion = new LinkedList<>();
        colaReparacion = new LinkedList<>();
        colaControlCalidad = new LinkedList<>();
        colaEntrega = new LinkedList<>();
        todasLasComputadoras = new ArrayList<>();
        cargarEstadoInicial();
    }

    private void cargarEstadoInicial() {
        todasLasComputadoras = archivoManager.cargarComputadoras();
        for (Compus comp : todasLasComputadoras) {
            List<Historicofcompus> historialCargado = archivoManager.cargarHistorial(comp.getServiceTag());
            comp.getHistorial().addAll(historialCargado); 
            switch (comp.getEstadoActual()) {
                case "Inspección":
                    colaInspeccion.offer(comp);
                    break;
                case "Reparación":
                    colaReparacion.offer(comp);
                    break;
                case "ControlCalidad":
                    colaControlCalidad.offer(comp);
                    break;
                case "Entrega":
                    colaEntrega.offer(comp);
                    break;
            }
        }
        System.out.println("Sistema cargado. " + todasLasComputadoras.size() + " computadoras en registro.");
    }

    private void guardarEstadoActual() {
        archivoManager.guardarComputadoras(todasLasComputadoras);
        for (Compus comp : todasLasComputadoras) {
            archivoManager.guardarHistorial(comp.getServiceTag(), comp.getHistorial());
        }
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE GARANTIAS JESS SERVICES ---");
            System.out.println("1. INGRESAR NUEVA COMPUTADORA");
            System.out.println("2. ACTUALIZAR ESTADO DE LA COMPUTADORA");
            System.out.println("3. HISTORIAL ASOCIADO A LA COMPUTADORA");
            System.out.println("4. STATUS DE LAS COLAS");
            System.out.println("5. ENTREGA A CLIENTE DE COMPUTADORA");
            System.out.println("6. ELIMINAR COMPUTADORA REGISTRADA E HISTORIAL");
            System.out.println("0. SALIR DEL SISTEMA");
            System.out.println("---------------------------------------");
            System.out.print("Seleccione una opción: ");
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        registrarComputadora();
                        break;
                    case 2:
                        moverComputadoraEntreFases();
                        break;
                    case 3:
                        mostrarHistorialComputadora();
                        break;
                    case 4:
                        mostrarStatusColas();
                        break;
                    case 5:
                        entregarComputadora();
                        break;
                    case 6:
                        eliminarComputadora();
                        break;
                    case 0:
                        System.out.println("Saliendo del sistema. Guardando datos...");
                        guardarEstadoActual();
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                opcion = -1; 
            }
        } while (opcion != 0);
    }

    private void registrarComputadora() {
        System.out.println("\n--- Registrar Nueva Computadora ---");
        System.out.print("Service Tag: ");
        String serviceTag = scanner.nextLine().trim().toUpperCase();

        if (todasLasComputadoras.stream().anyMatch(c -> c.getServiceTag().equals(serviceTag))) {
            System.out.println("Error: Ya existe una computadora con ese Service Tag.");
            return;
        }

        System.out.print("Descripción del Problema: ");
        String descripcionProblema = scanner.nextLine();

        LocalDate fechaRecepcion = null;
        boolean fechaValida = false;
        while (!fechaValida) {
            System.out.print("Fecha de Recepción (YYYY-MM-DD): ");
            String fechaStr = scanner.nextLine();
            try {
                fechaRecepcion = LocalDate.parse(fechaStr);
                fechaValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha inválido. Por favor, use YYYY-MM-DD.");
            }
        }

        System.out.print("Nombre del Cliente: ");
        String nombreCliente = scanner.nextLine();
        System.out.print("Correo Electrónico del Cliente: ");
        String correoCliente = scanner.nextLine();
        System.out.print("Número de Teléfono del Cliente: ");
        String telefonoCliente = scanner.nextLine();

        Compus nuevaComputadora = new Compus(serviceTag, descripcionProblema, fechaRecepcion,
                                                     nombreCliente, correoCliente, telefonoCliente);
        todasLasComputadoras.add(nuevaComputadora);
        colaInspeccion.offer(nuevaComputadora);
        nuevaComputadora.setEstadoActual("Inspección"); 

        System.out.println("Computadora " + serviceTag + " registrada y enviada a Inspección.");
        archivoManager.guardarHistorial(serviceTag, nuevaComputadora.getHistorial());
        guardarEstadoActual(); 
    }

    private void moverComputadoraEntreFases() {
        System.out.println("\n--- ACTUALIZACIÓN DE SITUACIÓN DE EQUIPO ---");
        System.out.println("Seleccione el origen:");
        System.out.println("1. Inspección (" + colaInspeccion.size() + " pendientes)");
        System.out.println("2. Reparación (" + colaReparacion.size() + " pendientes)");
        System.out.println("3. Control de Calidad (" + colaControlCalidad.size() + " pendientes)");
        System.out.println("0. Volver al menú principal");

        System.out.print("Opción: ");
        String opcionStr = scanner.nextLine();

        Queue<Compus> colaOrigen = null;
        String nombreColaOrigen = "";

        switch (opcionStr) {
            case "1":
                colaOrigen = colaInspeccion;
                nombreColaOrigen = "Inspección";
                break;
            case "2":
                colaOrigen = colaReparacion;
                nombreColaOrigen = "Reparación";
                break;
            case "3":
                colaOrigen = colaControlCalidad;
                nombreColaOrigen = "Control de Calidad";
                break;
            case "0":
                return;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        if (colaOrigen.isEmpty()) {
            System.out.println("La cola de " + nombreColaOrigen + " está vacía.");
            return;
        }

        System.out.println("Computadoras en la cola de " + nombreColaOrigen + ":");
        int i = 1;
        List<Compus> computadorasEnCola = new ArrayList<>(colaOrigen);
        for (Compus comp : computadorasEnCola) {
            System.out.println(i++ + ". " + comp.getServiceTag() + " - Cliente: " + comp.getNombreCliente());
        }

        System.out.print("Ingrese el número de la computadora a procesar (o 0 para cancelar): ");
        int indiceSeleccionado;
        try {
            indiceSeleccionado = Integer.parseInt(scanner.nextLine());
            if (indiceSeleccionado <= 0 || indiceSeleccionado > computadorasEnCola.size()) {
                System.out.println("Selección inválida o cancelada.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
            return;
        }

        Compus compSeleccionada = computadorasEnCola.get(indiceSeleccionado - 1);
        
        colaOrigen.remove(compSeleccionada);


        switch (nombreColaOrigen) {
            case "Inspección":
                procesarInspeccion(compSeleccionada);
                break;
            case "Reparación":
                procesarReparacion(compSeleccionada);
                break;
            case "Control de Calidad":
                procesarControlCalidad(compSeleccionada);
                break;
        }
        guardarEstadoActual();
    }

    private void procesarInspeccion(Compus comp) {
        System.out.println("\n--- Procesando Inspección para " + comp.getServiceTag() + " ---");
        System.out.print("Ingrese el diagnóstico: ");
        String diagnostico = scanner.nextLine();

        comp.agregarFaseHistorial(new Historicofcompus(LocalDate.now(), "Inspección", "Diagnóstico: " + diagnostico));

        System.out.print("¿La computadora puede ser reparada? (si/no): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();

        if ("si".equals(respuesta)) {
            colaReparacion.offer(comp);
            comp.setEstadoActual("Reparación");
            System.out.println(comp.getServiceTag() + " movida a la cola de Reparación.");
        } else {
            colaEntrega.offer(comp);
            comp.setEstadoActual("Entrega");
            System.out.println(comp.getServiceTag() + " movida a la cola de Entrega (no reparable).");
        }
    }

    private void procesarReparacion(Compus comp) {
        System.out.println("\n--- Procesando Reparación para " + comp.getServiceTag() + " ---");
        System.out.print("Nombre del técnico que realizó la reparación: ");
        String tecnico = scanner.nextLine();
        System.out.print("Descripción del proceso de reparación realizado: ");
        String procesoReparacion = scanner.nextLine();

        comp.agregarFaseHistorial(new Historicofcompus(LocalDate.now(), "Reparación",
                "Técnico: " + tecnico + ", Proceso: " + procesoReparacion));
        colaControlCalidad.offer(comp);
        comp.setEstadoActual("ControlCalidad");
        System.out.println(comp.getServiceTag() + " movida a la cola de Control de Calidad.");
    }

    private void procesarControlCalidad(Compus comp) {
        System.out.println("\n--- Procesando Control de Calidad para " + comp.getServiceTag() + " ---");
        System.out.print("¿La reparación fue correcta? (si/no): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();

        if ("si".equals(respuesta)) {
            comp.agregarFaseHistorial(new Historicofcompus(LocalDate.now(), "ControlCalidad", "Reparación aprobada."));
            colaEntrega.offer(comp);
            comp.setEstadoActual("Entrega");
            System.out.println(comp.getServiceTag() + " movida a la cola de Entrega.");
        } else {
            comp.agregarFaseHistorial(new Historicofcompus(LocalDate.now(), "ControlCalidad", "Reparación no aprobada. Reenvío a reparación."));
            colaReparacion.offer(comp);
            comp.setEstadoActual("Reparación");
            System.out.println(comp.getServiceTag() + " reenviada a la cola de Reparación.");
        }
    }

    private void entregarComputadora() {
        System.out.println("\n--- Entregar Computadora ---");
        if (colaEntrega.isEmpty()) {
            System.out.println("No hay computadoras listas para entregar.");
            return;
        }

        System.out.println("Computadoras en la cola de Entrega:");
        int i = 1;
        List<Compus> computadorasEnEntrega = new ArrayList<>(colaEntrega); 
        for (Compus comp : computadorasEnEntrega) {
            System.out.println(i++ + ". " + comp.getServiceTag() + " - Cliente: " + comp.getNombreCliente());
        }

        System.out.print("Ingrese el número de la computadora a entregar (o 0 para cancelar): ");
        int indiceSeleccionado;
        try {
            indiceSeleccionado = Integer.parseInt(scanner.nextLine());
            if (indiceSeleccionado <= 0 || indiceSeleccionado > computadorasEnEntrega.size()) {
                System.out.println("Selección inválida o cancelada.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
            return;
        }

        Compus compAEntregar = computadorasEnEntrega.get(indiceSeleccionado - 1);
        colaEntrega.remove(compAEntregar); 

        compAEntregar.agregarFaseHistorial(new Historicofcompus(LocalDate.now(), "Entrega", "Computadora entregada al cliente."));
        compAEntregar.setEstadoActual("Entregado"); 
        System.out.println("Computadora " + compAEntregar.getServiceTag() + " entregada exitosamente.");
        guardarEstadoActual(); 
    }


    private void mostrarHistorialComputadora() {
        System.out.println("\n--- Mostrar Historial de Computadora ---");
        System.out.print("Ingrese el Service Tag de la computadora: ");
        String serviceTag = scanner.nextLine().trim().toUpperCase();

        Compus comp = todasLasComputadoras.stream()
                               .filter(c -> c.getServiceTag().equals(serviceTag))
                               .findFirst()
                               .orElse(null);

        if (comp != null) {
            System.out.println("\nHistorial para Service Tag: " + comp.getServiceTag());
            System.out.println("Descripción del problema: " + comp.getDescripcionProblema());
            System.out.println("Cliente: " + comp.getNombreCliente());
            System.out.println("Estado Actual: " + comp.getEstadoActual());
            System.out.println("\n--- Detalles del Historial ---");
            if (comp.getHistorial().isEmpty()) {
                System.out.println("No hay historial registrado para esta computadora.");
            } else {
                comp.getHistorial().forEach(System.out::println);
            }
        } else {
            System.out.println("Computadora con Service Tag " + serviceTag + " no encontrada.");
        }
    }

    private void mostrarStatusColas() {
        System.out.println("\n--- Status Actualizado ---");

        System.out.println("\n--- Recibidas/En Inspección (" + colaInspeccion.size() + " computadoras) ---");
        if (colaInspeccion.isEmpty()) {
            System.out.println("Vacía.");
        } else {
            colaInspeccion.forEach(c -> System.out.println("  - " + c.getServiceTag() + " (" + c.getNombreCliente() + ")"));
        }

        System.out.println("\n--- En Reparación (" + colaReparacion.size() + " computadoras) ---");
        if (colaReparacion.isEmpty()) {
            System.out.println("Vacía.");
        } else {
            colaReparacion.forEach(c -> System.out.println("  - " + c.getServiceTag() + " (" + c.getNombreCliente() + ")"));
        }

        System.out.println("\n--- Revisión Control de Calidad (" + colaControlCalidad.size() + " computadoras) ---");
        if (colaControlCalidad.isEmpty()) {
            System.out.println("Vacía.");
        } else {
            colaControlCalidad.forEach(c -> System.out.println("  - " + c.getServiceTag() + " (" + c.getNombreCliente() + ")"));
        }

        System.out.println("\n--- Listo para Entrega (" + colaEntrega.size() + " computadoras) ---");
        if (colaEntrega.isEmpty()) {
            System.out.println("Vacía.");
        } else {
            colaEntrega.forEach(c -> System.out.println("  - " + c.getServiceTag() + " (" + c.getNombreCliente() + ")"));
        }

        long entregadas = todasLasComputadoras.stream().filter(c -> "Entregado".equals(c.getEstadoActual())).count();
        System.out.println("\n--- Computadoras Entregadas (" + entregadas + " en total) ---");
        todasLasComputadoras.stream()
                .filter(c -> "Entregado".equals(c.getEstadoActual()))
                .forEach(c -> System.out.println("  - " + c.getServiceTag() + " (" + c.getNombreCliente() + ")"));
    }

    private void eliminarComputadora() {
        System.out.println("\n--- Eliminar Computadora Registrada ---");
        System.out.print("Ingrese el Service Tag de la computadora a eliminar: ");
        String serviceTag = scanner.nextLine().trim().toUpperCase();

        Compus compAEliminar = todasLasComputadoras.stream()
                                         .filter(c -> c.getServiceTag().equals(serviceTag))
                                         .findFirst()
                                         .orElse(null);

        if (compAEliminar == null) {
            System.out.println("Computadora con Service Tag " + serviceTag + " no encontrada.");
            return;
        }

        System.out.print("¿Está seguro de que desea eliminar la computadora " + serviceTag + " y todo su historial? (si/no): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();

        if (!"si".equals(confirmacion)) {
            System.out.println("Eliminación cancelada.");
            return;
        }

        colaInspeccion.remove(compAEliminar);
        colaReparacion.remove(compAEliminar);
        colaControlCalidad.remove(compAEliminar);
        colaEntrega.remove(compAEliminar);

        todasLasComputadoras.remove(compAEliminar);

        archivoManager.eliminarArchivoHistorial(serviceTag);

        System.out.println("El historico de la computadora " + serviceTag + " y su historial han sido eliminado de forma exitosa.");
        guardarEstadoActual();
    }

    public static void main(String[] args) {
        SistemadeGarantias sistema = new SistemadeGarantias();
        sistema.mostrarMenu();
    }
}