package gestionmats.model;

public class Producto {
    private String idProducto; // Cambiado a String como propusiste
    private String nombre;
    private String marca;
    private String categoria;
    private double precioVenta;
    private int stockMaximo;
    private int stockActual;
    private String unidadMedida;
    private double descuento;

    public String getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }
    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStockMaximo() {
        return stockMaximo;
    }
    public void setStockMaximo(int stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public int getStockActual() {
        return stockActual;
    }
    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public String getUnidadMedida() { return unidadMedida;}
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getDescuento() {
        return descuento;
    }
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public Producto(String idProducto, String nombre, String marca, String categoria,
                    double precioVenta, int stockMaximo, int stockActual,
                    String unidadMedida, double descuento) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.precioVenta = precioVenta;
        this.stockMaximo = stockMaximo;
        this.stockActual = stockActual;
        this.unidadMedida = unidadMedida;
        this.descuento = descuento;
    }


    /**
     * DATO CALCULADO: No se guarda en el CSV, se calcula cuando se necesita.
     */
    public int calcularStockMinimo() {
        return (int) (this.stockMaximo * 0.25);
    }

    /**
     * ESTADO DINÁMICO: Tampoco se guarda en el CSV.
     * Java lo evalúa instantáneamente cada vez que la tabla lo pide.
     */
    public EstadoProducto getEstado() {
        int minimo = calcularStockMinimo();
        int nivelCritico = (int) (this.stockMaximo * 0.10);

        if (this.stockActual == stockMaximo) {
            return EstadoProducto.LLENO;
        } else if(this.stockActual > minimo){
            return EstadoProducto.OK;
        } else if (this.stockActual > nivelCritico) {
            return EstadoProducto.BAJO;
        } else {
            return EstadoProducto.CRITICO;
        }
    }

    //TODO: ActualizarStock y VerificarAlertStock
}