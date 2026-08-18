import java.io.*;
import java.util.*;

// ==========================================
// JERARQUÍA DE PERSONAS (Herencia y Encapsulamiento)
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
    private double[] notas;

    public Alumno(String cedula, String nombre, String correo, String tipoPrograma, double[] notas) {
        super(cedula, nombre, correo);
        this.tipoPrograma = tipoPrograma;
        this.notas = (notas != null) ? notas : new double[]{0.0, 0.0, 0.0};
    }

    public String getTipoPrograma() { return tipoPrograma; }
    public double[] getNotas() { return notas; }
    public void setNotas(double[] notas) { this.notas = notas; }
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
// JERARQUÍA DE PROGRAMAS ACADÉMICOS (Polimorfismo)
// ==========================================
abstract class ProgramaAcademico {
    public abstract boolean evaluarAprobacion(double[] notas);
    public double calcularPromedio(double[] notas) {
        if (notas == null || notas.length == 0) return 0.0;
        double suma = 0;
        for (double n : notas) suma += n;
        return suma / notas.length;
    }
}

class Curso extends ProgramaAcademico {
    @Override
    public boolean evaluarAprobacion(double[] notas) {
        return calcularPromedio(notas) >= 10.0;
    }
}

class Diplomado extends ProgramaAcademico {
    @Override
    public boolean evaluarAprobacion(double[] notas) {
        return calcularPromedio(notas) >= 14.0;
    }
}

class Bootcamp extends ProgramaAcademico {
    @Override
    public boolean evaluarAprobacion(double[] notas) {
        if (notas == null || notas.length == 0) return false;
        // Regla estricta: Ninguna nota individual puede ser menor a 14
        for (double n : notas) {
            if (n < 14.0) {
                return false;
            }
        }
        return true;
    }
}

// Clase para la Pila LIFO (Historial de Notas)
class HistorialNota {
    private Alumno alumno;
    private double[] notasAnteriores;

    public HistorialNota(Alumno alumno, double[] notasAnteriores) {
        this.alumno = alumno;
        this.notasAnteriores = notasAnteriores.clone();
    }

    public Alumno getAlumno() { return alumno; }
    public double[] getNotasAnteriores() { return notasAnteriores; }
}

// ==========================================
// CLASE PRINCIPAL DEL SISTEMA (SGA-DO)
// ==========================================
public class Main {
    private static List<Alumno> alumnos = new ArrayList<>();
    private static List<Profesor> profesores = new ArrayList<>();
    private static Stack<HistorialNota> pilaHistorialNotas = new Stack<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        cargarDatos();

        int opcion = 0;
        do {
            System.out.println("\n==================================================");
            System.out.println("    SGA-DO: SISTEMA DIPLOMADOSONLINE (JAVA)");
            System.out.println("==================================================");
            System.out.println("1. Registrar Alumno");
            System.out.println("2. Registrar Profesor");
            System.out.println("3. Registrar Notas a un Alumno");
            System.out.println("4. Deshacer Último Registro de Nota");
            System.out.println("5. Generar Cola de Certificados");
            System.out.println("6. Mostrar Reporte General");
            System.out.println("7. Salir");
            System.out.println("==================================================");
            System.out.print("Seleccione una opción (1-7): ");

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
                switch (opcion) {
                    case 1: registrarAlumno(); break;
                    case 2: registrarProfesor(); break;
                    case 3: registrarNotas(); break;
                    case 4: deshacerUltimoRegistroNota(); break;
                    case 5: generarColaCertificados(); break;
                    case 6: mostrarReporteGeneral(); break;
                    case 7: System.out.println("\nSaliendo del sistema. ¡Hasta luego!"); break;
                    default: System.out.println("Opción inválida. Intente de nuevo (1-7).");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            }
        } while (opcion != 7);
    }

    public static void cargarDatos() {
        File archivoAlumnos = new File("alumnos.txt");
        if (archivoAlumnos.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivoAlumnos))) {
                String linea;
                alumnos.clear();
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        String[] partes = linea.split(",");
                        if (partes.length >= 7) {
                            String cedula = partes[0].trim();
                            String nombre = partes[1].trim();
                            String correo = partes[2].trim();
                            String tipo = partes[3].trim();
                            double[] notas = {
                                Double.parseDouble(partes[4].trim()),
                                Double.parseDouble(partes[5].trim()),
                                Double.parseDouble(partes[6].trim())
                            };
                            alumnos.add(new Alumno(cedula, nombre, correo, tipo, notas));
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al leer alumnos.txt: " + e.getMessage());
            }
        }

        File archivoProfesores = new File("profesores.txt");
        if (archivoProfesores.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivoProfesores))) {
                String linea;
                profesores.clear();
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        String[] partes = linea.split(",");
                        if (partes.length >= 5) {
                            String cedula = partes[0].trim();
                            String nombre = partes[1].trim();
                            String correo = partes[2].trim();
                            String esp = partes[3].trim();
                            String mat = partes[4].trim();
                            profesores.add(new Profesor(cedula, nombre, correo, esp, mat));
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al leer profesores.txt: " + e.getMessage());
            }
        }
    }

    public static void guardarAlumnos() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("alumnos.txt"))) {
            for (Alumno a : alumnos) {
                double[] n = a.getNotas();
                pw.printf("%s,%s,%s,%s,%.0f,%.0f,%.0f\n", 
                    a.getCedula(), a.getNombre(), a.getCorreo(), a.getTipoPrograma(), n[0], n[1], n[2]);
            }
        } catch (IOException e) {
            System.out.println("Error al guardar alumnos.txt: " + e.getMessage());
        }
    }

    public static void guardarProfesores() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("profesores.txt"))) {
            for (Profesor p : profesores) {
                pw.printf("%s,%s,%s,%s,%s\n", 
                    p.getCedula(), p.getNombre(), p.getCorreo(), p.getEspecialidad(), p.getMateria());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar profesores.txt: " + e.getMessage());
        }
    }

    public static void registrarAlumno() {
        System.out.println("\n--- Registrar Alumno ---");
        System.out.print("Ingrese Cédula/ID (ej. V-111): ");
        String cedula = scanner.nextLine().trim();
        System.out.print("Ingrese Nombre Completo: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Ingrese Correo Electrónico: ");
        String correo = scanner.nextLine().trim();
        System.out.print("Ingrese Tipo de Programa (Curso/Diplomado/Bootcamp): ");
        String tipo = scanner.nextLine().trim();

        alumnos.add(new Alumno(cedula, nombre, correo, tipo, new double[]{0.0, 0.0, 0.0}));
        guardarAlumnos();
        System.out.println("¡Alumno registrado y guardado con éxito!");
    }

    public static void registrarProfesor() {
        System.out.println("\n--- Registrar Profesor ---");
        System.out.print("Ingrese Cédula/ID: ");
        String cedula = scanner.nextLine().trim();
        System.out.print("Ingrese Nombre Completo: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Ingrese Correo Electrónico: ");
        String correo = scanner.nextLine().trim();
        System.out.print("Ingrese Especialidad: ");
        String especialidad = scanner.nextLine().trim();
        System.out.print("Ingrese Materia Asignada: ");
        String materia = scanner.nextLine().trim();

        profesores.add(new Profesor(cedula, nombre, correo, especialidad, materia));
        guardarProfesores();
        System.out.println("¡Profesor registrado con éxito!");
    }

    public static void registrarNotas() {
        System.out.println("\n--- Registrar Notas a un Alumno ---");
        System.out.print("Ingrese la Cédula del alumno: ");
        String cedula = scanner.nextLine().trim();
        
        Alumno alumnoEncontrado = null;
        for (Alumno a : alumnos) {
            if (a.getCedula().equalsIgnoreCase(cedula)) {
                alumnoEncontrado = a;
                break;
            }
        }

        if (alumnoEncontrado == null) {
            System.out.println("Error: Alumno no encontrado.");
            return;
        }

        System.out.println("Alumno: " + alumnoEncontrado.getNombre() + " | Programa: " + alumnoEncontrado.getTipoPrograma());
        try {
            System.out.print("Ingrese Nota 1: ");
            double n1 = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Ingrese Nota 2: ");
            double n2 = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Ingrese Nota 3: ");
            double n3 = Double.parseDouble(scanner.nextLine().trim());

            // Respaldo en la Pila LIFO antes de mutar
            pilaHistorialNotas.push(new HistorialNota(alumnoEncontrado, alumnoEncontrado.getNotas()));

            alumnoEncontrado.setNotas(new double[]{n1, n2, n3});
            guardarAlumnos();
            System.out.println("¡Notas registradas con éxito! (Acción respaldada en Pila)");
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar valores numéricos válidos.");
        }
    }

    public static void deshacerUltimoRegistroNota() {
        System.out.println("\n--- Deshacer Último Registro de Nota ---");
        if (pilaHistorialNotas.isEmpty()) {
            System.out.println("No hay acciones de notas para deshacer.");
            return;
        }

        HistorialNota ultimoCambio = pilaHistorialNotas.pop();
        Alumno alumno = ultimoCambio.getAlumno();
        alumno.setNotas(ultimoCambio.getNotasAnteriores());
        guardarAlumnos();
        
        double[] n = alumno.getNotas();
        System.out.printf("Se han revertido las notas del alumno %s a su estado anterior: %.0f, %.0f, %.0f\n", 
            alumno.getNombre(), n[0], n[1], n[2]);
    }

    public static void generarColaCertificados() {
        System.out.println("\n--- Generar Cola de Certificados ---");
        Queue<Map<String, Object>> colaAprobados = new LinkedList<>();

        for (Alumno alumno : alumnos) {
            ProgramaAcademico evaluador = null;
            String tipoLower = alumno.getTipoPrograma().toLowerCase();
            
            if (tipoLower.contains("curso")) {
                evaluador = new Curso();
            } else if (tipoLower.contains("diplomado")) {
                evaluador = Diplomado();
            } else if (tipoLower.contains("bootcamp")) {
                evaluador = Bootcamp();
            }

            if (evaluador != null) {
                double[] notas = alumno.getNotas();
                boolean aprobado = evaluador.evaluarAprobacion(notas);
                double promedio = evaluador.calcularPromedio(notas);

                if (aprobado) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("alumno", alumno);
                    item.put("promedio", promedio);
                    colaAprobados.add(item);
                }
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter("certificados_pendientes.txt"))) {
            pw.println("=========================================");
            pw.println("REPORTE DE CERTIFICADOS PENDIENTES");
            pw.println("=========================================");
            pw.println("Total de graduandos en cola: " + colaAprobados.size() + "\n");

            int contador = 1;
            while (!colaAprobados.isEmpty()) {
                Map<String, Object> item = colaAprobados.poll();
                Alumno al = (Alumno) item.get("alumno");
                double prom = (double) item.get("promedio");

                pw.println(contador + ". [" + al.getCedula() + "] " + al.getNombre());
                pw.println("    - Programa: " + al.getTipoPrograma());
                pw.printf("    - Promedio Final: %.1f\n", prom);

                if (al.getTipoPrograma().toLowerCase().contains("bootcamp")) {
                    pw.println("    - Estatus: APROBADO (Cumple regla de ninguna nota < 14)\n");
                } else {
                    pw.println("    - Estatus: APROBADO\n");
                }
                contador++;
            }

            pw.println("=========================================");
            pw.println("* Fin del reporte - Generado por SGA-DO *");
            System.out.println("¡Cola de certificados generada con éxito y guardada en 'certificados_pendientes.txt'!");
        } catch (IOException e) {
            System.out.println("Error al generar el archivo de certificados: " + e.getMessage());
        }
    }

    public static void mostrarReporteGeneral() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println(" REPORTE GENERAL - SGA-DO (JAVA)");
        System.out.println("=".repeat(40));
        
        System.out.println("\n--- PROFESORES (" + profesores.size() + ") ---");
        for (Profesor p : profesores) {
            System.out.println("ID: " + p.getCedula() + " | Nombre: " + p.getNombre() + " | Correo: " + p.getCorreo() + " | Esp: " + p.getEspecialidad() + " | Mat: " + p.getMateria());
        }

        System.out.println("\n--- ALUMNOS (" + alumnos.size() + ") ---");
        for (Alumno a : alumnos) {
            double[] n = a.getNotas();
            System.out.printf("ID: %s | Nombre: %s | Programa: %s | Notas: [%.0f, %.0f, %.0f]\n", 
                a.getCedula(), a.getNombre(), a.getTipoPrograma(), n[0], n[1], n[2]);
        }
        System.out.println("=".repeat(40));
    }
}

           


     

            
        
         
          
       
