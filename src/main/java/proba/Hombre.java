package proba;

public class Hombre {
    private String nombre;
    private int edad;

    public Hombre(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

    public int getEdad() {
        return edad;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre + ", " + edad + " éves.";
    }
}
