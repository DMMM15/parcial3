import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Biblioteca {
    private final Map<String, Libro> librosByIsbn = new HashMap<>();
    private final Map<Integer, Usuario> usuariosById = new HashMap<>();
    private final Map<Integer, Prestamo> prestamosById = new HashMap<>();

    // Sincronizamos los métodos que modifican el estado compartido
    public synchronized void agregarLibro(Libro libro) {
        librosByIsbn.compute(libro.getIsbn(), (k, existente) -> {
            if (existente == null) {
                return libro;
            } else {
                existente.aumentarEjemplares(libro.getEjemplaresTotales());
                return existente;
            }
        });
    }

    public synchronized Optional<Libro> buscarLibroPorIsbn(String isbn) {
        return Optional.ofNullable(librosByIsbn.get(isbn));
    }

    public List<Libro> buscarLibrosPorTitulo(String fragmento) {
        String f = fragmento.toLowerCase();
        return librosByIsbn.values().stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(f))
                .collect(Collectors.toList());
    }

    public synchronized void registrarUsuario(Usuario u) {
        usuariosById.put(u.getId(), u);
    }

    public Optional<Usuario> obtenerUsuario(int id) {
        return Optional.ofNullable(usuariosById.get(id));
    }

  public synchronized Prestamo realizarPrestamo(int usuarioId, String isbn, java.time.LocalDate fechaLimite) throws Exception {
    Usuario usuario = usuariosById.get(usuarioId);
    if (usuario == null) throw new IllegalArgumentException("Usuario no encontrado: " + usuarioId);
    Libro libro = librosByIsbn.get(isbn);
    if (libro == null) throw new IllegalArgumentException("Libro no encontrado: " + isbn);

    if (!usuario.puedePedirPrestado()) {
        throw new UsuarioSinCupoException("Usuario no puede pedir prestado (cupo o multa).");
    }
    if (!libro.estaDisponible()) {
        throw new LibroNoDisponibleException("Libro no disponible: " + isbn);
    }

    libro.prestar();
    Prestamo p = new Prestamo(usuario, libro, fechaLimite);
    prestamosById.put(p.getId(), p);
    usuario.agregarPrestamo(p);
    return p;
}


    public synchronized BigDecimal devolverLibro(int prestamoId) throws Exception {
        Prestamo p = prestamosById.get(prestamoId);
        if (p == null) throw new IllegalArgumentException("Prestamo no existe: " + prestamoId);
        if (p.getEstado() == EstadoPrestamo.DEVUELTO) return BigDecimal.ZERO;

        BigDecimal multa = p.devolver();
        Libro libro = p.getLibro();
        libro.devolver();
        Usuario usuario = p.getUsuario();
        usuario.marcarDevuelto(p);
        if (multa.compareTo(BigDecimal.ZERO) > 0) {
            usuario.agregarMulta(multa);
        }
        return multa;
    }

    public List<Usuario> obtenerUsuariosConMultas() {
        return usuariosById.values().stream()
                .filter(u -> u.getMultas().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(Usuario::getMultas).reversed())
                .collect(Collectors.toList());
    }

    public List<Libro> obtenerTopLibrosPrestados(int topN) {
        return librosByIsbn.values().stream()
                .sorted(Comparator.comparingInt(Libro::getVecesPrestado).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    public List<Prestamo> obtenerPrestamosUsuario(int usuarioId) {
        return prestamosById.values().stream()
                .filter(p -> p.getUsuario().getId() == usuarioId)
                .sorted(Comparator.comparing(Prestamo::getFechaPrestamo).reversed())
                .collect(Collectors.toList());
    }

    public List<Libro> listarLibrosDisponibles() {
        return librosByIsbn.values().stream()
                .filter(Libro::estaDisponible)
                .collect(Collectors.toList());
    }

    public Optional<Prestamo> obtenerPrestamo(int id) {
        return Optional.ofNullable(prestamosById.get(id));
    }
}
