package app;

import java.util.Scanner;
import modelo.Estudiante;
import negocio.ArbolEstudiantes;
import negocio.NodoUnidad;
import enums.TipoRecorrido;

public class Main {

    public static void main(String[] args) {

        Contexto c = new Contexto();

        try (Scanner in = new Scanner(System.in)) {

            boolean ejecutando = true;

            while (ejecutando) {

                mostrarMenu();

                if (!in.hasNextLine()) {
                    break;
                }

                String opcion = in.nextLine().trim();

                try {

                    switch (opcion) {

                        case "1":
                            consultarJerarquia(c, in);
                            break;

                        case "2":
                            registrarOBuscarEstudiante(c, in);
                            break;

                        case "3":
                            mostrarRecorridosYEstadisticas(c, in);
                            break;

                        case "4":
                            modificarOEliminarEstudiante(c, in);
                            break;

                        case "5":
                            gestionarUnidadesYTransformacion(c, in);
                            break;

                        case "6":
                            mostrarEstadoSistema(c);
                            break;

                        case "7":
                            c.reiniciar();
                            System.out.println(
                                "\nDatos reiniciados correctamente."
                            );
                            break;

                        case "8":
                            RetoIntegrador.ejecutar(c, System.out);
                            break;

                        case "0":
                            System.out.println(
                                "\nFinalizando SJA-UTA. Hasta pronto."
                            );
                            ejecutando = false;
                            break;

                        default:
                            System.out.println(
                                "\nOpcion invalida. Intente nuevamente."
                            );
                    }

                } catch (NumberFormatException e) {

                    System.out.println(
                        "\nError: ingrese un valor numerico valido."
                    );

                } catch (IllegalArgumentException | IllegalStateException e) {

                    System.out.println(
                        "\nAviso: " + e.getMessage()
                    );
                }
            }
        }
    }

    private static void mostrarMenu() {

        System.out.println("\n======================================");
        System.out.println("    SISTEMA JERARQUICO ACADEMICO UTA");
        System.out.println("           APE3 - GRUPO 4");
        System.out.println("======================================");

        System.out.println("1. Jerarquia academica");
        System.out.println("2. Insercion y busqueda de estudiantes");
        System.out.println("3. Recorridos y estadisticas");
        System.out.println("4. Modificacion y eliminacion de estudiantes");
        System.out.println("5. Unidades y transformacion binaria");
        System.out.println("6. Estado general del sistema");
        System.out.println("7. Reiniciar datos de ejemplo");
        System.out.println("8. Ejecutar reto integrador");
        System.out.println("0. Salir");

        System.out.println("======================================");
        System.out.print("Seleccione una opcion: ");
    }

    private static void consultarJerarquia(Contexto c, Scanner in) {

        System.out.println("\n===== JERARQUIA ACADEMICA =====");

        System.out.println(c.organizacion.mostrarJerarquia());

        System.out.println(
            "Altura organizacional: " + c.organizacion.altura()
        );

        System.out.println(
            "Total de hojas: " + c.organizacion.contarHojas()
        );

        System.out.print("\nCodigo de unidad a consultar: ");
        String codigo = in.nextLine().trim();

        NodoUnidad unidad = c.organizacion.buscar(codigo);

        if (unidad == null) {
            System.out.println("La unidad academica no existe.");
            return;
        }

        System.out.println("Unidad encontrada: " + codigo);

        System.out.println(
            "Nivel: " + c.organizacion.nivel(codigo)
        );

        System.out.println(
            "Grado: " + c.organizacion.grado(codigo)
        );

        // Integrante Cris:
        // Completar registro de unidades y pruebas de jerarquia.
    }

    private static void registrarOBuscarEstudiante(
        Contexto c, Scanner in
    ) {

        System.out.println("\n===== ESTUDIANTES =====");
        System.out.println("1. Insertar estudiante");
        System.out.println("2. Buscar estudiante");
        System.out.print("Seleccione una opcion: ");

        String opcion = in.nextLine().trim();

        if (!opcion.equals("1") && !opcion.equals("2")) {
            System.out.println("Opcion invalida.");
            return;
        }

        System.out.print("Matricula: ");
        int matricula = Integer.parseInt(in.nextLine().trim());

        ArbolEstudiantes abb = c.estudiantes();

        if (opcion.equals("2")) {

            Estudiante estudiante = abb.buscar(matricula);

            if (estudiante == null) {
                System.out.println("Estudiante no encontrado.");
            } else {
                System.out.println(
                    "Estudiante encontrado: " + estudiante
                );
            }

            return;
        }

        System.out.print("Cedula: ");
        String cedula = in.nextLine().trim();

        System.out.print("Nombres: ");
        String nombres = in.nextLine().trim();

        System.out.print("Promedio: ");
        double promedio = Double.parseDouble(
            in.nextLine().trim()
        );

        Estudiante estudiante = new Estudiante(
            matricula, cedula, nombres, promedio
        );

        boolean insertado = abb.insertar(estudiante);

        if (insertado) {
            System.out.println(
                "Estudiante registrado correctamente."
            );
        } else {
            System.out.println(
                "La matricula ya se encuentra registrada."
            );
        }

        // Integrante Andres:
        // Validar duplicados y datos invalidos en el modelo.
    }

    private static void mostrarRecorridosYEstadisticas(
        Contexto c, Scanner in
    ) {

        System.out.println("\n===== RECORRIDOS Y ESTADISTICAS =====");

        ArbolEstudiantes abb = c.estudiantes();

        System.out.println("1. Mostrar recorridos");
        System.out.println("2. Mostrar estadisticas");
        System.out.println("3. Mostrar todo");
        System.out.print("Seleccione una opcion: ");

        String opcion = in.nextLine().trim();

        if (opcion.equals("1") || opcion.equals("3")) {

            for (TipoRecorrido tipo : TipoRecorrido.values()) {

                System.out.println(
                    "\n--- " + tipo + " ---"
                );

                System.out.println(
                    abb.trazaRecorrido(tipo)
                );
            }
        }

        if (opcion.equals("2") || opcion.equals("3")) {

            System.out.println("\n--- ESTADISTICAS ---");

            System.out.println(
                "Cantidad: " + abb.contar()
            );

            System.out.println(
                "Altura: " + abb.altura()
            );

            if (abb.estaVacio()) {

                System.out.println(
                    "El paralelo no tiene estudiantes."
                );

            } else {

                System.out.println(
                    "Menor matricula: " + abb.menor()
                );

                System.out.println(
                    "Mayor matricula: " + abb.mayor()
                );

                System.out.printf(
                    "Promedio general: %.2f%n",
                    abb.promedioGeneral()
                );
            }
        }

        if (!opcion.equals("1")
            && !opcion.equals("2")
            && !opcion.equals("3")) {

            System.out.println("Opcion invalida.");
        }

        // Integrante Lenin:
        // Verificar recorridos, trazas y casos limite.
    }

    private static void modificarOEliminarEstudiante(
        Contexto c, Scanner in
    ) {

        System.out.println("\n===== MODIFICAR O ELIMINAR =====");

        System.out.println("1. Modificar estudiante");
        System.out.println("2. Eliminar estudiante");
        System.out.print("Seleccione una opcion: ");

        String opcion = in.nextLine().trim();

        if (!opcion.equals("1") && !opcion.equals("2")) {
            System.out.println("Opcion invalida.");
            return;
        }

        System.out.print("Matricula: ");
        int matricula = Integer.parseInt(
            in.nextLine().trim()
        );

        ArbolEstudiantes abb = c.estudiantes();

        if (opcion.equals("2")) {

            System.out.println(
                "\nInorden antes de eliminar:"
            );

            System.out.println(
                abb.recorrer(TipoRecorrido.INORDEN)
            );

            boolean eliminado = abb.eliminar(matricula);

            if (eliminado) {

                System.out.println(
                    "Estudiante eliminado correctamente."
                );

            } else {

                System.out.println(
                    "La matricula no existe."
                );
            }

            System.out.println(
                "\nInorden despues de eliminar:"
            );

            System.out.println(
                abb.recorrer(TipoRecorrido.INORDEN)
            );

            return;
        }

        System.out.print("Nueva cedula: ");
        String cedula = in.nextLine().trim();

        System.out.print("Nuevos nombres: ");
        String nombres = in.nextLine().trim();

        System.out.print("Nuevo promedio: ");
        double promedio = Double.parseDouble(
            in.nextLine().trim()
        );

        boolean modificado = abb.modificar(
            matricula, cedula, nombres, promedio
        );

        if (modificado) {

            System.out.println(
                "Estudiante modificado correctamente."
            );

        } else {

            System.out.println(
                "La matricula no existe."
            );
        }

        // Integrante Josue:
        // Comprobar modificacion y los tres casos de eliminacion.
    }

    private static void gestionarUnidadesYTransformacion(
        Contexto c, Scanner in
    ) {

        System.out.println("\n===== UNIDADES ACADEMICAS =====");

        System.out.println("1. Transformar a binario");
        System.out.println("2. Eliminar unidad academica");
        System.out.print("Seleccione una opcion: ");

        String opcion = in.nextLine().trim();

        if (opcion.equals("1")) {

            System.out.println(
                "\nTransformacion hijo izquierdo - hermano derecho:"
            );

            System.out.println(
                c.organizacion.transformarABinario()
            );

            return;
        }

        if (!opcion.equals("2")) {
            System.out.println("Opcion invalida.");
            return;
        }

        System.out.print("Codigo de unidad: ");
        String codigo = in.nextLine().trim();

        NodoUnidad unidad = c.organizacion.buscar(codigo);

        if (unidad == null) {
            System.out.println(
                "La unidad academica no existe."
            );
            return;
        }

        if (unidad.getEstudiantes() != null
            && !unidad.getEstudiantes().estaVacio()) {

            System.out.println(
                "No se puede eliminar un paralelo con estudiantes."
            );

            return;
        }

        boolean eliminado = c.organizacion.eliminar(codigo);

        if (eliminado) {

            System.out.println(
                "Unidad academica eliminada correctamente."
            );

        } else {

            System.out.println(
                "No se puede eliminar la unidad solicitada."
            );
        }

        // Integrante Hector:
        // Aplicar la proteccion tambien en la capa de negocio.
        // Verificar transformacion HI/HD.
    }

    private static void mostrarEstadoSistema(Contexto c) {

        System.out.println("\n===== ESTADO GENERAL SJA-UTA =====");

        NodoUnidad nodo = c.organizacion.buscar(c.paralelo);

        if (nodo == null || nodo.getEstudiantes() == null) {

            System.out.println(
                "ERROR: el paralelo activo no tiene un ABB valido."
            );

            return;
        }

        ArbolEstudiantes abb = c.estudiantes();

        boolean vinculado = nodo.getEstudiantes() == abb;

        System.out.println(
            "Paralelo activo: " + c.paralelo
        );

        System.out.println(
            "ABB vinculado: " + vinculado
        );

        System.out.println("\n--- Arbol organizacional ---");

        System.out.println(
            "Altura: " + c.organizacion.altura()
        );

        System.out.println(
            "Hojas: " + c.organizacion.contarHojas()
        );

        System.out.println(
            "Nivel del paralelo: " +
            c.organizacion.nivel(c.paralelo)
        );

        System.out.println("\n--- Arbol de estudiantes ---");

        System.out.println(
            "Cantidad: " + abb.contar()
        );

        System.out.println(
            "Altura: " + abb.altura()
        );

        if (abb.estaVacio()) {

            System.out.println(
                "No hay estudiantes registrados."
            );

        } else {

            System.out.println(
                "Menor matricula: " +
                abb.menor().getMatricula()
            );

            System.out.println(
                "Mayor matricula: " +
                abb.mayor().getMatricula()
            );

            System.out.printf(
                "Promedio general: %.2f%n",
                abb.promedioGeneral()
            );
        }

        System.out.println(
            "\nDiagnostico finalizado."
        );
    }
}
