package com.gestionmats.app.dao;

import com.gestionmats.app.models.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // 1. Instancia estática para el Singleton
    private static ProductoDAO instancia;

    // Ruta del archivo CSV (se creará en la raíz del proyecto)
    private final String RUTA_ARCHIVO = "productos.csv";
    private final String SEPARADOR = ",";

    // 2. Constructor privado (nadie más puede hacer 'new ProductoDAO()')
    private ProductoDAO() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
            } catch (IOException e) {
                System.err.println("Error al crear el archivo CSV: " + e.getMessage());
            }
        }
    }

    // 3. Método para obtener la única instancia
    public static ProductoDAO getInstancia() {
        if (instancia == null) {
            instancia = new ProductoDAO();
        }
        return instancia;
    }

    // --- MÉTODOS TRADUCIDOS DEL CÓDIGO EN REACT ---

    public List<Producto> getAll() {
        List<Producto> productos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] datos = linea.split(SEPARADOR);
                // Mapeo del CSV al Record Producto
                Producto p = new Producto(
                        datos[0], datos[1], datos[2],
                        Double.parseDouble(datos[3]),
                        Integer.parseInt(datos[4]),
                        datos[5], datos[6],
                        Integer.parseInt(datos[7]),
                        Double.parseDouble(datos[8]),
                        datos[9], datos[10], datos[11]
                );
                productos.add(p);
            }
        } catch (IOException e) {
            System.err.println("Error al leer productos: " + e.getMessage());
        }
        return productos;
    }

    public void saveAll(List<Producto> productos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO))) {
            for (Producto p : productos) {
                String linea = String.join(SEPARADOR,
                        p.id(), p.nombre(), p.descripcion(),
                        String.valueOf(p.precio()), String.valueOf(p.stock()),
                        p.categoria(), p.unidad(), String.valueOf(p.minStock()),
                        String.valueOf(p.descuento()), p.imageUrl(),
                        p.createdAt(), p.updatedAt()
                );
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar productos: " + e.getMessage());
        }
    }

    public Producto create(Producto producto) {
        List<Producto> productos = getAll();
        productos.add(producto);
        saveAll(productos);
        return producto;
    }

    public boolean delete(String id) {
        List<Producto> productos = getAll();
        boolean removido = productos.removeIf(p -> p.id().equals(id));
        if (removido) {
            saveAll(productos);
        }
        return removido;
    }
}