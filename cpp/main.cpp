#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <vector>
#include <stack>
#include <queue>
#include <iomanip>
#include <limits>
#include <stdexcept>

using namespace std;

// ==========================================
// PARTE 1: JERARQUÍA DE PROGRAMAS ACADÉMICOS (Polimorfismo)
// ==========================================

class ProgramaAcademico {
protected:
    string nombrePrograma;
public:
    explicit ProgramaAcademico(string nombre) : nombrePrograma(move(nombre)) {}
    virtual ~ProgramaAcademico() = default;
    
    string getNombre() const { return nombrePrograma; }
    virtual bool estaAprobado(const vector<float>& notas) const = 0;
};

class Curso : public ProgramaAcademico {
public:
    Curso() : ProgramaAcademico("Curso") {}
    
    bool estaAprobado(const vector<float>& notas) const override {
        if (notas.empty()) return false;
        float suma = 0.0f;
        for (float n : notas) suma += n;
        return (suma / static_cast<float>(notas.size())) >= 10.0f;
    }
};

class Diplomado : public ProgramaAcademico {
public:
    Diplomado() : ProgramaAcademico("Diplomado") {}
    
    bool estaAprobado(const vector<float>& notas) const override {
        if (notas.empty()) return false;
        float suma = 0.0f;
        for (float n : notas) suma += n;
        return (suma / static_cast<float>(notas.size())) >= 14.0f;
    }
};

class Bootcamp : public ProgramaAcademico {
public:
    Bootcamp() : ProgramaAcademico("Bootcamp") {}
    
    bool estaAprobado(const vector<float>& notas) const override {
        if (notas.empty()) return false;
        for (float n : notas) {
            if (n < 14.0f) return false; // Regla estricta: ninguna nota < 14
        }
        return true;
    }
};

// ==========================================
// PARTE 2: JERARQUÍA DE PERSONAS (Herencia)
// ==========================================

class Persona {
protected:
    string cedula;
    string nombreCompleto;
    string correo;

public:
    Persona(string c, string n, string m) 
        : cedula(move(c)), nombreCompleto(move(n)), correo(move(m)) {}
    virtual ~Persona() = default;

    string getCedula() const { return cedula; }
    string getNombre() const { return nombreCompleto; }
    string getCorreo() const { return correo; }

    virtual void mostrarInformacion() const = 0;
};

class Alumno : public Persona {
private:
    ProgramaAcademico* programa; // Puntero a la base (Polimorfismo en Heap)
    vector<float> notas;

public:
    Alumno(string c, string n, string m, const string& tipoProg) 
        : Persona(move(c), move(n), move(m)) {
        if (tipoProg == "Diplomado" || tipoProg == "diplomado") {
            programa = new Diplomado();
        } else if (tipoProg == "Bootcamp" || tipoProg == "bootcamp") {
            programa = new Bootcamp();
        } else {
            programa = new Curso();
        }
    }

    ~Alumno() override {
        delete programa; // Liberación explícita de memoria dinámica
    }

    // Prevenir copia accidental para evitar doble liberación de memoria (Dangling Pointers)
    Alumno(const Alumno&) = delete;
    Alumno& operator=(const Alumno&) = delete;

    ProgramaAcademico* getPrograma() const { return programa; }
    const vector<float>& getNotas() const { return notas; }

    void setNotas(float n1, float n2, float n3) {
        notas = {n1, n2, n3};
    }

    float calcularPromedio() const {
        if (notas.empty()) return 0.0f;
        float suma = 0.0f;
        for (float n : notas) suma += n;
        return suma / static_cast<float>(notas.size());
    }

    bool estaAprobado() const {
        return programa->estaAprobado(notas);
    }

    void mostrarInformacion() const override {
        cout << " [Alumno] ID: " << cedula 
             << " | Nombre: " << nombreCompleto 
             << " | Programa: " << programa->getNombre()
             << " | Promedio: " << fixed << setprecision(1) << calcularPromedio()
             << " | Estatus: " << (estaAprobado() ? "APROBADO" : "REPROBADO") << "\n";
    }
};

class Profesor : public Persona {
private:
    string especialidad;
    string materiaAsignada;

public:
    Profesor(string c, string n, string m, string esp, string mat)
        : Persona(move(c), move(n), move(m)), especialidad(move(esp)), materiaAsignada(move(mat)) {}

    string getEspecialidad() const { return especialidad; }
    string getMateria() const { return materiaAsignada; }

    void mostrarInformacion() const override {
        cout << " [Profesor] ID: " << cedula 
             << " | Nombre: " << nombreCompleto 
             << " | Especialidad: " << especialidad 
             << " | Materia: " << materiaAsignada << "\n";
    }
};

// ==========================================
// ESTRUCTURA DE CONTROL DE HISTORIAL (Pila)
// ==========================================

struct HistorialNotas {
    Alumno* alumno;
    vector<float> notasAnteriores;
};

// ==========================================
// SISTEMA PRINCIPAL DE GESTIÓN ACADÉMICA
// ==========================================

class SistemaSGA {
private:
    vector<Alumno*> listaAlumnos;
    vector<Profesor*> listaProfesores;
    stack<HistorialNotas> pilaHistorial;

    int leerEntero() {
        int val;
        cin >> val;
        if (cin.fail()) {
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            throw invalid_argument("Entrada no valida. Ingrese un entero.");
        }
        return val;
    }

    float leerFlotante() {
        float val;
        cin >> val;
        if (cin.fail()) {
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            throw invalid_argument("Entrada no valida. Ingrese un numero decimal.");
        }
        return val;
    }

    void cargarDatos() {
        ifstream archAlumnos("alumnos.txt");
        if (archAlumnos.is_open()) {
            string linea;
            while (getline(archAlumnos, linea)) {
                if (linea.empty()) continue;
                stringstream ss(linea);
                string c, n, m, prog, n1, n2, n3;

                getline(ss, c, ',');
                getline(ss, n, ',');
                getline(ss, m, ',');
                getline(ss, prog, ',');
                getline(ss, n1, ',');
                getline(ss, n2, ',');
                getline(ss, n3, ',');

                Alumno* al = new Alumno(c, n, m, prog);
                try {
                    al->setNotas(stof(n1), stof(n2), stof(n3));
                } catch (...) {
                    al->setNotas(0.0f, 0.0f, 0.0f);
                }
                listaAlumnos.push_back(al);
            }
            archAlumnos.close();
        }

        ifstream archProf("profesores.txt");
        if (archProf.is_open()) {
            string linea;
            while (getline(archProf, linea)) {
                if (linea.empty()) continue;
                stringstream ss(linea);
                string c, n, m, esp, mat;

                getline(ss, c, ',');
                getline(ss, n, ',');
                getline(ss, m, ',');
                getline(ss, esp, ',');
                getline(ss, mat, ',');

                listaProfesores.push_back(new Profesor(c, n, m, esp, mat));
            }
            archProf.close();
        }
    }

    void guardarAlumnos() {
        ofstream arch("alumnos.txt");
        for (auto al : listaAlumnos) {
            const auto& notas = al->getNotas();
            float n1 = (notas.size() > 0) ? notas[0] : 0.0f;
            float n2 = (notas.size() > 1) ? notas[1] : 0.0f;
            float n3 = (notas.size() > 2) ? notas[2] : 0.0f;

            arch << al->getCedula() << ","
                 << al->getNombre() << ","
                 << al->getCorreo() << ","
                 << al->getPrograma()->getNombre() << ","
                 << n1 << "," << n2 << "," << n3 << "\n";
        }
        arch.close();
    }

    void guardarProfesores() {
        ofstream arch("profesores.txt");
        for (auto pf : listaProfesores) {
            arch << pf->getCedula() << ","
                 << pf->getNombre() << ","
                 << pf->getCorreo() << ","
                 << pf->getEspecialidad() << ","
                 << pf->getMateria() << "\n";
        }
        arch.close();
    }

    Alumno* buscarAlumno(const string& cedula) {
        for (auto al : listaAlumnos) {
            if (al->getCedula() == cedula) return al;
        }
        return nullptr;
    }

public:
    SistemaSGA() { cargarDatos(); }

    ~SistemaSGA() {
        // Gestión manual de memoria: Liberación estricta de todos los punteros almacenados
        for (auto al : listaAlumnos) delete al;
        for (auto pf : listaProfesores) delete pf;
    }

    void registrarAlumno() {
        string c, n, m, tipo;
        cout << "\n--- REGISTRAR ALUMNO ---\n";
        cout << "Cedula / ID: "; cin >> c; cin.ignore();
        cout << "Nombre Completo: "; getline(cin, n);
        cout << "Correo Electronico: "; getline(cin, m);
        cout << "Tipo de Programa (Curso / Diplomado / Bootcamp): "; getline(cin, tipo);

        listaAlumnos.push_back(new Alumno(c, n, m, tipo));
        guardarAlumnos();
        cout << " [✓] Alumno registrado exitosamente.\n";
    }

    void registrarProfesor() {
        string c, n, m, esp, mat;
        cout << "\n--- REGISTRAR PROFESOR ---\n";
        cout << "Cedula / ID: "; cin >> c; cin.ignore();
        cout << "Nombre Completo: "; getline(cin, n);
        cout << "Correo Electronico: "; getline(cin, m);
        cout << "Especialidad: "; getline(cin, esp);
        cout << "Materia Asignada: "; getline(cin, mat);

        listaProfesores.push_back(new Profesor(c, n, m, esp, mat));
        guardarProfesores();
        cout << " [✓] Profesor registrado exitosamente.\n";
    }

    void registrarNotas() {
        string c;
        cout << "\n--- REGISTRAR NOTAS A UN ALUMNO ---\n";
        cout << "Ingrese Cedula del Alumno: "; cin >> c;

        Alumno* al = buscarAlumno(c);
        if (al) {
            try {
                cout << "Ingrese Nota 1: "; float n1 = leerFlotante();
                cout << "Ingrese Nota 2: "; float n2 = leerFlotante();
                cout << "Ingrese Nota 3: "; float n3 = leerFlotante();

                // Respaldar estado previo en la Pila
                pilaHistorial.push({al, al->getNotas()});

                al->setNotas(n1, n2, n3);
                guardarAlumnos();
                cout << " [✓] Notas registradas correctamente (Pila actualizada).\n";
            } catch (const invalid_argument& e) {
                cout << " [!] " << e.what() << "\n";
            }
        } else {
            cout << " [!] Error: Alumno no encontrado.\n";
        }
    }

    void deshacerUltimaNota() {
        cout << "\n--- DESHACER ULTIMO REGISTRO DE NOTA ---\n";
        if (pilaHistorial.empty()) {
            cout << " [!] No hay historial para deshacer.\n";
            return;
        }

        HistorialNotas ultimo = pilaHistorial.top();
        pilaHistorial.pop();

        const auto& nAnt = ultimo.notasAnteriores;
        float n1 = (nAnt.size() > 0) ? nAnt[0] : 0.0f;
        float n2 = (nAnt.size() > 1) ? nAnt[1] : 0.0f;
        float n3 = (nAnt.size() > 2) ? nAnt[2] : 0.0f;

        ultimo.alumno->setNotas(n1, n2, n3);
        guardarAlumnos();
        cout << " [✓] Se revirtieron las notas de " << ultimo.alumno->getNombre() << " con exito.\n";
    }

    void generarColaCertificados() {
        cout << "\n--- GENERAR COLA DE CERTIFICADOS ---\n";
        queue<Alumno*> colaAprobados;

        for (auto al : listaAlumnos) {
            if (al->estaAprobado()) {
                colaAprobados.push(al);
            }
        }

        ofstream arch("certificados_pendientes.txt");
        arch << "=========================================\n";
        arch << "REPORTE DE CERTIFICADOS PENDIENTES\n";
        arch << "=========================================\n";
        arch << "Total graduandos en cola: " << colaAprobados.size() << "\n\n";

        int contador = 1;
        while (!colaAprobados.empty()) {
            Alumno* al = colaAprobados.front();
            colaAprobados.pop();

            arch << contador++ << ". [" << al->getCedula() << "] " << al->getNombre() << "\n"
                 << "    - Programa: " << al->getPrograma()->getNombre() << "\n"
                 << "    - Promedio Final: " << fixed << setprecision(1) << al->calcularPromedio() << "\n"
                 << "    - Estatus: APROBADO\n\n";
        }
        arch << "=========================================\n";
        arch.close();

        cout << " [✓] Cola procesada y guardada en 'certificados_pendientes.txt'.\n";
    }

    void mostrarReporteGeneral() {
        cout << "\n=========================================\n";
        cout << "        REPORTE GENERAL - SGA-DO         \n";
        cout << "=========================================\n";

        cout << "\n--- PROFESORES (" << listaProfesores.size() << ") ---\n";
        for (auto pf : listaProfesores) pf->mostrarInformacion();

        cout << "\n--- ALUMNOS (" << listaAlumnos.size() << ") ---\n";
        for (auto al : listaAlumnos) al->mostrarInformacion();
        cout << "=========================================\n";
    }

    void ejecutar() {
        int opcion = 0;
        do {
            cout << "\n==================================================\n";
            cout << "    SGA-DO: SISTEMA DIPLOMADOSONLINE (C++)        \n";
            cout << "==================================================\n";
            cout << "1. Registrar Alumno\n";
            cout << "2. Registrar Profesor\n";
            cout << "3. Registrar Notas a un Alumno\n";
            cout << "4. Deshacer Ultimo Registro de Nota\n";
            cout << "5. Generar Cola de Certificados\n";
            cout << "6. Mostrar Reporte General\n";
            cout << "7. Salir\n";
            cout << "==================================================\n";
            cout << "Seleccione una opcion (1-7): ";

            try {
                opcion = leerEntero();
                switch (opcion) {
                    case 1: registrarAlumno(); break;
                    case 2: registrarProfesor(); break;
                    case 3: registrarNotas(); break;
                    case 4: deshacerUltimaNota(); break;
                    case 5: generarColaCertificados(); break;
                    case 6: mostrarReporteGeneral(); break;
                    case 7: cout << "\nGuardando cambios y cerrando programa...\n"; break;
                    default: cout << " [!] Opcion fuera de rango (1-7).\n";
                }
            } catch (const invalid_argument& e) {
                cout << " [!] " << e.what() << "\n";
            }
        } while (opcion != 7);
    }
};

int main() {
    SistemaSGA sistema;
    sistema.ejecutar();
    return 0;
}

    
    

  
   
     

   

        
    

  
              
       
            
   
   

   


        
     

 


 



    
     
