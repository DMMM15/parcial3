import java.util.Objects;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicInteger;

public class Libro {
    private final String isbn; // 13 dígitos
    private String titulo;
    private String autor;
    private int año;
    private final AtomicInteger ejemplaresTotales = new AtomicInteger(0);
    private final AtomicInteger ejemplaresDisponibles = new AtomicInteger(0);
    private final AtomicInteger vecesPrestado = new AtomicInteger(0);

    private static final Pattern ISBN13 = Pattern.compile("\\d{13}");

    public Libro(String isbn, String titulo, String autor, int año, int ejemplares) {
        if (!ISBN13.matcher(isbn).matches()) {
            throw new IllegalArgumentException("ISBN inválido: debe tener 13 dígitos.");
        }
        int añoActual = java.time.Year.now().getValue();
        if (año < 1500 || año > añoActual) {
            throw new IllegalArgumentException("Año inválido.");
        }
        if (ejemplares <= 0) {
            throw new IllegalArgumentException("Debe haber al menos 1 ejemplar.");
        }
        this.isbn = isbn;
        this.titulo = Objects.requireNonNull(titulo);
        this.autor = Objects.requireNonNull(autor);
        this.año = año;
        this.ejemplaresTotales.set(ejemplares);
        this.ejemplaresDisponibles.set(ejemplares);
    }

    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAño() { return año; }
    public int getEjemplaresTotales() { return ejemplaresTotales.get(); }
    public int getEjemplaresDisponibles() { return ejemplaresDisponibles.get(); }
    public int getVecesPrestado() { return vecesPrestado.get(); }

    public synchronized void prestar() throws LibroNoDisponibleException {
        if (ejemplaresDisponibles.get() <= 0) {
            throw new LibroNoDisponibleException("No hay ejemplares disponibles para ISBN: " + isbn);
        }
        ejemplaresDisponibles.decrementAndGet();
        vecesPrestado.incrementAndGet();
    }

    public synchronized void devolver() {
        // No permitir exceder el total
        if (ejemplaresDisponibles.get() < ejemplaresTotales.get()) {
            ejemplaresDisponibles.incrementAndGet();
        }
    }

    public boolean estaDisponible() {
        return ejemplaresDisponibles.get() > 0;
    }

    public void aumentarEjemplares(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser > 0.");
        ejemplaresTotales.addAndGet(cantidad);
        ejemplaresDisponibles.addAndGet(cantidad);
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %d | tot: %d disp: %d prestado:%d",
                isbn, titulo, autor, año, ejemplaresTotales.get(), ejemplaresDisponibles.get(), vecesPrestado.get());
    }
}
