public class Soltero implements Comparable<Soltero> {

    private String nombre;
    private Double precio;
    private int edad;
    private Double puntuacion;

    public Soltero(String nombre, Double precio, int edad, Double puntuacion) {
        setNombre(nombre);
        setPrecio(precio);
        setEdad(edad);
        setPuntuacion(puntuacion);
    }

    public Double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Double puntuacion) {
        this.puntuacion = puntuacion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public int getEdad() {
        return edad;
    }

    @Override
    public String toString() {
        return "Soltero: " + "nombre: " + nombre + ", precio: " + precio + ", edad: " + edad + ", puntuacion: " + puntuacion + '}';
    }

    @Override
    public int compareTo(Soltero o) {
        if (this.puntuacion > o.getPuntuacion()) {
            return -1;
        }
        if (this.puntuacion < o.getPuntuacion()) {
            return 1;
        }
        return 0;
    }
}
