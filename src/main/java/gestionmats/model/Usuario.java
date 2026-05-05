package gestionmats.model;

import java.util.Objects;

public abstract class Usuario {
    private int idUsuario;
    private String primerApellido;
    private String segundoApellido;
    private String nombre;
    private String username;
    private String password;
    private RolUsuario rol; // Usamos el ENUM aquí

    public Usuario(int idUsuario, String primerApellido, String segundoApellido, String nombre, String username, String password, RolUsuario rol) {
        this.idUsuario = idUsuario;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }
    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }


    public String getSegundoApellido() {
        return segundoApellido;
    }
    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public RolUsuario getRol() {
        return rol;
    }
    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        // Consideramos que dos usuarios son el mismo si comparten el mismo ID o Username
        return idUsuario == usuario.idUsuario || Objects.equals(username, usuario.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, username);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + idUsuario +
                ", nombre='" + nombre + " " + primerApellido + '\'' +
                ", username='" + username + '\'' +
                ", rol=" + rol +
                '}';
    }

}
