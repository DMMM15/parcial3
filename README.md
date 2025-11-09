#  Sistema de Gestión de Biblioteca — Parcial 3

##  Objetivo del proyecto
Desarrollar un sistema de gestión de biblioteca que permita administrar libros, usuarios y préstamos, aplicando los conceptos de:
- Programación Orientada a Objetos (POO)
- Manejo de excepciones personalizadas
- Validaciones y reglas de negocio
- Uso de colecciones 
- Streams y Optional
- Tipos primitivos vs clases wrapper
- Concurrencia 

##  Concepto general
La biblioteca necesita modernizar su control de préstamos. El sistema permite:
- Registrar libros y controlar ejemplares disponibles.
- Registrar usuarios con control de cupos y multas.
- Realizar y devolver préstamos.
- Calcular multas por retraso.
- Generar reportes de los libros más prestados y usuarios con multas.

##  Estructura del proyecto
BibliotecaApp/
│
├── BibliotecaApp.java  
├── Biblioteca.java  
├── Libro.java  
├── LibroNoDisponibleException.java  
├── Usuario.java  
├── UsuarioSinCupoException.java  
├── Prestamo.java  
├── EstadoPrestamo.java  
└── README.md

##  Clases principales

### Libro
Representa un libro en la biblioteca.  
**Atributos:** ISBN, título, autor, año, ejemplares totales y disponibles.  
**Métodos principales:** prestar(), devolver(), estaDisponible().  
Valida ISBN (13 dígitos) y año correcto. Lanza LibroNoDisponibleException.

### Usuario
Representa a un usuario registrado.  
**Atributos:** ID autogenerado, nombre, email, libros prestados, multas acumuladas.  
**Reglas:** máximo 3 libros prestados, multa máxima $5000.  
**Métodos principales:** puedePedirPrestado(), agregarMulta(), pagarMultas().  
Lanza UsuarioSinCupoException.

### Prestamo
Controla la información de cada préstamo.  
**Atributos:** ID, usuario, libro, fechas de préstamo y devolución, fecha límite y estado.  
Permite ingresar una fecha límite personalizada.  
Calcula la multa automáticamente según el retraso ($500/día).  
Estados posibles: ACTIVO, DEVUELTO, VENCIDO.

### Biblioteca
Gestor principal del sistema.  
Usa HashMap y ArrayList para almacenar libros, usuarios y préstamos.  
**Funciones principales:** agregarLibro(), registrarUsuario(), realizarPrestamo(), devolverLibro(), obtenerTopLibrosPrestados(), obtenerUsuariosConMultas().

### BibliotecaApp
Clase principal con menú interactivo por consola.  
**Opciones del menú:**
1. Agregar libro  
2. Registrar usuario  
3. Realizar préstamo (permite digitar fecha límite)  
4. Devolver libro  
5. Consultar libros disponibles  
6. Consultar préstamos de usuario  
7. Listar usuarios con multas  
8. Top 5 libros más prestados  
9. Salir  

##  Ejemplo de uso

### Crear libro
ISBN (13 dígitos): 9780307474728  
Título: Cien años de soledad  
Autor: Gabriel García Márquez  
Año: 1967  
Ejemplares: 4  

### Registrar usuario
Nombre: Juan Pérez  
Email: juan@example.com  

### Realizar préstamo
ID usuario: 1002  
ISBN libro: 9780307474728  
Fecha límite (YYYY-MM-DD): 2025-11-25  
✅ Préstamo realizado con ID: 3. Límite: 2025-11-25  

### Devolver libro
ID de préstamo: 3  
Libro devuelto. Multa calculada: $1000  

## ⚙️ Ejecución

### Compilación
javac *.java

### Ejecución
java BibliotecaApp

##  Excepciones personalizadas
- LibroNoDisponibleException → cuando no hay ejemplares disponibles.  
- UsuarioSinCupoException → cuando el usuario supera el límite de préstamos o multas.

##  Validaciones y reglas
- ISBN de 13 dígitos.  
- Año del libro válido (entre 1500 y año actual).  
- Email del usuario con formato válido.  
- Fecha límite posterior al día actual.  
- Máximo 3 libros prestados por usuario.  
- Multa máxima acumulada: $5000.

