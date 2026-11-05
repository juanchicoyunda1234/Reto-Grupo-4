package app;
import negocio.*; import modelo.*; import enums.*;
public class Contexto {
 public ArbolOrganizacional organizacion;
 public final String paralelo="EB-N1-A";
 public Contexto(){reiniciar();}
 public void reiniciar(){
 organizacion=new ArbolOrganizacional(new UnidadAcademica("UTA","Universidad Tecnica de Ambato",TipoUnidad.UNIVERSIDAD));
 organizacion.agregar("UTA",new UnidadAcademica("FCHE","Ciencias Humanas y de la Educacion",TipoUnidad.FACULTAD));
 organizacion.agregar("FCHE",new UnidadAcademica("EB","Educacion Basica",TipoUnidad.CARRERA));
 organizacion.agregar("EB",new UnidadAcademica("EB-N1","Nivel 1",TipoUnidad.NIVEL));
 organizacion.agregar("EB-N1",new UnidadAcademica(paralelo,"Paralelo A",TipoUnidad.PARALELO));
 organizacion.agregar("EB-N1",new UnidadAcademica("EB-N1-B","Paralelo B vacio",TipoUnidad.PARALELO));
 for(int n:new int[]{30,15,45,10,20,40,50,35,55}) estudiantes().insertar(new Estudiante(n,String.format("%010d",n),"Estudiante "+n,8));
 }
 public ArbolEstudiantes estudiantes(){
 NodoUnidad n=organizacion.buscar(paralelo);
 if(n==null)throw new IllegalStateException("El paralelo activo no existe. Reinicie los datos con 7.");
 return n.getEstudiantes();
 }
}

