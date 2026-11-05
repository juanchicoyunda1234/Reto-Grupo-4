package pruebas;

import enums.TipoRecorrido;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import modelo.Estudiante;
import negocio.ArbolEstudiantes;

/** Pruebas de recorridos, trazas y estadisticas sobre la base compartida. */
public class PruebasLenin {
    public static void main(String[] args) {
        String caso = args.length == 0 ? "todos" : args[0];
        if (!List.of("inicial", "vacio", "cadena", "todos").contains(caso)) {
            throw new IllegalArgumentException("Use: inicial, vacio, cadena o todos");
        }
        if (caso.equals("inicial") || caso.equals("todos")) probarInicial();
        if (caso.equals("vacio") || caso.equals("todos")) probarVacio();
        if (caso.equals("cadena") || caso.equals("todos")) probarCadena();
    }

    private static void probarInicial() {
        ArbolEstudiantes arbol = crear(
                new int[]{30, 15, 45, 10, 20, 40, 50, 35, 55},
                new double[]{8, 8, 8, 8, 8, 8, 8, 8, 8});
        comprobar("ARBOL INICIAL", arbol,
                List.of(30, 15, 10, 20, 45, 40, 35, 50, 55),
                List.of(10, 15, 20, 30, 35, 40, 45, 50, 55),
                List.of(10, 20, 15, 35, 40, 55, 50, 45, 30),
                9, 3, 10, 55, 8.0);
    }

    private static void probarVacio() {
        comprobar("ARBOL VACIO", new ArbolEstudiantes(),
                List.of(), List.of(), List.of(), 0, -1, null, null, 0.0);
    }

    private static void probarCadena() {
        int[] matriculas = {10, 15, 20, 30, 35, 40, 45, 50, 55};
        double[] promedios = {5, 6, 7, 8, 9, 10, 6, 8, 8.5};
        System.out.println("Insercion ascendente: " + Arrays.toString(matriculas));
        System.out.println("Promedios: " + Arrays.toString(promedios));
        System.out.println("Media esperada: 67.5 / 9 = 7.5");
        comprobar("ARBOL EN CADENA", crear(matriculas, promedios),
                List.of(10, 15, 20, 30, 35, 40, 45, 50, 55),
                List.of(10, 15, 20, 30, 35, 40, 45, 50, 55),
                List.of(55, 50, 45, 40, 35, 30, 20, 15, 10),
                9, 8, 10, 55, 7.5);
    }

    private static ArbolEstudiantes crear(int[] matriculas, double[] promedios) {
        ArbolEstudiantes arbol = new ArbolEstudiantes();
        for (int i = 0; i < matriculas.length; i++) {
            int m = matriculas[i];
            if (!arbol.insertar(new Estudiante(m, String.format("%010d", m),
                    "Estudiante " + m, promedios[i]))) {
                throw new AssertionError("No se pudo preparar la matricula " + m);
            }
        }
        return arbol;
    }

    private static void comprobar(String titulo, ArbolEstudiantes arbol,
            List<Integer> pre, List<Integer> in, List<Integer> pos,
            int cantidad, int altura, Integer menor, Integer mayor, double promedio) {
        System.out.println("\n=== " + titulo + " ===");
        comprobarRecorrido(arbol, TipoRecorrido.PREORDEN, pre);
        comprobarRecorrido(arbol, TipoRecorrido.INORDEN, in);
        comprobarRecorrido(arbol, TipoRecorrido.POSORDEN, pos);
        igual("Cantidad", cantidad, arbol.contar());
        igual("Altura (aristas)", altura, arbol.altura());
        igual("Menor matricula", menor, matricula(arbol.menor()));
        igual("Mayor matricula", mayor, matricula(arbol.mayor()));
        double obtenido = arbol.promedioGeneral();
        if (!Double.isFinite(obtenido) || Math.abs(promedio - obtenido) > 1e-9) {
            throw new AssertionError("Promedio: esperado=" + promedio + ", obtenido=" + obtenido);
        }
        System.out.println("[OK] Promedio: esperado=" + promedio + ", obtenido=" + obtenido);
        System.out.println("RESULTADO: APROBADO\n");
    }

    private static void comprobarRecorrido(ArbolEstudiantes arbol, TipoRecorrido tipo,
            List<Integer> esperado) {
        List<Integer> obtenido = new ArrayList<>();
        for (Estudiante e : arbol.recorrer(tipo)) obtenido.add(e.getMatricula());
        igual(tipo.toString(), esperado, obtenido);

        String traza = arbol.trazaRecorrido(tipo);
        List<Integer> visitas = new ArrayList<>();
        int entradas = 0;
        int nulos = 0;
        for (String linea : traza.split("\\R")) {
            String paso = linea.stripLeading();
            if (paso.startsWith("VISITA ")) visitas.add(Integer.parseInt(paso.substring(7)));
            if (paso.startsWith("entra ")) entradas++;
            if (paso.equals("null -> regresa")) nulos++;
        }
        igual("Traza " + tipo + " / VISITA", esperado, visitas);
        String secuencia = String.join(" ", esperado.stream().map(String::valueOf).toList());
        String finalEsperado = "Secuencia " + tipo + ":" + (secuencia.isEmpty() ? "" : " " + secuencia);
        if (!traza.stripTrailing().endsWith(finalEsperado)
                || entradas != esperado.size() || nulos != esperado.size() + 1) {
            throw new AssertionError("Traza incompleta o secuencia final incorrecta en " + tipo);
        }
        System.out.println("[OK] Traza " + tipo + ": secuencia final, entradas y retornos null");
    }

    private static Integer matricula(Estudiante estudiante) {
        return estudiante == null ? null : estudiante.getMatricula();
    }

    private static void igual(String nombre, Object esperado, Object obtenido) {
        if (!java.util.Objects.equals(esperado, obtenido)) {
            throw new AssertionError(nombre + ": esperado=" + esperado + ", obtenido=" + obtenido);
        }
        System.out.println("[OK] " + nombre + ": esperado=" + esperado + ", obtenido=" + obtenido);
    }
}
