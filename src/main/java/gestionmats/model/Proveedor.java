/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestionmats.model;

/**
 *
 * @author Admin
 */
public class Proveedor {
    private int idProveedor;
    private String nombreProveedor;
    private String numeroTelefonico;
    
    public Proveedor() {
    }

    public Proveedor(int idProveedor, String nombreProveedor, String numeroTelefonico) {
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
        this.numeroTelefonico = numeroTelefonico;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public String getNumeroTelefonico() {
        return numeroTelefonico;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public void setNumeroTelefonico(String numeroTelefonico) {
        this.numeroTelefonico = numeroTelefonico;
    }
    
    
}
