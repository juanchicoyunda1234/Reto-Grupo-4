package pruebas;

import app.Contexto;
import app.RetoIntegrador;
import enums.TipoRecorrido;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import modelo.Estudiante;
import negocio.ArbolEstudiantes;

public class PruebasReto {

    private static int total;
    private static int superadas;

    public static void main(String[] args) {
        Contexto c = new Contexto();
        PrintStream silencio = new PrintStream(new ByteArrayOutputStream());

        comprobar("Estado inicial reconocido", estadoInicial(c));

        boolean primera = RetoIntegrador.ejecutar(c, silencio);
        String estadoPrimera = instantanea(c);
        comprobar("Primera ejecucion de la opcion 8 sin fallas", primera);
        comprobar("Estado tras la primera ejecucion distinto del inicial", !estadoInicial(c));

        boolean segunda = RetoIntegrador.ejecutar(c, silencio);
        String estadoSegunda = instantanea(c);
        comprobar("Segunda ejecucion seguida de la opcion 8 sin fallas", segunda);
        comprobar("La repeticion deja exactamente el mismo estado (sin duplicados)",
                estadoPrimera.equals(estadoSegunda));

        c.reiniciar();
        comprobar("Opcion 7: EB-N1-A vuelve a tener las nueve matriculas",
                matriculas(c, "EB-N1-A").equals(List.of(10, 15, 20, 30, 35, 40, 45, 50, 55)));
        comprobar("Opcion 7: EB-N1-B queda vacio", c.organizacion.buscar("EB-N1-B").getEstudiantes().estaVacio());
        comprobar("Opcion 7: SW, SW-N3 y SW-N3-A dejan de existir",
                c.organizacion.buscar("SW") == null
                        && c.organizacion.buscar("SW-N3") == null
                        && c.organizacion.buscar("SW-N3-A") == null);
        comprobar("Opcion 7: altura 4 y 2 hojas como al inicio",
                c.organizacion.altura() == 4 && c.organizacion.contarHojas() == 2);
        comprobar("Opcion 7: promedio de EB-N1-A vuelve a 8.00 con la matricula 35 en 8.00",
                c.estudiantes().buscar(35).getPromedio() == 8.0
                        && c.estudiantes().promedioGeneral() == 8.0);
        comprobar("Estado tras reiniciar igual al estado inicial", estadoInicial(c));

        System.out.println("\nPruebas del reto: " + superadas + " de " + total + " superadas.");
        System.out.println(superadas == total ? "RESULTADO FINAL: APROBADO" : "RESULTADO FINAL: REPROBADO");
        if (superadas != total) {
            System.exit(1);
        }
    }

    private static void comprobar(String descripcion, boolean condicion) {
        total++;
        if (condicion) {
            superadas++;
        }
        System.out.println("[" + (condicion ? "OK" : "FALLA") + "] " + descripcion);
    }

    private static List<Integer> matriculas(Contexto c, String codigo) {
        ArbolEstudiantes arbol = c.organizacion.buscar(codigo).getEstudiantes();
        List<Integer> lista = new ArrayList<>();
        for (Estudiante e : arbol.recorrer(TipoRecorrido.INORDEN)) {
            lista.add(e.getMatricula());
        }
        return lista;
    }

    private static boolean estadoInicial(Contexto c) {
        return matriculas(c, "EB-N1-A").equals(List.of(10, 15, 20, 30, 35, 40, 45, 50, 55))
                && matriculas(c, "EB-N1-B").isEmpty()
                && c.organizacion.buscar("SW") == null;
    }

    private static String instantanea(Contexto c) {
        StringBuilder sb = new StringBuilder(c.organizacion.mostrarJerarquia());
        for (String codigo : new String[] {"EB-N1-A", "EB-N1-B", "SW-N3-A"}) {
            if (c.organizacion.buscar(codigo) != null) {
                ArbolEstudiantes arbol = c.organizacion.buscar(codigo).getEstudiantes();
                sb.append("\n").append(codigo).append(" pre=")
                        .append(arbol.recorrer(TipoRecorrido.PREORDEN).stream()
                                .map(e -> e.getMatricula() + ":" + e.getPromedio()).toList());
            }
        }
        return sb.toString();
    }
}
