import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Prestamo {
    private static final AtomicInteger ID_GEN = new AtomicInteger(1);
    private final int id;
    private final Usuario usuario;
    private final Libro libro;
    private final LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion; // cuando se devuelve
    private final LocalDate fechaLimite;
    private EstadoPrestamo estado;

    public static final BigDecimal COSTO_POR_DIA = BigDecimal.valueOf(500);
    public static final int DIAS_PRESTAMO = 14;

    public Prestamo(Usuario usuario, Libro libro, LocalDate fechaLimite) {
    this.id = ID_GEN.getAndIncrement();
    this.usuario = Objects.requireNonNull(usuario);
    this.libro = Objects.requireNonNull(libro);
    this.fechaPrestamo = LocalDate.now();
    this.fechaLimite = Objects.requireNonNull(fechaLimite);
    this.estado = EstadoPrestamo.ACTIVO;
}

    public int getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Libro getLibro() { return libro; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaLimite() { return fechaLimite; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public EstadoPrestamo getEstado() { return estado; }

    public synchronized BigDecimal calcularMultaSiCorresponde(LocalDate devolución) {
        LocalDate referencia = (devolución != null) ? devolución : LocalDate.now();
        if (!referencia.isAfter(fechaLimite)) return BigDecimal.ZERO;
        long diasRetraso = ChronoUnit.DAYS.between(fechaLimite, referencia);
        return COSTO_POR_DIA.multiply(BigDecimal.valueOf(diasRetraso));
    }

    public synchronized BigDecimal devolver() {
        if (estado == EstadoPrestamo.DEVUELTO) return BigDecimal.ZERO;
        this.fechaDevolucion = LocalDate.now();
        BigDecimal multa = calcularMultaSiCorresponde(fechaDevolucion);
        if (multa.compareTo(BigDecimal.ZERO) > 0) {
            estado = EstadoPrestamo.VENCIDO;
        } else {
            estado = EstadoPrestamo.DEVUELTO;
        }
        return multa;
    }

    public synchronized void marcarVencidoSiCorresponde() {
        if (estado == EstadoPrestamo.ACTIVO && LocalDate.now().isAfter(fechaLimite)) {
            estado = EstadoPrestamo.VENCIDO;
        }
    }

    @Override
    public String toString() {
        return String.format("Prestamo[%d] %s -> %s | inicio=%s limite=%s estado=%s",
                id, usuario.getNombre(), libro.getTitulo(), fechaPrestamo, fechaLimite, estado);
    }
}
