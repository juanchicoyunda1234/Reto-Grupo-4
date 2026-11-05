package negocio;

import enums.TipoUnidad;
import modelo.Estudiante;
import modelo.UnidadAcademica;
import java.util.List;

public class ArbolOrganizacional {

    private NodoUnidad raiz;

    public ArbolOrganizacional(UnidadAcademica universidad) {
        if (universidad == null
                || universidad.getTipo() != TipoUnidad.UNIVERSIDAD) {
            throw new IllegalArgumentException(
                "La raiz debe ser una UNIVERSIDAD"
            );
        }

        this.raiz = new NodoUnidad(universidad);
    }

    public NodoUnidad getRaiz() {
        return raiz;
    }

    // A1: Agregar unidad academica
    public boolean agregar(String codigoPadre, UnidadAcademica unidad) {

        if (codigoPadre == null || unidad == null) {
            return false;
        }

        if (buscar(unidad.getCodigo()) != null) {
            return false;
        }

        NodoUnidad padre = buscar(codigoPadre);

        if (padre == null) {
            return false;
        }

        return padre.agregarHijo(new NodoUnidad(unidad));
    }

    // A2: Buscar unidad recursivamente
    public NodoUnidad buscar(String codigo) {

        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        return buscar(raiz, codigo.trim().toUpperCase());
    }

    private NodoUnidad buscar(NodoUnidad nodo, String codigo) {

        if (nodo == null) {
            return null;
        }

        if (nodo.getDato().getCodigo().equals(codigo)) {
            return nodo;
        }

        for (NodoUnidad hijo : nodo.getHijos()) {
            NodoUnidad encontrado = buscar(hijo, codigo);

            if (encontrado != null) {
                return encontrado;
            }
        }

        return null;
    }

    // A3: Mostrar jerarquia
    public String mostrarJerarquia() {

        if (raiz == null) {
            return "Arbol organizacional vacio.";
        }

        StringBuilder sb = new StringBuilder();

        mostrarJerarquia(raiz, 0, sb);

        return sb.toString().stripTrailing();
    }

    private void mostrarJerarquia(
            NodoUnidad nodo,
            int nivel,
            StringBuilder sb) {

        if (nodo == null) {
            return;
        }

        sb.append("  ".repeat(nivel))
          .append(nodo.getDato())
          .append(System.lineSeparator());

        for (NodoUnidad hijo : nodo.getHijos()) {
            mostrarJerarquia(hijo, nivel + 1, sb);
        }
    }

    // A4: Calcular altura organizacional
    public int altura() {
        return altura(raiz);
    }

    private int altura(NodoUnidad nodo) {

        if (nodo == null) {
            return -1;
        }

        int mayor = -1;

        for (NodoUnidad hijo : nodo.getHijos()) {
            mayor = Math.max(mayor, altura(hijo));
        }

        return 1 + mayor;
    }

    // A4: Consultar grado de una unidad
    public int grado(String codigo) {

        NodoUnidad nodo = buscar(codigo);

        if (nodo == null) {
            return -1;
        }

        return nodo.getHijos().size();
    }

    // A4: Consultar nivel de una unidad
    public int nivel(String codigo) {

        if (codigo == null || codigo.isBlank()) {
            return -1;
        }

        return nivel(
            raiz,
            codigo.trim().toUpperCase(),
            0
        );
    }

    private int nivel(
            NodoUnidad nodo,
            String codigo,
            int profundidad) {

        if (nodo == null) {
            return -1;
        }

        if (nodo.getDato().getCodigo().equals(codigo)) {
            return profundidad;
        }

        for (NodoUnidad hijo : nodo.getHijos()) {

            int resultado = nivel(
                hijo,
                codigo,
                profundidad + 1
            );

            if (resultado != -1) {
                return resultado;
            }
        }

        return -1;
    }

    // A4: Contar hojas del arbol general
    public int contarHojas() {
        return contarHojas(raiz);
    }

    private int contarHojas(NodoUnidad nodo) {

        if (nodo == null) {
            return 0;
        }

        if (nodo.esHoja()) {
            return 1;
        }

        int total = 0;

        for (NodoUnidad hijo : nodo.getHijos()) {
            total += contarHojas(hijo);
        }

        return total;
    }

    // A5: Eliminar unidad de manera segura
    public boolean eliminar(String codigo) {

        if (codigo == null
                || codigo.isBlank()
                || raiz == null) {
            return false;
        }

        codigo = codigo.trim().toUpperCase();

        NodoUnidad objetivo = buscar(codigo);

        if (objetivo == null || !puedeEliminarUnidad(objetivo)) {
            return false;
        }

        if (objetivo == raiz) {
            raiz = null;
            return true;
        }

        return eliminar(raiz, codigo);
    }

    // Comprueba que la unidad no tenga
    // subunidades ni estudiantes matriculados
    private boolean puedeEliminarUnidad(NodoUnidad nodo) {

        if (nodo == null || !nodo.esHoja()) {
            return false;
        }

        ArbolEstudiantes estudiantes = nodo.getEstudiantes();

        return estudiantes == null || estudiantes.estaVacio();
    }

    private boolean eliminar(NodoUnidad padre, String codigo) {

        if (padre == null) {
            return false;
        }

        for (NodoUnidad hijo : padre.getHijos()) {

            if (hijo.getDato().getCodigo().equals(codigo)) {

                if (!puedeEliminarUnidad(hijo)) {
                    return false;
                }

                return padre.eliminarHijo(hijo);
            }

            if (eliminar(hijo, codigo)) {
                return true;
            }
        }

        return false;
    }

    public String obtenerRutaJerarquica(String codigo) {

        if (codigo == null || codigo.isBlank() || raiz == null) {
            return "No existe";
        }

        String ruta = obtenerRuta(raiz, codigo.trim().toUpperCase());

        return ruta == null ? "No existe" : ruta;
    }

    private String obtenerRuta(NodoUnidad nodo, String codigo) {

        if (nodo == null) {
            return null;
        }

        String actual = nodo.getDato().getCodigo();

        if (actual.equals(codigo)) {
            return actual;
        }

        for (NodoUnidad hijo : nodo.getHijos()) {

            String parcial = obtenerRuta(hijo, codigo);

            if (parcial != null) {
                return actual + " -> " + parcial;
            }
        }

        return null;
    }

    public boolean trasladarEstudiante(
            String codOrigen,
            String codDestino,
            int matricula) {

        NodoUnidad origen = buscar(codOrigen);
        NodoUnidad destino = buscar(codDestino);

        if (origen == null || destino == null || origen == destino) {
            return false;
        }

        if (origen.getDato().getTipo() != TipoUnidad.PARALELO
                || destino.getDato().getTipo() != TipoUnidad.PARALELO) {
            return false;
        }

        ArbolEstudiantes arbolOrigen = origen.getEstudiantes();
        ArbolEstudiantes arbolDestino = destino.getEstudiantes();

        if (arbolOrigen == null || arbolDestino == null) {
            return false;
        }

        Estudiante estudiante = arbolOrigen.buscar(matricula);

        if (estudiante == null) {
            return false;
        }

        if (arbolDestino.buscar(matricula) != null) {
            return false;
        }

        if (!arbolDestino.insertar(estudiante)) {
            return false;
        }

        boolean eliminadoDeOrigen;

        try {
            eliminadoDeOrigen = arbolOrigen.eliminar(matricula);
        } catch (RuntimeException e) {
            eliminadoDeOrigen = false;
        }

        if (!eliminadoDeOrigen) {

            if (!arbolDestino.eliminar(matricula)) {
                throw new IllegalStateException(
                    "No se pudo revertir el traslado de la matricula "
                    + matricula
                );
            }

            return false;
        }

        return true;
    }

    // A6: Representacion hijo izquierdo / hermano derecho
    public String transformarABinario() {

        if (raiz == null) {
            return "Arbol vacio.";
        }

        StringBuilder sb = new StringBuilder();

        transformarNodo(
            raiz,
            0,
            "Raiz",
            sb
        );

        return sb.toString().stripTrailing();
    }

    private void transformarNodo(
            NodoUnidad nodo,
            int profundidad,
            String relacion,
            StringBuilder sb) {

        if (nodo == null) {
            return;
        }

        sb.append("  ".repeat(profundidad))
          .append(relacion)
          .append(": ")
          .append(nodo.getDato().getCodigo())
          .append(System.lineSeparator());

        List<NodoUnidad> hijos = nodo.getHijos();

        if (!hijos.isEmpty()) {
            transformarHermanos(
                hijos,
                0,
                profundidad + 1,
                "Hijo Izq de " + nodo.getDato().getCodigo(),
                sb
            );
        }
    }

    private void transformarHermanos(
            List<NodoUnidad> hermanos,
            int indice,
            int profundidad,
            String relacion,
            StringBuilder sb) {

        if (indice >= hermanos.size()) {
            return;
        }

        NodoUnidad actual = hermanos.get(indice);

        // Primero se representa el nodo y su hijo izquierdo
        transformarNodo(
            actual,
            profundidad,
            relacion,
            sb
        );

        // Luego el siguiente hermano derecho del nodo actual
        if (indice + 1 < hermanos.size()) {

            transformarHermanos(
                hermanos,
                indice + 1,
                profundidad + 1,
                "Hermano Der de "
                    + actual.getDato().getCodigo(),
                sb
            );
        }
    }
}
