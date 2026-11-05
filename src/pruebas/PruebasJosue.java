
package pruebas;

import negocio.ArbolEstudiantes;
import modelo.Estudiante;
import enums.TipoRecorrido;

public class PruebasJosue {

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE JOSUE ===");

        probarModificacion();
        probarEliminacion(10, "Nodo hoja");
        probarEliminacion(40, "Nodo con un hijo");
        probarEliminacion(45, "Nodo con dos hijos");
        probarEliminacion(30, "Nodo raiz");

        System.out.println("\n=== FIN DE LAS PRUEBAS ===");
    }

    // Crear un arbol nuevo para cada prueba
    private static ArbolEstudiantes crearArbol() {
        ArbolEstudiantes arbol = new ArbolEstudiantes();

        int[] matriculas = {
            30, 20, 45, 10, 25, 40, 50, 42, 47
        };

        for (int matricula : matriculas) {
            Estudiante estudiante = new Estudiante(
                matricula,
                "1234567890",
                "Estudiante " + matricula,
                8.5
            );
            arbol.insertar(estudiante);
        }

        return arbol;
    }

    // Prueba de modificacion
    private static void probarModificacion() {
        System.out.println("\n--- MODIFICACION ---");

        ArbolEstudiantes arbol = crearArbol();

        boolean resultado = arbol.modificar(
            30, "0987654321", "Josue Actualizado", 9.5
        );

        Estudiante estudiante = arbol.buscar(30);

        verificar(resultado && estudiante != null
            && estudiante.getNombres().equals("Josue Actualizado")
            && estudiante.getPromedio() == 9.5,
            "Modificacion valida");

        // Guardar datos anteriores
        String nombreAnterior = estudiante.getNombres();
        String cedulaAnterior = estudiante.getCedula();
        double promedioAnterior = estudiante.getPromedio();

        boolean rechazo = false;

        try {
            arbol.modificar(
                30, "1111111111", "Nombre Invalido", -5
            );
        } catch (IllegalArgumentException e) {
            rechazo = true;
        }

        Estudiante despues = arbol.buscar(30);

        verificar(rechazo
            && despues.getNombres().equals(nombreAnterior)
            && despues.getCedula().equals(cedulaAnterior)
            && despues.getPromedio() == promedioAnterior,
            "Modificacion invalida conserva los datos");
    }

    // Pruebas de eliminacion
    private static void probarEliminacion(
            int matricula, String descripcion) {

        ArbolEstudiantes arbol = crearArbol();

        System.out.println("\n--- " + descripcion + " ---");

        System.out.println("Inorden antes:");
        System.out.println(
            arbol.recorrer(TipoRecorrido.INORDEN)
        );

        int cantidadAnterior = arbol.contar();

        boolean eliminado = arbol.eliminar(matricula);

        System.out.println("Inorden despues:");
        System.out.println(
            arbol.recorrer(TipoRecorrido.INORDEN)
        );

        verificar(
            eliminado
            && arbol.buscar(matricula) == null
            && arbol.contar() == cantidadAnterior - 1
            && estaOrdenado(arbol),
            "Eliminacion de matricula " + matricula
        );
    }

    private static boolean estaOrdenado(
            ArbolEstudiantes arbol) {

        java.util.List<Estudiante> lista =
            arbol.recorrer(TipoRecorrido.INORDEN);

        for (int i = 1; i < lista.size(); i++) {
            if (lista.get(i - 1).getMatricula()
                    >= lista.get(i).getMatricula()) {
                return false;
            }
        }

        return true;
    }

    private static void verificar(
            boolean condicion, String prueba) {

        if (condicion) {
            System.out.println("APROBADO: " + prueba);
        } else {
            System.out.println("FALLO: " + prueba);
        }
    }
}
