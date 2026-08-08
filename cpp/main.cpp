#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <sstream>
#include <stack>
#include <queue>

using namespace std;

// ==========================================
// CLASE BASE (Herencia y Encapsulamiento)
// ==========================================
class Persona {
private:
    string cedula;
    string nombre;
    string correo;

public:
    Persona(string c, string n, string co) : cedula(c), nombre(n), correo(co) {}
    virtual ~Persona() {}

    string getCedula() const { return cedula; }
    string getNombre() const { return nombre; }
    string getCorreo() const { return correo; }
};

class Alumno : public Persona {
private:
    string tipoPrograma;
    vector<double> notas;

public:
    Alumno(string c, string n, string co, string tipo, vector<double> nt) 
        : Persona(c, n, co), tipoPrograma(tipo), notas(nt) {
        if (notas.empty()) notas = {0.0, 0.0, 0.0};
    }

    string getTipoPrograma() const { return tipoPrograma; }
    vector<double> getNotas() const { return notas; }
    void setNotas(const vector<double>& nt) { notas = nt; }
};

class Profesor : public Persona {
private:
    string especialidad;
    string materia;

public:
    Profesor(string c, string n, string co, string esp, string mat) 
        : Persona(c, n, co), especialidad(esp), materia(mat) {}

    string getEspecialidad() const { return especialidad; }
    string getMateria() const { return materia; }
};

// ==========================================
// JERARQUÍA DE PROGRAMAS (Polimorfismo)
// ==========================================
class ProgramaAcademico {
public:
    virtual double calcularPromedio(const vector<double>& notas) = 0;
    virtual bool evaluarAprobacion(const vector<double>& notas) = 0;
    virtual ~ProgramaAcademico() {}
};

class Curso : public ProgramaAcademico {
public:
    double calcularPromedio(const vector<double>& notas) override {
        if (notas.empty()) return 0.0;
        double suma = 0;
        for (double n : notas) suma += n;
        return suma / notas.size();
    }

    bool evaluarAprobacion(const vector<double>& notas) override {
        return calcularPromedio(notas) >= 10.0;
    }
};

class Diplomado : public ProgramaAcademico {
public:
    double calcularPromedio(const vector<double>& notas) override {
        if (notas.empty()) return 0.0;
        double suma = 0;
        for (double n : notas) suma += n;
        return suma / notas.size();
    }

    bool evaluarAprobacion(const vector<double>& notas) override {
        return calcularPromedio(notas) >= 14.0;
    }
};

class Bootcamp : public ProgramaAcademico {
public:
    double calcularPromedio(const vector<double>& notas) override {
        if (notas.empty()) return 0.0;
        double suma = 0;
        for (double n : notas) suma += n;
        return suma / notas.size();
    }

    bool evaluarAprobacion(const vector<double>& notas) override {
        if (notas.empty()) return false;
        for (double n : notas) {
            if (n < 14.0) return false;
        }
        return true;
    }
};

// ==========================================
// ESTRUCTURA PARA LA PILA DE NOTAS
// ==========================================
struct HistorialNota {
    Alumno* alumno;
    vector<double> notasAnteriores;
};

// Variables globales del sistema usando punteros y colecciones de C++
vector<Alumno*> alumnos;
vector<Profesor*> profesores;
stack<HistorialNota> pilaHistorial;

// Prototipos de funciones de persistencia y menú
void cargarDatos();
void guardarAlumnos();
void guardarProfesores();
void registrarAlumno();
void registrarProfesor();
void registrarNotas();
void deshacerNota();
void generarColaCertificados();
void mostrarReporteGeneral();
void liberarMemoria();

int main() {
    cargarDatos();
    int opcion = 0;
    do {
        cout << "\n==================================================\n";
        cout << "      SGA-DO: SISTEMA DIPLOMADOSONLINE (C++)\n";
        cout << "==================================================\n";
        cout << "1. Registrar Alumno\n";
        cout << "2. Registrar Profesor\n";
        cout << "3. Registrar Notas a un Alumno\n";
        cout << "4. Deshacer Ultimo Registro de Nota (Pila)\n";
        cout << "5. Generar Cola de Certificados (Cola)\n";
        cout << "6. Mostrar Reporte General\n";
        cout << "7. Salir\n";
        cout << "==================================================\n";
        cout << "Seleccione una opcion (1-7): ";
        
        if (!(cin >> opcion)) {
            cout << "Error: Entrada invalida.\n";
            cin.clear();
            cin.ignore(10000, '\n');
            continue;
        }
        cin.ignore(); // Limpiar buffer

        switch (opcion) {
            case 1: registrarAlumno(); break;
            case 2: registrarProfesor(); break;
            case 3: registrarNotas(); break;
            case 4: deshacerNota(); break;
            case 5: generarColaCertificados(); break;
            case 6: mostrarReporteGeneral(); break;
            case 7: cout << "Saliendo del sistema...\n"; break;
            default: cout << "Opcion invalida.\n";
        }
    } while (opcion != 7);

    liberarMemoria();
    return 0;
}

// ==========================================
// PERSISTENCIA CON FSTREAM
// ==========================================
void cargarDatos() {
    ifstream archivoAlumnos("alumnos.txt");
    if (archivoAlumnos.is_open()) {
        string linea;
        while (getline(archivoAlumnos, linea)) {
            stringstream ss(linea);
            string cedula, nombre, correo, tipo, n1, n2, n3;
            if (getline(ss, cedula, ',') && getline(ss, nombre, ',') && 
                getline(ss, correo, ',') && getline(ss, tipo, ',') &&
                getline(ss, n1, ',') && getline(ss, n2, ',') && getline(ss, n3, ',')) {
                vector<double> notas = {stod(n1), stod(n2), stod(n3)};
                alumnos.push_back(new Alumno(cedula, nombre, correo, tipo, notas));
            }
        }
        archivoAlumnos.close();
    }

    ifstream archivoProfesores("profesores.txt");
    if (archivoProfesores.is_open()) {
        string linea;
        while (getline(archivoProfesores, linea)) {
            stringstream ss(linea);
            string cedula, nombre, correo, esp, mat;
            if (getline(ss, cedula, ',') && getline(ss, nombre, ',') && 
                getline(ss, correo, ',') && getline(ss, esp, ',') && getline(ss, mat, ',')) {
                profesores.push_back(new Profesor(cedula, nombre, correo, esp, mat));
            }
        }
        archivoProfesores.close();
    }
}

void guardarAlumnos() {
    ofstream archivo("alumnos.txt");
    if (archivo.is_open()) {
        for (const auto& a : alumnos) {
            vector<double> n = a->getNotas();
            archivo << a->getCedula() << "," << a->getNombre() << "," << a->getCorreo() << "," 
                    << a->getTipoPrograma() << "," << n[0] << "," << n[1] << "," << n[2] << "\n";
        }
        archivo.close();
    }
}

void guardarProfesores() {
    ofstream archivo("profesores.txt");
    if (archivo.is_open()) {
        for (const auto& p : profesores) {
            archivo << p->getCedula() << "," << p->getNombre() << "," << p->getCorreo() << "," 
                    << p->getEspecialidad() << "," << p->getMateria() << "\n";
        }
        archivo.close();
    }
}

// ==========================================
// FUNCIONES DEL SISTEMA
// ==========================================
void registrarAlumno() {
    cout << "\n--- Registrar Alumno ---\n";
    string cedula, nombre, correo, tipo;
    cout << "Cedula: "; getline(cin, cedula);
    cout << "Nombre: "; getline(cin, nombre);
    cout << "Correo: "; getline(cin, correo);
    cout << "Tipo de Programa (Curso, Diplomado, Bootcamp): "; getline(cin, tipo);

    alumnos.push_back(new Alumno(cedula, nombre, correo, tipo, {0.0, 0.0, 0.0}));
    guardarAlumnos();
    cout << "Alumno registrado con exito.\n";
}

void registrarProfesor() {
    cout << "\n--- Registrar Profesor ---\n";
    string cedula, nombre, correo, esp, mat;
    cout << "Cedula: "; getline(cin, cedula);
    cout << "Nombre: "; getline(cin, nombre);
    cout << "Correo: "; getline(cin, correo);
    cout << "Especialidad: "; getline(cin, esp);
    cout << "Materia: "; getline(cin, mat);

    profesores.push_back(new Profesor(cedula, nombre, correo, esp, mat));
    guardarProfesores();
    cout << "Profesor registrado con exito.\n";
}

void registrarNotas() {
    cout << "\n--- Registrar Notas ---\n";
    string cedula;
    cout << "Ingrese cedula del alumno: "; getline(cin, cedula);
    
    Alumno* encontrado = nullptr;
    for (auto& a : alumnos) {
        if (a->getCedula() == cedula) {
            encontrado = a;
            break;
        }
    }

    if (!encontrado) {
        cout << "Alumno no encontrado.\n";
        return;
    }

    double n1, n2, n3;
    cout << "Nota 1: "; cin >> n1;
    cout << "Nota 2: "; cin >> n2;
    cout << "Nota 3: "; cin >> n3;
    cin.ignore();

    // Guardar en la Pila antes de modificar
    pilaHistorial.push({encontrado, encontrado->getNotas()});

    encontrado->setNotas({n1, n2, n3});
    guardarAlumnos();
    cout << "Notas registradas exitosamente.\n";
}

void deshacerNota() {
    cout << "\n--- Deshacer Ultimo Registro ---\n";
    if (pilaHistorial.empty()) {
        cout << "No hay acciones en la pila para deshacer.\n";
        return;
    }

    HistorialNota ultimo = pilaHistorial.top();
    pilaHistorial.pop();
    ultimo.alumno->setNotas(ultimo.notasAnteriores);
    guardarAlumnos();
    cout << "Se han revertido las notas del alumno: " << ultimo.alumno->getNombre() << "\n";
}

void generarColaCertificados() {
    queue<pair<Alumno*, double>> colaAprobados;

    for (auto& a : alumnos) {
        ProgramaAcademico* prog = nullptr;
        string tipo = a->getTipoPrograma();
        if (tipo.find("Curso") != string::npos || tipo.find("curso") != string::npos) prog = new Curso();
        else if (tipo.find("Diplomado") != string::npos || tipo.find("diplomado") != string::npos) prog = new Diplomado();
        else if (tipo.find("Bootcamp") != string::npos || tipo.find("bootcamp") != string::npos) prog = new Bootcamp();

        if (prog) {
            if (prog->evaluarAprobacion(a->getNotas())) {
                colaAprobados.push({a, prog->calcularPromedio(a->getNotas())});
            }
            delete prog; // Liberar memoria dinámica temporal del polimorfismo
        }
    }

    ofstream archivo("certificados_pendientes.txt");
    if (archivo.is_open()) {
        archivo << "=========================================\n";
        archivo << "REPORTE DE CERTIFICADOS PENDIENTES (C++)\n";
        archivo << "=========================================\n";
        int cont = 1;
        while (!colaAprobados.empty()) {
            auto item = colaAprobados.front();
            colaAprobados.pop();
            Alumno* a = item.first;
            double prom = item.second;

            archivo << cont << ". [" << a->getCedula() << "] " << a->getNombre() << "\n";
            archivo << "   - Programa: " << a->getTipoPrograma() << "\n";
            archivo << "   - Promedio: " << prom << "\n\n";
            cont++;
        }
        archivo << "=========================================\n";
        archivo.close();
        cout << "Cola de certificados generada en 'certificados_pendientes.txt'.\n";
    }
}

void mostrarReporteGeneral() {
    cout << "\n================ REPORTE GENERAL ================\n";
    cout << "Profesores (" << profesores.size() << "):\n";
    for (const auto& p : profesores) {
        cout << " - " << p->getNombre() << " | Materia: " << p->getMateria() << "\n";
    }
    cout << "\nAlumnos (" << alumnos.size() << "):\n";
    for (const auto& a : alumnos) {
        cout << " - " << a->getNombre() << " | Programa: " << a->getTipoPrograma() << "\n";
    }
    cout << "=================================================\n";
}

// ==========================================
// LIBERAR MEMORIA MANUAL (DELETE)
// ==========================================
void liberarMemoria() {
    for (auto& a : alumnos) delete a;
    for (auto& p : profesores) delete p;
}
