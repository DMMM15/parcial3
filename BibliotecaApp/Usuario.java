import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class Usuario {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1000);
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final int id;
    private String nombre;
    private String email;
    private final List<Prestamo> prestamosActivos = new ArrayList<>();
    private BigDecimal multas = BigDecimal.ZERO;

    public static final int MAX_LIBROS = 3;
    public static final BigDecimal MAX_MULTA = BigDecimal.valueOf(5000);

    public Usuario(String nombre, String email) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.nombre = Objects.requireNonNull(nombre);
        if (!EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("Email inválido.");
        }
        this.email = email;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public List<Prestamo> getPrestamosActivos() { return Collections.unmodifiableList(prestamosActivos); }
    public BigDecimal getMultas() { return multas; }

    public synchronized boolean puedePedirPrestado() {
        return prestamosActivos.size() < MAX_LIBROS && multas.compareTo(MAX_MULTA) <= 0;
    }

    public synchronized void agregarPrestamo(Prestamo p) throws UsuarioSinCupoException {
        if (!puedePedirPrestado()) {
            throw new UsuarioSinCupoException("Usuario no puede pedir prestado (cupo o multa excedida).");
        }
        prestamosActivos.add(p);
    }

    public synchronized void marcarDevuelto(Prestamo p) {
        prestamosActivos.remove(p);
    }

    public synchronized void agregarMulta(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) return;
        multas = multas.add(monto);
        
    }

    public synchronized void pagarMultas(BigDecimal pago) {
        if (pago == null || pago.compareTo(BigDecimal.ZERO) <= 0) return;
        multas = multas.subtract(pago);
        if (multas.compareTo(BigDecimal.ZERO) < 0) multas = BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return String.format("Usuario[%d] %s <%s> | prestamosActivos=%d | multas=%s",
                id, nombre, email, prestamosActivos.size(), multas.toPlainString());
    }
}
