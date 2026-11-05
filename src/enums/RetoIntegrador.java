package app;

import enums.TipoRecorrido;
import enums.TipoUnidad;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import modelo.Estudiante;
import modelo.UnidadAcademica;
import negocio.ArbolEstudiantes;
import negocio.ArbolOrganizacional;
import negocio.NodoEstudiante;

public class RetoIntegrador {

    private static final String PARALELO_A = "EB-N1-A";
    private static final String PARALELO_B = "EB-N1-B";
    private static final String RUTA_ESPERADA_A = "UTA -> FCHE -> EB -> EB-N1 -> EB-N1-A";
    private static final String RUTA_ESPERADA_SW = "UTA -> FCHE -> SW -> SW-N3 -> SW-N3-A";

    private final Contexto contexto;
    private final PrintStream out;
    private int total;
    private int superadas;

    private record Estadisticas(
            List<Integer> inorden,
            int cantidad,
            int altura,
            int menor,
            int mayor,
            double promedio,
            int aprobados) {
    }

    public RetoIntegrador(Contexto contexto, PrintStream out) {
        if (contexto == null || out == null) {
            throw new IllegalArgumentException("El contexto y la salida son obligatorios");
        }
        this.contexto = contexto;
        this.out = out;
    }

    public static boolean ejecutar(Contexto contexto, PrintStream out) {
        return new RetoIntegrador(contexto, out).correr();
    }

    public int getTotal() {
        return total;
    }

    public int getSuperadas() {
        return superadas;
    }

    private boolean correr() {
        total = 0;
        superadas = 0;

        out.println("\n======================================");
        out.println("      RETO INTEGRADOR SJA-UTA");
        out.println("======================================");
        out.println("Se reconstruyen los datos iniciales antes de empezar,");
        out.println("asi la opcion 8 se puede repetir sin duplicados.");
        contexto.reiniciar();

        mision1();
        mision2();
        mision3();
        mision4();
        mision5();
        mision6();

        return superadas == total;
    }

    private ArbolOrganizacional org() {
        return contexto.organizacion;
    }

    private ArbolEstudiantes arbol(String codigo) {
        return org().buscar(codigo).getEstudiantes();
    }

    private void verificar(String descripcion, boolean condicion) {
        total++;
        if (condicion) {
            superadas++;
        }
        out.println("  [" + (condicion ? "OK" : "FALLA") + "] " + descripcion);
    }

    private void titulo(String texto) {
        out.println("\n--------------------------------------");
        out.println(texto);
        out.println("--------------------------------------");
    }

    private static List<Integer> matriculas(List<Estudiante> lista) {
        List<Integer> resultado = new ArrayList<>();
        for (Estudiante e : lista) {
            resultado.add(e.getMatricula());
        }
        return resultado;
    }

    private List<Integer> inorden(String codigo) {
        return matriculas(arbol(codigo).recorrer(TipoRecorrido.INORDEN));
    }

    private static boolean esAscendente(List<Integer> lista) {
        for (int i = 1; i < lista.size(); i++) {
            if (lista.get(i - 1) >= lista.get(i)) {
                return false;
            }
        }
        return true;
    }

    private static boolean lanzaIllegalArgument(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private void mision1() {
        titulo("MISION 1. Jerarquia y ruta institucional");
        ArbolOrganizacional org = org();

        out.println("Antes: altura " + org.altura() + ", hojas " + org.contarHojas());

        verificar("Registrar SW bajo FCHE",
                org.agregar("FCHE", new UnidadAcademica("SW", "Software", TipoUnidad.CARRERA)));
        verificar("Registrar SW-N3 bajo SW",
                org.agregar("SW", new UnidadAcademica("SW-N3", "Nivel 3", TipoUnidad.NIVEL)));
        verificar("Registrar SW-N3-A bajo SW-N3",
                org.agregar("SW-N3", new UnidadAcademica("SW-N3-A", "Paralelo A", TipoUnidad.PARALELO)));
        verificar("Rechazar codigo duplicado SW-N3-A",
                !org.agregar("SW-N3", new UnidadAcademica("SW-N3-A", "Duplicado", TipoUnidad.PARALELO)));
        verificar("Rechazar relacion invalida (paralelo directo bajo FCHE)",
                !org.agregar("FCHE", new UnidadAcademica("SW-X", "Invalido", TipoUnidad.PARALELO)));

        out.println("\nJerarquia actual:");
        out.println(org.mostrarJerarquia());

        out.println("\nDespues: altura " + org.altura() + ", hojas " + org.contarHojas());
        out.println("Nivel SW: " + org.nivel("SW")
                + " | SW-N3: " + org.nivel("SW-N3")
                + " | SW-N3-A: " + org.nivel("SW-N3-A"));
        verificar("Altura organizacional igual a 4 (en aristas)", org.altura() == 4);
        verificar("Cantidad de hojas igual a 3 (EB-N1-A, EB-N1-B, SW-N3-A)", org.contarHojas() == 3);
        verificar("Niveles de SW, SW-N3 y SW-N3-A iguales a 2, 3 y 4",
                org.nivel("SW") == 2 && org.nivel("SW-N3") == 3 && org.nivel("SW-N3-A") == 4);
        verificar("Grado de FCHE igual a 2", org.grado("FCHE") == 2);

        String rutaA = org.obtenerRutaJerarquica(PARALELO_A);
        String rutaSw = org.obtenerRutaJerarquica("SW-N3-A");
        String rutaNo = org.obtenerRutaJerarquica("NO-EXISTE");
        out.println("\nRuta de EB-N1-A: " + rutaA);
        out.println("Ruta de SW-N3-A: " + rutaSw);
        out.println("Ruta de NO-EXISTE: " + rutaNo);
        verificar("Ruta de EB-N1-A correcta", RUTA_ESPERADA_A.equals(rutaA));
        verificar("Ruta de SW-N3-A correcta", RUTA_ESPERADA_SW.equals(rutaSw));
        verificar("Ruta de un codigo inexistente es 'No existe'", "No existe".equals(rutaNo));
        verificar("Ruta de la raiz es UTA", "UTA".equals(org.obtenerRutaJerarquica("uta")));
    }

    private void mision2() {
        titulo("MISION 2. Proteccion de unidades y transformacion binaria");
        ArbolOrganizacional org = org();

        verificar("Rechazar eliminar EB-N1-A mientras tenga estudiantes", !org.eliminar(PARALELO_A));
        verificar("Rechazar eliminar EB (tiene subunidades)", !org.eliminar("EB"));
        verificar("Rechazar eliminar SW (tiene subunidades)", !org.eliminar("SW"));
        verificar("Rechazar eliminar SW-N3 (tiene subunidades)", !org.eliminar("SW-N3"));
        verificar("Eliminar un codigo inexistente devuelve false", !org.eliminar("NO-EXISTE"));
        verificar("EB-N1-A sigue existiendo con sus 9 estudiantes",
                org.buscar(PARALELO_A) != null && arbol(PARALELO_A).contar() == 9);

        boolean eliminada = org.eliminar("SW-N3-A");
        verificar("Eliminar SW-N3-A (hoja vacia) es posible", eliminada);
        verificar("SW-N3-A ya no existe tras eliminarla", org.buscar("SW-N3-A") == null);
        verificar("SW-N3 vuelve a ser hoja (hojas igual a 3)", org.contarHojas() == 3);
        verificar("Volver a registrar SW-N3-A para continuar el reto",
                org.agregar("SW-N3", new UnidadAcademica("SW-N3-A", "Paralelo A", TipoUnidad.PARALELO)));

        String antes = org.mostrarJerarquia();
        String binaria = org.transformarABinario();
        String despues = org.mostrarJerarquia();
        out.println("\nTransformacion hijo izquierdo / hermano derecho:");
        out.println(binaria);
        out.println();
        verificar("El arbol general no cambia con transformarABinario()", antes.equals(despues));
        verificar("Relacion EB -> SW: 'Hermano Der de EB: SW'", binaria.contains("Hermano Der de EB: SW"));
        verificar("Relacion EB-N1-A -> EB-N1-B: 'Hermano Der de EB-N1-A: EB-N1-B'",
                binaria.contains("Hermano Der de EB-N1-A: EB-N1-B"));
        verificar("Primer hijo de FCHE es EB: 'Hijo Izq de FCHE: EB'", binaria.contains("Hijo Izq de FCHE: EB"));
        verificar("Primer hijo de EB-N1 es EB-N1-A: 'Hijo Izq de EB-N1: EB-N1-A'",
                binaria.contains("Hijo Izq de EB-N1: EB-N1-A"));
    }

    private void mision3() {
        titulo("MISION 3. Validacion de datos y traslado entre paralelos");
        ArbolOrganizacional org = org();

        verificar("Matricula 0 rechazada",
                lanzaIllegalArgument(() -> new Estudiante(0, "0000000001", "Prueba", 7)));
        verificar("Matricula negativa rechazada",
                lanzaIllegalArgument(() -> new Estudiante(-5, "0000000001", "Prueba", 7)));

        Estudiante prueba = new Estudiante(1, "0000000001", "Prueba", 7.5);
        verificar("Promedio NaN rechazado", lanzaIllegalArgument(() -> prueba.setPromedio(Double.NaN)));
        verificar("Promedio +Infinity rechazado",
                lanzaIllegalArgument(() -> prueba.setPromedio(Double.POSITIVE_INFINITY)));
        verificar("Promedio -Infinity rechazado",
                lanzaIllegalArgument(() -> prueba.setPromedio(Double.NEGATIVE_INFINITY)));
        verificar("Promedio negativo rechazado", lanzaIllegalArgument(() -> prueba.setPromedio(-0.01)));
        verificar("Promedio mayor que 10 rechazado", lanzaIllegalArgument(() -> prueba.setPromedio(10.01)));
        verificar("Un promedio rechazado no altera el valor guardado (7.5)", prueba.getPromedio() == 7.5);
        prueba.setPromedio(0);
        prueba.setPromedio(10);
        verificar("Los limites 0 y 10 se aceptan", prueba.getPromedio() == 10);

        List<Integer> antesA = inorden(PARALELO_A);
        out.println("\nInorden inicial de EB-N1-A: " + antesA);

        verificar("Rechazar traslado con origen inexistente",
                !org.trasladarEstudiante("NO-EXISTE", PARALELO_B, 10));
        verificar("Rechazar traslado con destino inexistente",
                !org.trasladarEstudiante(PARALELO_A, "NO-EXISTE", 10));
        verificar("Rechazar traslado con destino que no es PARALELO",
                !org.trasladarEstudiante(PARALELO_A, "EB-N1", 10));
        verificar("Rechazar traslado con origen que no es PARALELO",
                !org.trasladarEstudiante("EB-N1", PARALELO_B, 10));
        verificar("Rechazar traslado de una matricula inexistente en el origen",
                !org.trasladarEstudiante(PARALELO_A, PARALELO_B, 999));
        verificar("Rechazar traslado al mismo paralelo",
                !org.trasladarEstudiante(PARALELO_A, PARALELO_A, 10));

        ArbolEstudiantes a = arbol(PARALELO_A);
        ArbolEstudiantes swa = arbol("SW-N3-A");
        a.insertar(new Estudiante(777, "0000000777", "Estudiante 777", 8));
        swa.insertar(new Estudiante(777, "0000000777", "Estudiante 777", 8));
        verificar("Rechazar traslado con matricula duplicada en el destino",
                !org.trasladarEstudiante(PARALELO_A, "SW-N3-A", 777));
        verificar("Tras el rechazo por duplicado, la matricula 777 sigue en origen y destino",
                a.buscar(777) != null && swa.buscar(777) != null && a.contar() == 10 && swa.contar() == 1);
        a.eliminar(777);
        swa.eliminar(777);

        verificar("Los rechazos no modificaron EB-N1-A", antesA.equals(inorden(PARALELO_A)));
        verificar("Los rechazos no modificaron EB-N1-B (vacio)", arbol(PARALELO_B).estaVacio());
        verificar("Los rechazos no modificaron SW-N3-A (vacio)", swa.estaVacio());
    }

    private String describirCaso(ArbolEstudiantes arbol, int matricula) {
        NodoEstudiante nodo = arbol.getRaiz();
        while (nodo != null && nodo.getDato().getMatricula() != matricula) {
            nodo = matricula < nodo.getDato().getMatricula() ? nodo.getIzquierdo() : nodo.getDerecho();
        }
        if (nodo == null) {
            return "no existe";
        }
        String tipo;
        if (nodo.esHoja()) {
            tipo = "hoja";
        } else if (nodo.getIzquierdo() != null && nodo.getDerecho() != null) {
            tipo = "dos hijos";
        } else {
            tipo = "un hijo";
        }
        return nodo == arbol.getRaiz() ? tipo + " y raiz" : tipo;
    }

    private void trasladarYVerificar(int matricula, String casoEsperado) {
        ArbolEstudiantes a = arbol(PARALELO_A);
        String caso = describirCaso(a, matricula);
        List<Integer> antes = inorden(PARALELO_A);
        int raizAntes = a.getRaiz().getDato().getMatricula();

        out.println("\nTraslado de la matricula " + matricula + " (caso en el ABB de origen: " + caso + ")");
        out.println("  Inorden EB-N1-A antes:   " + antes);
        boolean trasladado = contexto.organizacion.trasladarEstudiante(PARALELO_A, PARALELO_B, matricula);
        List<Integer> despuesA = inorden(PARALELO_A);
        List<Integer> despuesB = inorden(PARALELO_B);
        out.println("  Inorden EB-N1-A despues: " + despuesA);
        out.println("  Inorden EB-N1-B despues: " + despuesB);
        out.println("  Raiz de EB-N1-A: " + raizAntes + " -> "
                + (a.estaVacio() ? "vacio" : String.valueOf(a.getRaiz().getDato().getMatricula())));

        verificar("Caso del ABB para " + matricula + " es '" + casoEsperado + "'", casoEsperado.equals(caso));
        verificar("trasladarEstudiante(" + matricula + ") devuelve true", trasladado);
        verificar("La matricula " + matricula + " salio de EB-N1-A y llego a EB-N1-B",
                !despuesA.contains(matricula) && despuesB.contains(matricula));
        verificar("Inorden de EB-N1-A sigue ascendente y solo perdio la matricula " + matricula,
                esAscendente(despuesA) && despuesA.size() == antes.size() - 1);
        verificar("Inorden de EB-N1-B sigue ascendente", esAscendente(despuesB));
        verificar("Total de estudiantes conservado (9)", despuesA.size() + despuesB.size() == 9);
    }

    private void mision4() {
        titulo("MISION 4. Modificacion segura y casos de eliminacion del ABB");
        ArbolEstudiantes a = arbol(PARALELO_A);

        boolean modificado = a.modificar(35, "0000000035", "Estudiante 35", 9.80);
        verificar("Modificar el promedio de la matricula 35 a 9.80", modificado);
        verificar("La matricula 35 quedo con promedio 9.80", a.buscar(35).getPromedio() == 9.80);

        verificar("Actualizacion con cedula invalida rechazada",
                lanzaIllegalArgument(() -> a.modificar(35, "123", "Otro nombre", 5)));
        verificar("Actualizacion con promedio 15 rechazada",
                lanzaIllegalArgument(() -> a.modificar(35, "0000000099", "Otro nombre", 15)));
        verificar("Actualizacion con promedio NaN rechazada",
                lanzaIllegalArgument(() -> a.modificar(35, "0000000099", "Otro nombre", Double.NaN)));
        Estudiante e35 = a.buscar(35);
        out.println("\nRegistro 35 despues de los intentos invalidos: " + e35);
        verificar("El registro 35 no cambio (cedula, nombres y promedio)",
                e35.getCedula().equals("0000000035")
                        && e35.getNombres().equals("Estudiante 35")
                        && e35.getPromedio() == 9.80);
        verificar("Modificar una matricula inexistente devuelve false",
                !a.modificar(999, "0000000999", "Nadie", 8));

        trasladarYVerificar(10, "hoja");
        trasladarYVerificar(40, "un hijo");
        trasladarYVerificar(45, "dos hijos");
        trasladarYVerificar(30, "dos hijos y raiz");
    }

    private Estadisticas estadisticas(String codigo) {
        ArbolEstudiantes arbol = arbol(codigo);
        return new Estadisticas(
                inorden(codigo),
                arbol.contar(),
                arbol.altura(),
                arbol.menor() == null ? -1 : arbol.menor().getMatricula(),
                arbol.mayor() == null ? -1 : arbol.mayor().getMatricula(),
                arbol.promedioGeneral(),
                arbol.contarAprobados(8.00));
    }

    private void mision5() {
        titulo("MISION 5. Recorridos y estadisticas comparadas");

        Estadisticas ea = estadisticas(PARALELO_A);
        Estadisticas eb = estadisticas(PARALELO_B);

        out.println(String.format("%-28s | %-22s | %-22s", "Dato", PARALELO_A, PARALELO_B));
        out.println("-".repeat(80));
        out.println(String.format("%-28s | %-22s | %-22s", "Inorden", ea.inorden(), eb.inorden()));
        out.println(String.format("%-28s | %-22d | %-22d", "Cantidad de estudiantes", ea.cantidad(), eb.cantidad()));
        out.println(String.format("%-28s | %-22d | %-22d", "Altura (aristas)", ea.altura(), eb.altura()));
        out.println(String.format("%-28s | %-22d | %-22d", "Matricula menor", ea.menor(), eb.menor()));
        out.println(String.format("%-28s | %-22d | %-22d", "Matricula mayor", ea.mayor(), eb.mayor()));
        out.println(String.format("%-28s | %-22.2f | %-22.2f", "Promedio general", ea.promedio(), eb.promedio()));
        out.println(String.format("%-28s | %-22d | %-22d", "Aprobados (nota minima 8.00)",
                ea.aprobados(), eb.aprobados()));

        verificar("EB-N1-A contiene 15, 20, 35, 50 y 55", ea.inorden().equals(List.of(15, 20, 35, 50, 55)));
        verificar("EB-N1-B contiene 10, 30, 40 y 45", eb.inorden().equals(List.of(10, 30, 40, 45)));
        verificar("Promedio de EB-N1-A igual a 8.36", String.format("%.2f", ea.promedio()).equals("8.36"));
        verificar("Promedio de EB-N1-B igual a 8.00", String.format("%.2f", eb.promedio()).equals("8.00"));
        verificar("Cantidades 5 y 4", ea.cantidad() == 5 && eb.cantidad() == 4);
        verificar("Menor y mayor de EB-N1-A: 15 y 55", ea.menor() == 15 && ea.mayor() == 55);
        verificar("Menor y mayor de EB-N1-B: 10 y 45", eb.menor() == 10 && eb.mayor() == 45);
        verificar("Aprobados con nota minima 8.00: 5 en EB-N1-A y 4 en EB-N1-B",
                ea.aprobados() == 5 && eb.aprobados() == 4);
        verificar("contarAprobados(9.00) en EB-N1-A cuenta solo la matricula 35 (9.80)",
                arbol(PARALELO_A).contarAprobados(9.00) == 1);
        verificar("contarAprobados(10.00) en EB-N1-A es 0", arbol(PARALELO_A).contarAprobados(10.00) == 0);
        verificar("contarAprobados(0) cuenta a todos los de EB-N1-A", arbol(PARALELO_A).contarAprobados(0) == 5);
        verificar("contarAprobados en un ABB vacio es 0", new ArbolEstudiantes().contarAprobados(8.00) == 0);
        verificar("contarAprobados rechaza una nota minima NaN",
                lanzaIllegalArgument(() -> new ArbolEstudiantes().contarAprobados(Double.NaN)));

        out.println("\nComparacion:");
        out.println(String.format("  EB-N1-A tiene %d estudiantes (promedio %.2f) y EB-N1-B tiene %d (promedio %.2f).",
                ea.cantidad(), ea.promedio(), eb.cantidad(), eb.promedio()));
        out.println(String.format("  La diferencia de promedio se debe a la matricula 35, modificada a %.2f y no trasladada.",
                arbol(PARALELO_A).buscar(35).getPromedio()));
        out.println(String.format("  Aprobados con nota minima 8.00: %d de %d en EB-N1-A y %d de %d en EB-N1-B.",
                ea.aprobados(), ea.cantidad(), eb.aprobados(), eb.cantidad()));
        out.println("\nPor que el inorden conserva el orden de matriculas:");
        out.println("  En un ABB cada nodo tiene en su subarbol izquierdo solo matriculas menores y en el");
        out.println("  derecho solo matriculas mayores. El inorden visita izquierdo, nodo y derecho, asi que");
        out.println("  en cada nodo primero salen todas las menores, luego la propia y despues las mayores.");
        out.println("  Como esto se cumple en todo el arbol y no hay matriculas repetidas, la secuencia sale");
        out.println("  ascendente. Las eliminaciones y la insercion del traslado mantienen esa propiedad.");
    }

    private void mision6() {
        titulo("MISION 6. Integracion y reinicio");
        ArbolOrganizacional org = org();

        verificar("Las unidades SW, SW-N3 y SW-N3-A existen una sola vez",
                org.buscar("SW") != null && org.buscar("SW-N3") != null && org.buscar("SW-N3-A") != null
                        && org.contarHojas() == 3);
        verificar("Total de estudiantes en el sistema igual a 9",
                arbol(PARALELO_A).contar() + arbol(PARALELO_B).contar() + arbol("SW-N3-A").contar() == 9);

        out.println("\nResultado del reto: " + superadas + " de " + total + " verificaciones superadas.");
        if (superadas == total) {
            out.println("Estado: TODAS LAS VERIFICACIONES OK.");
        } else {
            out.println("Estado: HAY VERIFICACIONES QUE FALLARON, revise las lineas [FALLA].");
        }
        out.println("\nEl sistema queda en el estado final del reto (EB-N1-A con 5 estudiantes,");
        out.println("EB-N1-B con 4 y las unidades SW creadas).");
        out.println("Use la opcion 7 para reconstruir los datos iniciales o la opcion 8 para repetir el reto.");
    }
}
