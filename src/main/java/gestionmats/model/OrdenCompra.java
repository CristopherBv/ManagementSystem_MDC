/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestionmats.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Admin
 */
public class OrdenCompra {
    private int idOrden;
    private Date fechaEmision;
    private String estado;

    //Campos añadidos por las relaciones del diagrama
    //Relación de asociación '1' hacia Proveedor
    private Proveedor proveedor;
    //Relación de composición '1..*' hacia DetalleOrden
    private List<DetalleOrden> detalles;

    //Constructor público
    public OrdenCompra() {
        //Inicializamos la lista para cumplir con la multiplicidad
        this.detalles = new ArrayList<>();
        //El estado inicial podría ser "EMITIDA"
        this.estado = "EMITIDA";
        //La fecha de emisión se puede establecer en la creación
        this.fechaEmision = new Date();
    }

    //Constructor completo para conveniencia
    public OrdenCompra(int idOrden, Proveedor proveedor) {
        this(); //Llama al constructor por defecto para inicializaciones básicas
        this.idOrden = idOrden;
        this.proveedor = proveedor;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public String getEstado() {
        return estado;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public List<DetalleOrden> getDetalles() {
        return detalles;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public void recibirMercancia() {
        
    }

    
    //Método auxiliar para añadir detalles de orden, cumpliendo con la relación de composición.
    public void addDetalleOrden(DetalleOrden detalle) {
        if (detalle != null) {
            this.detalles.add(detalle);
        }
    }

    
}
