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

class Alumno extends Persona {
    private String tipoPrograma;
    private List<Double> notas;

    public Alumno(String cedula, String nombre, String correo, String tipoPrograma, List<Double> notas) {
        super(cedula, nombre, correo);
        this.tipoPrograma = tipoPrograma;
        this.notas = notas != null ? notas : new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0));
    }

    public String getTipoPrograma() { return tipoPrograma; }
    public List<Double> getNotas() { return notas; }
    public void setNotas(List<Double> notas) { this.notas = notas; }
}

class Profesor extends Persona {
    private String especialidad;
    private String materia;

    public Profesor(String cedula, String nombre, String correo, String especialidad, String materia) {
        super(cedula, nombre, correo);
        this.especialidad = especialidad;
        this.materia = materia;
    }

    public String getEspecialidad() { return especialidad; }
    public String getMateria() { return materia; }
}

// ==========================================
// JERARQUÍA DE PROGRAMAS (Polimorfismo)
// ==========================================
abstract class ProgramaAcademico {
    public abstract boolean evaluarAprobacion(List<Double> notas);
    public abstract double calcularPromedio(List<Double> notas);
}

class Curso extends ProgramaAcademico {
    @Override
    public double calcularPromedio(List<Double> notas) {
        if (notas == null || notas.isEmpty()) return 0.0;
        double suma = 0;
        for (double n : notas) suma += n;
        return suma / notas.size();
    }

    @Override
    public boolean evaluarAprobacion(List<Double> notas) {
        return calcularPromedio(notas) >= 10.0;
    }
}

class Diplomado extends ProgramaAcademico {
    @Override
    public double calcularPromedio(List<Double> notas) {
        if (notas == null || notas.isEmpty()) return 0.0;
        double suma = 0;
        for (double n : notas) suma += n;
        return suma / notas.size();
    }

    @Override
    public boolean evaluarAprobacion(List<Double> notas) {
        return calcularPromedio(notas) >= 14.0;
    }
}

class Bootcamp extends ProgramaAcademico {
    @Override
    public double calcularPromedio(List<Double> notas) {
        if (notas == null || notas.isEmpty()) return 0.0;
        double suma = 0;
        for (double n : notas) suma += n;
        return suma / notas.size();
    }

    @Override
    public boolean evaluarAprobacion(List<Double> notas) {
        if (notas == null || notas.isEmpty()) return false;
        for (double n : notas) {
            if (n < 14.0) return false;
        }
        return true;
    }
}

// ==========================================
// CLASE PRINCIPAL DEL SISTEMA (SGA-DO)
// ==========================================
class HistorialNota {
    Alumno alumno;
    List<Double> notasAnteriores;

    public HistorialNota(Alumno alumno, List<Double> notasAnteriores) {
        this.alumno = alumno;
        this.notasAnteriores = new ArrayList<>(notasAnteriores);
    }
}

public class Main {
    private static List<Alumno> alumnos = new ArrayList<>();
    private static List<Profesor> profesores = new ArrayList<>();
    // Pila oficial (LIFO) usando Deque para deshacer el último registro de notas
    private static Deque<HistorialNota> pilaHistorial = new ArrayDeque<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        cargarDatos();
        int opcion = 0;
        do {
            System.out.println("\n==================================================");
            System.out.println("      SGA-DO: SISTEMA DIPLOMADOSONLINE (JAVA)");
            System.out.println("==================================================");
            System.out.println("1. Registrar Alumno");
            System.out.println("2. Registrar Profesor");
            System.out.println("3. Registrar Notas a un Alumno");
            System.out.println("4. Deshacer Ultimo Registro de Nota (Pila)");
            System.out.println("5. Generar Cola de Certificados (Cola)");
            System.out.println("6. Mostrar Reporte General");
            System.out.println("7. Salir");
            System.out.println("==================================================");
            System.out.print("Seleccione una opcion (1-7): ");
            
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1: registrarAlumno(); break;
                    case 2: registrarProfesor(); break;
                    case 3: registrarNotas(); break;
                    case 4: deshacerNota(); break;
                    case 5: generarColaCertificados(); break;
                    case 6: mostrarReporteGeneral(); break;
                    case 7: System.out.println("Saliendo del sistema..."); break;
                    default: System.out.println("Opcion invalida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un numero valido.");
            }
        } while (opcion != 7);
    }

    private static void cargarDatos() {
        try {
            File archivoAlumnos = new File("alumnos.txt");
            if (archivoAlumnos.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(archivoAlumnos));
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] p = linea.split(",");
                    if (p.length >= 7) {
                        List<Double> notas = new ArrayList<>(Arrays.asList(Double.parseDouble(p[4]), Double.parseDouble(p[5]), Double.parseDouble(p[6])));
                        alumnos.add(new Alumno(p[0], p[1], p[2], p[3], notas));
                    }
                }
                br.close();
            }

            File archivoProfesores = new File("profesores.txt");
            if (archivoProfesores.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(archivoProfesores));
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] p = linea.split(",");
                    if (p.length >= 5) {
                        profesores.add(new Profesor(p[0], p[1], p[2], p[3], p[4]));
                    }
                }
                br.close();
            }
        } catch (IOException e) {
            System.out.println("Aviso: No se pudieron cargar los archivos previos.");
        }
    }

    private static void guardarAlumnos() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("alumnos.txt"))) {
            for (Alumno a : alumnos) {
                pw.println(a.getCedula() + "," + a.getNombre() + "," + a.getCorreo() + "," + a.getTipoPrograma() + "," + a.getNotas().get(0) + "," + a.getNotas().get(1) + "," + a.getNotas().get(2));
            }
        } catch (IOException e) {
            System.out.println("Error al guardar alumnos: " + e.getMessage());
        }
    }

    private static void guardarProfesores() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("profesores.txt"))) {
            for (Profesor p : profesores) {
                pw.println(p.getCedula() + "," + p.getNombre() + "," + p.getCorreo() + "," + p.getEspecialidad() + "," + p.getMateria());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar profesores: " + e.getMessage());
        }
    }

    private static void registrarAlumno() {
        System.out.println("\n--- Registrar Alumno ---");
        System.out.print("Cedula: "); String cedula = scanner.nextLine();
        System.out.print("Nombre: "); String nombre = scanner.nextLine();
        System.out.print("Correo: "); String correo = scanner.nextLine();
        System.out.print("Tipo de Programa (Curso, Diplomado, Bootcamp): "); String tipo = scanner.nextLine();

        alumnos.add(new Alumno(cedula, nombre, correo, tipo, new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0))));
        guardarAlumnos();
        System.out.println("Alumno registrado con exito.");
    }

    private static void registrarProfesor() {
        System.out.println("\n--- Registrar Profesor ---");
        System.out.print("Cedula: "); String cedula = scanner.nextLine();
        System.out.print("Nombre: "); String nombre = scanner.nextLine();
        System.out.print("Correo: "); String correo = scanner.nextLine();
        System.out.print("Especialidad: "); String esp = scanner.nextLine();
        System.out.print("Materia: "); String mat = scanner.nextLine();

        profesores.add(new Profesor(cedula, nombre, correo, esp, mat));
        guardarProfesores();
        System.out.println("Profesor registrado con exito.");
    }

    private static void registrarNotas() {
        System.out.println("\n--- Registrar Notas ---");
        System.out.print("Ingrese cedula del alumno: ");
        String cedula = scanner.nextLine();
        Alumno encontrado = null;
        for (Alumno a : alumnos) {
            if (a.getCedula().equals(cedula)) {
                encontrado = a;
                break;
            }
        }
        if (encontrado == null) {
            System.out.println("Alumno no encontrado.");
            return;
        }

        try {
            System.out.print("Nota 1: "); double n1 = Double.parseDouble(scanner.nextLine());
            System.out.print("Nota 2: "); double n2 = Double.parseDouble(scanner.nextLine());
            System.out.print("Nota 3: "); double n3 = Double.parseDouble(scanner.nextLine());

            // Guardar en la Pila (LIFO) antes de sobrescribir
            pilaHistorial.push(new HistorialNota(encontrado, encontrado.getNotas()));

            encontrado.setNotas(new ArrayList<>(Arrays.asList(n1, n2, n3)));
            guardarAlumnos();
            System.out.println("Notas registradas exitosamente.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese valores numericos validos para las notas.");
        }
    }

    private static void deshacerNota() {
        System.out.println("\n--- Deshacer Ultimo Registro ---");
        if (pilaHistorial.isEmpty()) {
            System.out.println("No hay acciones en la pila para deshacer.");
            return;
        }
        HistorialNota ultimo = pilaHistorial.pop();
        ultimo.alumno.setNotas(ultimo.notasAnteriores);
        guardarAlumnos();
        System.out.println("Se han revertido las notas del alumno: " + ultimo.alumno.getNombre());
    }

    private static void generarColaCertificados() {
        // Cola oficial (FIFO) usando Queue
        Queue<Map<String, Object>> colaAprobados = new LinkedList<>();

        for (Alumno a : alumnos) {
            ProgramaAcademico prog = null;
            String tipo = a.getTipoPrograma().toLowerCase();
            if (tipo.contains("curso")) prog = new Curso();
            else if (tipo.contains("diplomado")) prog = new Diplomado();
            else if (tipo.contains("bootcamp")) prog = new Bootcamp();

            if (prog != null && prog.evaluarAprobacion(a.getNotas())) {
                Map<String, Object> datos = new HashMap<>();
                datos.put("alumno", a);
                datos.put("promedio", prog.calcularPromedio(a.getNotas()));
                colaAprobados.add(datos);
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter("certificados_pendientes.txt"))) {
            pw.println("=========================================");
            pw.println("REPORTE DE CERTIFICADOS PENDIENTES (JAVA)");
            pw.println("=========================================");
            int cont = 1;
            while (!colaAprobados.isEmpty()) {
                Map<String, Object> item = colaAprobados.poll(); // FIFO
                Alumno a = (Alumno) item.get("alumno");
                double prom = (Double) item.get("promedio");

                pw.println(cont + ". [" + a.getCedula() + "] " + a.getNombre());
                pw.println("   - Programa: " + a.getTipoPrograma());
                pw.println("   - Promedio: " + prom);
                pw.println();
                cont++;
            }
            pw.println("=========================================");
            System.out.println("Cola de certificados generada en 'certificados_pendientes.txt'.");
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo de certificados: " + e.getMessage());
        }
    }

    private static void mostrarReporteGeneral() {
        System.out.println("\n================ REPORTE GENERAL ================");
        System.out.println("Profesores (" + profesores.size() + "):");
        for (Profesor p : profesores) {
            System.out.println(" - " + p.getNombre() + " | Matriz: " + p.getMateria());
        }
        System.out.println("\nAlumnos (" + alumnos.size() + "):");
        for (Alumno a : alumnos) {
            System.out.println(" - " + a.getNombre() + " | Programa: " + a.getTipoPrograma() + " | Notas: " + a.getNotas());
        }
        System.out.println("=================================================");
    }
}
