package gestionmats.model;

public abstract class AbstractUsuario {
    private int idUsuario;
    private String primerApellido;
    private String segundoApellido;
    private String nombre;
    private String username;
    private String password;
    private RolUsuario rol; // Usamos el Enum aquí

    public AbstractUsuario(int idUsuario, String primerApellido, String segundoApellido, String nombre, String username, String password, RolUsuario rol) {
        this.idUsuario = idUsuario;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    // Aquí irían todos tus Getters y Setters...

    // Un método abstracto opcional si quieres polimorfismo puro
    public abstract void mostrarMenuPrincipal();
}
