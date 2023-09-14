package app;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;

import app.exceptions.NotEqualTypesException;
import app.exceptions.NotFoundAttributeExceptions;
import app.models.Person;
import app.services.DB;

public class app {
    public static void main(String[] args) {
        DB<Person> db = new DB<>("prueba", "|");

        // Person p1 = new Person("Juan", "Esquives", 20, "123", "Lima", "casado");
        // Person p2 = new Person("Jesus", "Zapata", 30, "123", "Lima", "soltero");
        // // Person p3 = new Person("Mariano", "Rosales", 40, "123", "Lima",
        // "soltero");

        // db.addData(p1);
        // db.addData(p2);
        // // db.push(p3);

        ArrayList<Person> data;
        try {
            data = db.getData(Person.class);
            for (Person person : data) {
                System.out.printf("Hola %s %s, tienes %d anios%n", person.nombre,
                        person.apellido, person.edad);
            }
        } catch (IOException | ReflectiveOperationException e) {
            System.out.println("Ocurrio un error: " + e.getMessage());
        }

        // try {
        // db.determineGenericType();
        // } catch (NoSuchMethodException | SecurityException e) {
        // e.printStackTrace();
        // }

        // try {
        // db.find("firstname", "Juan", Person.class).forEach((persona) -> {
        // System.out.println("------------Persona------------");
        // System.out.println("nombre: " + persona.nombre);
        // System.out.println("apellido: " + persona.apellido);
        // System.out.println("edad: " + persona.edad);
        // System.out.println("telefono: " + persona.telefono);
        // System.out.println("ciudad: " + persona.ciudad);
        // System.out.println("estado civil: " + persona.estado_civil);
        // });
        // } catch (IOException | ReflectiveOperationException ioex) {
        // System.out.println("error");
        // } catch (NotEqualTypesException e) {
        // e.printStackTrace();
        // } catch (NotFoundAttributeExceptions e) {
        // e.printStackTrace();
        // }

    }
}
