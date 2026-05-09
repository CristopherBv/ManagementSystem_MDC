/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestionmats.model;

/**
 *
 * @author Admin
 */
public class DetalleOrden {
    // Campos privados marcados con '-' en el diagrama
    private int cantidadEsperada;
    private int cantidadRecibida;

    // Constructor público
    public DetalleOrden() {
    }

    // Constructor completo para conveniencia
    public DetalleOrden(int cantidadEsperada, int cantidadRecibida) {
        this.cantidadEsperada = cantidadEsperada;
        this.cantidadRecibida = cantidadRecibida;
    }

    // Métodos getter públicos (implícitos) para acceder a los datos
    public int getCantidadEsperada() {
        return cantidadEsperada;
    }

    public int getCantidadRecibida() {
        return cantidadRecibida;
    }

    // Métodos setter públicos (implícitos) para modificar datos.
    // Especialmente útil para actualizar la cantidad recibida.
    public void setCantidadEsperada(int cantidadEsperada) {
        this.cantidadEsperada = cantidadEsperada;
    }

    public void setCantidadRecibida(int cantidadRecibida) {
        this.cantidadRecibida = cantidadRecibida;
    }

    
}
