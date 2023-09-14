package app.models;

public class Person {
    public String nombre;
    public String apellido;
    public int edad;
    public String telefono;
    public String ciudad;
    public String estado_civil;

    public Person() {
    }

    public Person(String nombre, String apellido, int edad, String telefono, String ciudad, String estado_civil) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.estado_civil = estado_civil;
    }

}
