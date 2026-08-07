import os

# Archivos de texto nativos para la persistencia
ARCHIVO_ALUMNOS = "alumnos.txt"
ARCHIVO_PROFESORES = "profesores.txt"

def mostrar_menu():
    print("\n==============================================")
    print("      SGA-DO: SISTEMA DIPLOMADOSONLINE        ")
    print("==============================================")
    print("1. Registrar Alumno")
    print("2. Registrar Profesor")
    print("3. Mostrar Reporte General")
    print("4. Salir")
    print("==============================================")

def registrar_alumno():
    cedula = input("Ingrese la cédula del alumno: ")
    nombre = input("Ingrese el nombre completo: ")
    correo = input("Ingrese el correo electrónico: ")
    tipo_programa = input("Ingrese el tipo de programa (Curso / Diplomado / Bootcamp): ")
    
    # Guarda directamente en el archivo alumnos.txt usando manejo nativo
    with open(ARCHIVO_ALUMNOS, "a", encoding="utf-8") as archivo:
        archivo.write(f"{cedula},{nombre},{correo},{tipo_programa},0,0,0\n")
    
    print("\n¡Alumno registrado y guardado en alumnos.txt con éxito!")

def registrar_profesor():
    cedula = input("Ingrese la cédula del profesor: ")
    nombre = input("Ingrese el nombre completo: ")
    correo = input("Ingrese el correo electrónico: ")
    especialidad = input("Ingrese la especialidad: ")
    materia = input("Ingrese la materia asignada: ")
    
    # Guarda directamente en el archivo profesores.txt usando manejo nativo
    with open(ARCHIVO_PROFESORES, "a", encoding="utf-8") as archivo:
        archivo.write(f"{cedula},{nombre},{correo},{especialidad},{materia}\n")
    
    print("\n¡Profesor registrado y guardado en profesores.txt con éxito!")

def mostrar_reporte():
    print("\n--- REPORTE DE ARCHIVOS GUARDADOS ---")
    
    print("\n[ALUMNOS]")
    if os.path.exists(ARCHIVO_ALUMNOS):
        with open(ARCHIVO_ALUMNOS, "r", encoding="utf-8") as archivo:
            contenido = archivo.read()
            print(contenido if contenido else "El archivo está vacío.")
    else:
        print("Aún no existe el archivo de alumnos.")

    print("\n[PROFESORES]")
    if os.path.exists(ARCHIVO_PROFESORES):
        with open(ARCHIVO_PROFESORES, "r", encoding="utf-8") as archivo:
            contenido = archivo.read()
            print(contenido if contenido else "El archivo está vacío.")
    else:
        print("Aún no existe el archivo de profesores.")

def main():
    while True:
        mostrar_menu()
        opcion = input("Seleccione una opción (1-4): ")
        
        if opcion == "1":
            registrar_alumno()
        elif opcion == "2":
            registrar_profesor()
        elif opcion == "3":
            mostrar_reporte()
        elif opcion == "4":
            print("\nSaliendo del sistema. ¡Hasta luego!")
            break
        else:
            print("\nOpción inválida. Intente de nuevo.")

if __name__ == "__main__":
    main()
