import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BibliotecaApp {
    private final Biblioteca biblioteca = new Biblioteca();
    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        BibliotecaApp app = new BibliotecaApp();
        app.seedDatos(); // opcional: datos para probar
        app.run();
    }

    private void run() {
        while (true) {
            mostrarMenu();
            int opc;
            try {
                opc = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Opción inválida.");
                continue;
            }
            try {
                switch (opc) {
                    case 1 -> agregarLibroUI();
                    case 2 -> registrarUsuarioUI();
                    case 3 -> realizarPrestamoUI();
                    case 4 -> devolverLibroUI();
                    case 5 -> consultarLibrosDisponiblesUI();
                    case 6 -> consultarPrestamosUsuarioUI();
                    case 7 -> listarUsuariosConMultasUI();
                    case 8 -> top5LibrosUI();
                    case 9 -> { System.out.println("Saliendo..."); return; }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("\n--- Biblioteca ---");
        System.out.println("1. Agregar libro");
        System.out.println("2. Registrar usuario");
        System.out.println("3. Realizar préstamo");
        System.out.println("4. Devolver libro");
        System.out.println("5. Consultar libros disponibles");
        System.out.println("6. Consultar préstamos de usuario");
        System.out.println("7. Listar usuarios con multas");
        System.out.println("8. Top 5 libros más prestados");
        System.out.println("9. Salir");
        System.out.print("Elige una opción: ");
    }

    private void agregarLibroUI() {
        System.out.print("ISBN (13 dígitos): ");
        String isbn = sc.nextLine().trim();
        System.out.print("Título: ");
        String titulo = sc.nextLine().trim();
        System.out.print("Autor: ");
        String autor = sc.nextLine().trim();
        System.out.print("Año: ");
        int año = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Ejemplares: ");
        int ejemplares = Integer.parseInt(sc.nextLine().trim());
        Libro libro = new Libro(isbn, titulo, autor, año, ejemplares);
        biblioteca.agregarLibro(libro);
        System.out.println("Libro agregado/actualizado.");
    }

    private void registrarUsuarioUI() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        Usuario u = new Usuario(nombre, email);
        biblioteca.registrarUsuario(u);
        System.out.println("Usuario registrado con ID: " + u.getId());
    }

private void realizarPrestamoUI() {
    System.out.print("ID usuario: ");
    int uid = Integer.parseInt(sc.nextLine().trim());
    System.out.print("ISBN libro: ");
    String isbn = sc.nextLine().trim();
    System.out.print("Fecha límite (YYYY-MM-DD): ");
    String fechaStr = sc.nextLine().trim();

    try {
        java.time.LocalDate fechaLimite = java.time.LocalDate.parse(fechaStr);
        java.time.LocalDate hoy = java.time.LocalDate.now();

        // Validar que la fecha sea posterior a hoy
        if (!fechaLimite.isAfter(hoy)) {
            System.out.println(" La fecha límite debe ser posterior a hoy.");
            return;
        }

        Prestamo p = biblioteca.realizarPrestamo(uid, isbn, fechaLimite);
        System.out.println(" Préstamo realizado con ID: " + p.getId() + ". Límite: " + p.getFechaLimite());

    } catch (java.time.format.DateTimeParseException e) {
        System.out.println(" Formato de fecha inválido. Usa YYYY-MM-DD (por ejemplo: 2025-11-25).");
    } catch (Exception e) {
        System.out.println(" No se pudo realizar préstamo: " + e.getMessage());
    }
}


    private void devolverLibroUI() {
        System.out.print("ID de préstamo: ");
        int pid = Integer.parseInt(sc.nextLine().trim());
        try {
            BigDecimal multa = biblioteca.devolverLibro(pid);
            if (multa.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Libro devuelto. Multa calculada: $" + multa.toPlainString());
            } else {
                System.out.println("Libro devuelto. Sin multa.");
            }
        } catch (Exception e) {
            System.out.println("Error al devolver: " + e.getMessage());
        }
    }

    private void consultarLibrosDisponiblesUI() {
        List<Libro> disponibles = biblioteca.listarLibrosDisponibles();
        if (disponibles.isEmpty()) {
            System.out.println("No hay libros disponibles.");
        } else {
            disponibles.forEach(System.out::println);
        }
    }

    private void consultarPrestamosUsuarioUI() {
        System.out.print("ID usuario: ");
        int uid = Integer.parseInt(sc.nextLine().trim());
        List<Prestamo> prestamos = biblioteca.obtenerPrestamosUsuario(uid);
        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos para este usuario.");
        } else {
            prestamos.forEach(System.out::println);
        }
    }

    private void listarUsuariosConMultasUI() {
        List<Usuario> conMultas = biblioteca.obtenerUsuariosConMultas();
        if (conMultas.isEmpty()) {
            System.out.println("No hay usuarios con multas.");
        } else {
            conMultas.forEach(System.out::println);
        }
    }

    private void top5LibrosUI() {
        List<Libro> top = biblioteca.obtenerTopLibrosPrestados(5);
        if (top.isEmpty()) {
            System.out.println("No hay datos de préstamos aún.");
        } else {
            int i = 1;
            for (Libro l : top) {
                System.out.println(i++ + ". " + l);
            }
        }
    }

    private void seedDatos() {
        // Datos de ejemplo para probar rápidamente
        try {
            Biblioteca b = this.biblioteca;
            b.agregarLibro(new Libro("9780306406157", "El Principito", "Antoine de Saint-Exupéry", 1943, 3));
            b.agregarLibro(new Libro("9780140449136", "Don Quijote", "Miguel de Cervantes", 1605, 2));
            Usuario u1 = new Usuario("María Pérez", "maria@example.com");
            Usuario u2 = new Usuario("Juan López", "juan@example.com");
            b.registrarUsuario(u1);
            b.registrarUsuario(u2);
            // Simular un préstamo
            Prestamo p = b.realizarPrestamo(u1.getId(), "9780306406157", java.time.LocalDate.now().plusDays(14));
            System.out.println("Seed: préstamo creado ID " + p.getId());
        } catch (Exception e) {
        }
    }
}
