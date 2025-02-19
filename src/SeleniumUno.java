import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SeleniumUno {
    private static final String URL = "https://amazondating.co/";

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\Desarrollo\\Librerias\\Selenium-driver\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        List<Soltero> lstSolteros;
        List<Soltero> lstFiltradaOrdenada = new ArrayList<>();

        try{
            lstSolteros = obtenerListaDeWeb(driver);
            Thread.sleep(5000);

            if (!lstSolteros.isEmpty()) {
                int edadMin = preguntarMinEdad();
                int edadMax = preguntarMaxEdad();
                double precioMax = preguntarPrecioMax();

                lstFiltradaOrdenada = filtrarLista(edadMin, edadMax, precioMax, lstSolteros);
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            driver.quit();
        }
    }

    private static List<Soltero> filtrarLista(int edadMin, int edadMax, double precioMax, List<Soltero> lstSolteros) {
        List<Soltero> lstSolterosFiltro = new ArrayList<>();
        for (Soltero soltero : lstSolteros) {
            if (soltero.getEdad() > edadMin && soltero.getEdad() < edadMax) {
                if (soltero.getPrecio() < precioMax && soltero.getPrecio() != 0.0) {
                    lstSolterosFiltro.add(soltero);
                }
            }
        }

        Collections.sort(lstSolterosFiltro);

        for (Soltero soltero : lstSolterosFiltro) {
            System.out.println(soltero.toString());
        }
        return lstSolterosFiltro;
    }


    private static List<Soltero> obtenerListaDeWeb(WebDriver driver) {
        // Variables para crear el nuevo objeto Soltero
        String nombre;
        int edad;
        Double precioConvertido;
        String puntuacionObtenida;

        // Variables auxiliares para convertir los datos obtenidos
        String[] precio;
        String producto;
        String precioFinal;
        String[] nombreEdadArray;

        // Lista donde añadiremos los solteros
        List<Soltero> lstSolteros = new ArrayList<>();

        driver.get(URL);

        List<WebElement> nombresEdad = driver.findElements(By.className("product-name"));
        List<WebElement> productos = driver.findElements(By.className("product-price"));
        List<WebElement> puntuaciones = driver.findElements(By.className("stars"));


        for (int i = 0; i < nombresEdad.size(); i++) {
            puntuacionObtenida = puntuaciones.get(i).getDomAttribute("class");
            nombreEdadArray = nombresEdad.get(i).getText().split(", ");
            producto = productos.get(i).getText();
            precio = producto.split(" ");
            if (precio[0].contains("FREE")) {
                precioFinal = precio[0].replace("FREE", "");
            } else {
                precioFinal = precio[0];
            }

            nombre = nombreEdadArray[0];
            edad = Integer.parseInt(nombreEdadArray[1]);
            precioConvertido = convertirPrecio(precioFinal);

            Double puntuacionDouble = convertirPuntuacion(puntuacionObtenida);

            lstSolteros.add(new Soltero(nombre, precioConvertido, edad, puntuacionDouble));
        }



        return lstSolteros;
    }

    private static Double convertirPuntuacion(String puntuacion) {
        String[] puntuacionSplit = puntuacion.split("-");
        double puntuacionDouble = 0.0;

        for (int i = 0; i < puntuacionSplit.length; i++) {
            if (puntuacionSplit.length == 3) {
                puntuacionDouble = Double.parseDouble(puntuacionSplit[1] + "." + puntuacionSplit[2]);
            } else if (puntuacionSplit.length == 2) {
                puntuacionDouble = Double.parseDouble(puntuacionSplit[1]);
            }
        }
        return puntuacionDouble;
    }

    private static Double convertirPrecio(String precio){
        try{
            return Double.parseDouble(precio.substring(1, precio.length() - 1));
        }catch (Exception e){
            return 0.0;
        }
    }

    private static Integer preguntarMaxEdad() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduzca el máximo de edad: ");
        int edadMax = sc.nextInt();
        return edadMax;
    }

    private static Integer preguntarMinEdad() {
        Scanner sc = new Scanner(System.in);
        int edadMin;
        do {
            System.out.print("Introduzca el mínimo de edad: ");
            edadMin = sc.nextInt();
        } while (edadMin < 18);
        return edadMin;
    }

    private static Double preguntarPrecioMax() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduzca el precio máximo: ");
        String precioMax = sc.nextLine();
        if(precioMax.contains(".")){
            return Double.parseDouble(precioMax);
        }else{
            return Double.parseDouble(precioMax.replace(",", "."));
        }
    }
}