import java.io.*;
import java.util.*;

// ==========================================
// CLASE BASE (Herencia y Encapsulamiento)
// ==========================================
class Persona {
    private String cedula;
    private String nombre;
    private String correo;

    public Persona(String cedula, String nombre, String correo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
}

// ==========================================
// CLASES DERIVADAS (Polimorfismo)
// ==========================================
class Alumno extends Persona {
    private String tipoPrograma;
    private double[] notas;

    public Alumno(String cedula, String nombre, String correo, String tipoPrograma, double[] notas) {
        super(cedula, nombre, correo);
        this.tipoPrograma = tipoPrograma;
        this.notas = notas;
    }

    @Override
    public String toString() {
        return getCedula() + "," + getNombre() + "," + tipoPrograma + "," + Arrays.toString(notas);
    }
}

// ==========================================
// CLASE PRINCIPAL Y GESTIÓN
// ==========================================
public class Main {
    private static List<Alumno> alumnos = new ArrayList<>();
    private static Stack<String> pilaDeshacer = new Stack<>();
    private static Queue<String> colaGraduandos = new LinkedList<>();

    public static void main(String[] args) {
        cargarDatosDesdeArchivo();
        menuPrincipal();
    }

    private static void cargarDatosDesdeArchivo() {
        try (BufferedReader br = new BufferedReader(new FileReader("alumnos.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Lógica de parseo simple para la data semilla
                System.out.println("Cargado: " + linea);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar archivos: " + e.getMessage());
        }
    }

    public static void registrarAlumno(String cedula, String nombre, String correo, String tipo, double[] notas) {
        Alumno nuevoAlumno = new Alumno(cedula, nombre, correo, tipo, notas);
        alumnos.add(nuevoAlumno);
        pilaDeshacer.push(cedula); // Ejemplo de uso de Stack
        guardarEnArchivo(nuevoAlumno);
    }

    private static void guardarEnArchivo(Alumno a) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("alumnos.txt", true))) {
            bw.write(a.toString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al persistir datos.");
        }
    }

    private static void menuPrincipal() {
        System.out.println("--- Sistema de Gestión Académica (SGA-DO) ---");
        System.out.println("1. Registrar Alumno");
        System.out.println("2. Reporte General");
        System.out.println("3. Salir");
    }
}

    


     

            
        
         
          
       
