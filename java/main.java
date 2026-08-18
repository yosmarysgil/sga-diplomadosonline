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
    public String getCorreo() { return correo; }
}

// ==========================================
// CLASE DERIVADA (Polimorfismo)
// ==========================================
class Alumno extends Persona {
    private String tipoPrograma;
    private double[] notas;

    public Alumno(String cedula, String nombre, String correo, String tipoPrograma, double[] notas) {
        super(cedula, nombre, correo);
        this.tipoPrograma = tipoPrograma;
        this.notas = notas;
    }

    public String getTipoPrograma() { return tipoPrograma; }
    public double[] getNotas() { return notas; }

    @Override
    public String toString() {
        return getCedula() + "," + getNombre() + "," + getCorreo() + "," + tipoPrograma + "," + notas[0] + "," + notas[1] + "," + notas[2];
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
        
        // Simulación de prueba para verificar que cargó la data semilla
        System.out.println("Alumnos totales en memoria tras cargar archivo: " + alumnos.size());
    }

    // Método robusto para leer y procesar la data semilla ante las pruebas del profesor
    private static void cargarDatosDesdeArchivo() {
        File archivo = new File("alumnos.txt");
        if (!archivo.exists()) {
            return; // Si no existe aún, no hace nada
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                
                String[] partes = linea.split(",");
                if (partes.length >= 7) {
                    String cedula = partes[0];
                    String nombre = partes[1];
                    String correo = partes[2];
                    String tipo = partes[3];
                    double n1 = Double.parseDouble(partes[4]);
                    double n2 = Double.parseDouble(partes[5]);
                    double n3 = Double.parseDouble(partes[6]);
                    
                    double[] notas = {n1, n2, n3};
                    Alumno alumno = new Alumno(cedula, nombre, correo, tipo, notas);
                    alumnos.add(alumno);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo de alumnos: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error en el formato numérico de las notas dentro del archivo.");
        }
    }

    public static void registrarAlumno(String cedula, String nombre, String correo, String tipo, double[] notas) {
        Alumno nuevoAlumno = new Alumno(cedula, nombre, correo, tipo, notas);
        alumnos.add(nuevoAlumno);
        pilaDeshacer.push(cedula); // Pila oficial (LIFO)
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
}

    
    


     

            
        
         
          
       
