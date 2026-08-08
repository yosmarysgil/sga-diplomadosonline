import os
from collections import deque

# ==========================================
# JERARQUÍA DE PERSONAS (Herencia)
# ==========================================
class Persona:
    def __init__(self, cedula, nombre, correo):
        self.cedula = cedula
        self.nombre = nombre
        self.correo = correo

class Alumno(Persona):
    def __init__(self, cedula, nombre, correo, tipo_programa, notas=None):
        super().__init__(cedula, nombre, correo)
        self.tipo_programa = tipo_programa
        # Máximo 3 notas por programa
        self.notas = notas if notas else [0.0, 0.0, 0.0]

class Profesor(Persona):
    def __init__(self, cedula, nombre, correo, especialidad, materia):
        super().__init__(cedula, nombre, correo)
        self.especialidad = especialidad
        self.materia = materia


# ==========================================
# JERARQUÍA DE PROGRAMAS ACADÉMICOS (Polimorfismo)
# ==========================================
class ProgramaAcademico:
    def evaluar_aprobacion(self, notas):
        pass

class Curso(ProgramaAcademico):
    def evaluar_aprobacion(self, notas):
        # Se aprueba si el promedio de las 3 notas es mayor o igual a 10/20
        promedio = sum(notas) / len(notas) if notas else 0
        return promedio >= 10, promedio

class Diplomado(ProgramaAcademico):
    def evaluar_aprobacion(self, notas):
        # Se aprueba si el promedio de las 3 notas es mayor o igual a 14/20
        promedio = sum(notas) / len(notas) if notas else 0
        return promedio >= 14, promedio

class Bootcamp(ProgramaAcademico):
    def evaluar_aprobacion(self, notas):
        # Ninguna nota individual menor a 14/20
        promedio = sum(notas) / len(notas) if notas else 0
        if not notas:
            return False, promedio
        aprobado = all(nota >= 14 for nota in notas)
        return aprobado, promedio


# ==========================================
# CLASE PRINCIPAL DEL SISTEMA (SGA-DO)
# ==========================================
class SistemaGestionAcademica:
    def __init__(self):
        self.alumnos = []
        self.profesores = []
        # Pila para deshacer el último registro de nota (LIFO)
        self.pila_historial_notas = deque()
        self.cargar_datos()

    def cargar_datos(self):
        """Carga los datos desde alumnos.txt y profesores.txt si existen."""
        if os.path.exists("alumnos.txt"):
            with open("alumnos.txt", "r", encoding="utf-8") as f:
                for linea in f:
                    linea = linea.strip()
                    if linea:
                        partes = linea.split(",")
                        if len(partes) >= 7:
                            cedula, nombre, correo, tipo = partes[0], partes[1], partes[2], partes[3]
                            notas = [float(partes[4]), float(partes[5]), float(partes[6])]
                            self.alumnos.append(Alumno(cedula, nombre, correo, tipo, notas))

        if os.path.exists("profesores.txt"):
            with open("profesores.txt", "r", encoding="utf-8") as f:
                for linea in f:
                    linea = linea.strip()
                    if linea:
                        partes = linea.split(",")
                        if len(partes) >= 5:
                            cedula, nombre, correo, esp, mat = partes[0], partes[1], partes[2], partes[3], partes[4]
                            self.profesores.append(Profesor(cedula, nombre, correo, esp, mat))

    def guardar_alumnos(self):
        """Guarda todos los alumnos en alumnos.txt."""
        with open("alumnos.txt", "w", encoding="utf-8") as f:
            for a in self.alumnos:
                linea = f"{a.cedula},{a.nombre},{a.correo},{a.tipo_programa},{a.notas[0]},{a.notas[1]},{a.notas[2]}\n"
                f.write(linea)

    def guardar_profesores(self):
        """Guarda todos los profesores en profesores.txt."""
        with open("profesores.txt", "w", encoding="utf-8") as f:
            for p in self.profesores:
                linea = f"{p.cedula},{p.nombre},{p.correo},{p.especialidad},{p.materia}\n"
                f.write(linea)

    def registrar_alumno(self):
        print("\n--- Registrar Alumno ---")
        cedula = input("Ingrese Cédula/ID: ").strip()
        nombre = input("Ingrese Nombre Completo: ").strip()
        correo = input("Ingrese Correo Electrónico: ").strip()
        print("Tipos de Programa disponibles: Curso, Diplomado, Bootcamp")
        tipo = input("Ingrese Tipo de Programa: ").strip()

        nuevo_alumno = Alumno(cedula, nombre, correo, tipo, [0.0, 0.0, 0.0])
        self.alumnos.append(nuevo_alumno)
        self.guardar_alumnos()
        print("¡Alumno registrado y guardado en alumnos.txt con éxito!")

    def registrar_profesor(self):
        print("\n--- Registrar Profesor ---")
        cedula = input("Ingrese Cédula/ID: ").strip()
        nombre = input("Ingrese Nombre Completo: ").strip()
        correo = input("Ingrese Correo Electrónico: ").strip()
        especialidad = input("Ingrese Especialidad (ej. Python, Java, C++): ").strip()
        materia = input("Ingrese Materia Asignada: ").strip()

        nuevo_profesor = Profesor(cedula, nombre, correo, especialidad, materia)
        self.profesores.append(nuevo_profesor)
        self.guardar_profesores()
        print("¡Profesor registrado y guardado en profesores.txt con éxito!")

    def registrar_notas(self):
        print("\n--- Registrar Notas a un Alumno ---")
        cedula = input("Ingrese la Cédula del alumno: ").strip()
        alumno_encontrado = None
        for a in self.alumnos:
            if a.cedula == cedula:
                alumno_encontrado = a
                break

        if not alumno_encontrado:
            print("Error: Alumno no encontrado.")
            return

        print(f"Alumno: {alumno_encontrado.nombre} | Programa: {alumno_encontrado.tipo_programa}")
        print(f"Notas actuales: {alumno_encontrado.notas}")
        
        try:
            n1 = float(input("Ingrese Nota 1: "))
            n2 = float(input("Ingrese Nota 2: "))
            n3 = float(input("Ingrese Nota 3: "))
        except ValueError:
            print("Error: Debe ingresar valores numéricos válidos.")
            return

        # Guardar estado anterior en la pila antes de modificar (para Deshacer - LIFO)
        self.pila_historial_notas.append({
            "alumno": alumno_encontrado,
            "notas_anteriores": list(alumno_encontrado.notas)
        })

        alumno_encontrado.notas = [n1, n2, n3]
        self.guardar_alumnos()
        print("¡Notas registradas con éxito! (Acción guardada en Pila)")

    def deshacer_ultimo_registro_nota(self):
        print("\n--- Deshacer Último Registro de Nota ---")
        if not self.pila_historial_notas:
            print("No hay acciones de notas para deshacer.")
            return

        ultimo_cambio = self.pila_historial_notas.pop()
        alumno = ultimo_cambio["alumno"]
        alumno.notas = ultimo_cambio["notas_anteriores"]
        self.guardar_alumnos()
        print(f"Se han revertido las notas del alumno {alumno.nombre} a su estado anterior: {alumno.notas}")

    def generar_cola_certificados(self):
        print("\n--- Generar Cola de Certificados ---")
        cola_aprobados = deque()

        for alumno in self.alumnos:
            evaluador = None
            tipo_lower = alumno.tipo_programa.lower()
            if "curso" in tipo_lower:
                evaluador = Curso()
            elif "diplomado" in tipo_lower:
                evaluador = Diplomado()
            elif "bootcamp" in tipo_lower:
                evaluador = Bootcamp()

            if evaluador:
                aprobado, promedio = evaluador.evaluar_aprobacion(alumno.notas)
                if aprobado:
                    cola_aprobados.append({
                        "alumno": alumno,
                        "promedio": promedio
                    })

        # Exportar a certificados_pendientes.txt usando lógica FIFO
        with open("certificados_pendientes.txt", "w", encoding="utf-8") as f:
            f.write("=========================================\n")
            f.write("REPORTE DE CERTIFICADOS PENDIENTES\n")
            f.write("=========================================\n")
            f.write(f"Total de graduandos en cola: {len(cola_aprobados)}\n\n")

            contador = 1
            while cola_aprobados:
                item = cola_aprobados.popleft() # FIFO (First In, First Out)
                _al = item["alumno"]
                _prom = item["promedio"]
                
                estatus_txt = "APROBADO"
                if _al.tipo_programa.lower() == "bootcamp":
                    estatus_txt = "APROBADO (Cumple regla de ninguna nota < 14)"

                f.write(f"{contador}. [{_al.cedula}] {_al.nombre}\n")
                f.write(f"   - Programa: {_al.tipo_programa}\n")
                f.write(f"   - Promedio Final: {_prom:.1f}\n")
                f.write(f"   - Estatus: {estatus_txt}\n\n")
                contador += 1

            f.write("=========================================\n")
            f.write("* Fin del reporte - Generado por SGA-DO *\n")

        print("¡Cola de certificados generada con éxito en 'certificados_pendientes.txt'!")

    def mostrar_reporte_general(self):
        print("\n" + "="*40)
        print(" REPORTE GENERAL - SGA-DO")
        print("="*40)
        
        print(f"\n--- PROFESORES ({len(self.profesores)}) ---")
        if not self.profesores:
            print("No hay profesores registrados.")
        for p in self.profesores:
            print(f"ID: {p.cedula} | Nombre: {p.nombre} | Correo: {p.correo} | Esp: {p.especialidad} | Mat: {p.materia}")

        print(f"\n--- ALUMNOS ({len(self.alumnos)}) ---")
        if not self.alumnos:
            print("No hay alumnos registrados.")
        for a in self.alumnos:
            print(f"ID: {a.cedula} | Nombre: {a.nombre} | Programa: {a.tipo_programa} | Notas: {a.notas}")
        print("="*40)

    def ejecutar(self):
        while True:
            print("\n==================================================")
            print("      SGA-DO: SISTEMA DIPLOMADOSONLINE")
            print("==================================================")
            print("1. Registrar Alumno")
            print("2. Registrar Profesor")
            print("3. Registrar Notas a un Alumno")
            print("4. Deshacer Último Registro de Nota")
            print("5. Generar Cola de Certificados")
            print("6. Mostrar Reporte General")
            print("7. Salir")
            print("==================================================")
            
            opcion = input("Seleccione una opción (1-7): ").strip()

            if opcion == "1":
                self.registrar_alumno()
            elif opcion == "2":
                self.registrar_profesor()
            elif opcion == "3":
                self.registrar_notas()
            elif opcion == "4":
                self.deshacer_ultimo_registro_nota()
            elif opcion == "5":
                self.generar_cola_certificados()
            elif opcion == "6":
                self.mostrar_reporte_general()
            elif opcion == "7":
                print("\nSaliendo del sistema. ¡Hasta luego!")
                break
            else:
                print("Opción inválida. Intente de nuevo (1-7).")

if __name__ == "__main__":
    sistema = SistemaGestionAcademica()
    sistema.ejecutar()
