import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SeleniumUno {
    private static final String URL = "https://amazondating.co/"; // url de la página

    public static void main(String[] args) {
        // Le decimos la ubicacíón del driver
        System.setProperty("webdriver.chrome.driver", "C:\\Desarrollo\\Librerias\\Selenium-driver\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();  // Inicializacion del driver
        List<Soltero> lstSolteros;              // Lista para los solteros de la página
        List<Soltero> lstFiltradaOrdenada = new ArrayList<>(); // Lista filtrada y ordenada
        String solteroSeleccionadoString = ""; // url del soltero seleccionado

        try{
            lstSolteros = obtenerListaDeWeb(driver);  // Obtenemos la lista de solteros
            hacerCaptura("Paso_1_EntrarPag");// Hacemos una captura tras entrar en la pagina
            Thread.sleep(5000);                 // Espera de 5s

            if (!lstSolteros.isEmpty()) {
                // Preguntar por edad minima, máxima y precio máximo
                int edadMin = 60; //preguntarMinEdad();
                int edadMax = 80; //preguntarMaxEdad();
                double precioMax = 1200; //preguntarPrecioMax();

                // Filtrar la lista para que nos de los solteros que entren dentro de los parámetros establecidos
                lstFiltradaOrdenada = filtrarLista(edadMin, edadMax, precioMax, lstSolteros);
            }

            // Obtenemos la url del primer soltero de la lista
            solteroSeleccionadoString = obtenerUrlSoltero(getPrimero(lstFiltradaOrdenada), driver);

            // Función para hacer el pedido del soltero
            hacerPedido(solteroSeleccionadoString, driver);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            driver.quit();
        }
    }

    // Filtramos la lista y nos quedamos con los solteros que entren dentro de esos parámetros
    private static List<Soltero> filtrarLista(int edadMin, int edadMax, double precioMax, List<Soltero> lstSolteros) {
        List<Soltero> lstSolterosFiltro = new ArrayList<>();
        for (Soltero soltero : lstSolteros) {
            if (soltero.getEdad() > edadMin && soltero.getEdad() < edadMax) {
                if (soltero.getPrecio() < precioMax && soltero.getPrecio() != 0.0) {
                    lstSolterosFiltro.add(soltero);
                }
            }
        }

        // Ordenamos la lista dependiendo de lo que le hayamos establecido en la clase, en este caso las estrellas
        Collections.sort(lstSolterosFiltro);

        // Imprimir la lista
//        for (Soltero soltero : lstSolterosFiltro) {
//            System.out.println(soltero.toString());
//        }
        return lstSolterosFiltro;
    }

    // Función para hacer el pedido mediante la url del soltero
    private static void hacerPedido(String urlSoltero, WebDriver driver) {
        try{
            driver.get(urlSoltero); // Entramos en la url del soltero
            Thread.sleep(2000);
            hacerCaptura("Paso_2_URL_Soltero_Seleccionado");

            // Seleccionar la opción de la altura
            WebElement dropAltura = driver.findElement(By.id("select-demeanor"));
            Select sel = new Select(dropAltura);
            sel.selectByValue("7’7”");
            Thread.sleep(2000);
            hacerCaptura("Paso_3_Seleccionar_Altura");

            // Seleccionar el lenguaje de amor del soltero
            List<WebElement> loveLanguage = driver.findElements(By.className("interest-button"));
            loveLanguage.get(3).click();
            Thread.sleep(2000);
            hacerCaptura("Paso_4_Seleccionar_LoveLanguage");

            // Pulsar el btn de comprar ahora
            WebElement btnComprar = driver.findElement(By.className("buy-now"));
            btnComprar.click();
            Thread.sleep(2000);
            hacerCaptura("Paso_5_Seleccionar_Comprar");

            // Pulsar el btn de finalizar la compra
            WebElement btnCompraFinal = driver.findElement(By.xpath("/html/body/div[2]/div/div/div/div/div[2]/button"));
            btnCompraFinal.click();
            Thread.sleep(2000);
            hacerCaptura("Paso_6_Aceptar_Compra_Final");

            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println("Error compra: " + e.getMessage());
        }
    }

    // Funcion para obtener la lista con los solteros
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

        driver.get(URL); // Entramos en la pagina

        // Lista de los elementos que necesitamos para crear los objetos "Soltero"
        List<WebElement> nombresEdad = recuperarListByName("product-name", driver);
        List<WebElement> productos = recuperarListByName("product-price", driver);
        List<WebElement> puntuaciones = recuperarListByName("stars", driver);

        // Extraemos la información de cada soltero
        for (int i = 0; i < nombresEdad.size(); i++) {
            puntuacionObtenida = puntuaciones.get(i).getDomAttribute("class"); // Obtenemos las estrellas
            nombreEdadArray = nombresEdad.get(i).getText().split(", "); // El nombre y edad lo separamos
            producto = productos.get(i).getText();  // Obtenemos el precio del soltero
            precio = producto.split(" "); // Separamos el precio por espacios
            if (precio[0].contains("FREE")) { // Si contiene free el string
                precioFinal = precio[0].replace("FREE", ""); // Quitamos el Free del string
            } else { // Sino
                precioFinal = precio[0]; // Se queda el string tal y como está
            }

            nombre = nombreEdadArray[0]; // El nombre es la primera posicion del array
            edad = Integer.parseInt(nombreEdadArray[1]); // La edad es la segunda posición del array
            precioConvertido = convertirPrecio(precioFinal); // El precio lo convertimos mediante una funcion

            Double puntuacionDouble = convertirPuntuacion(puntuacionObtenida); // Convertimos la puntuacion obtenida

            lstSolteros.add(new Soltero(nombre, precioConvertido, edad, puntuacionDouble)); // Añadimos un soltero nuevo a la lista
        }
        return lstSolteros;
    }

    // Función para recuperar una lista de webelements a partir de una class
    private static List<WebElement> recuperarListByName(String nombreClase, WebDriver driver) {
        return driver.findElements(By.className(nombreClase));
    }

    // Obtener la primera posicion de la lista
    private static Soltero getPrimero(List<Soltero> lstSolteros) {
        return lstSolteros.get(0);
    }

    // Funcion para hacer capturas
    public static void hacerCaptura(String fileName) {
        try{
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRectangle = new Rectangle(screenSize);
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            ImageIO.write(image, "png", new File(fileName));
        }catch(Exception e){
            System.out.println("Error captura: " + e.getMessage());
        }
    }

    // Funcion para obtener la url del soltero que le pasemos por parámetro
    public static String obtenerUrlSoltero(Soltero soltero, WebDriver driver) {
        List<WebElement> lstClaseUrl = recuperarListByName("product-tile", driver);
        String solteroSeleccionadoUrl = "";

        for (int i = 0; i < lstClaseUrl.size(); i++) {
            WebElement linkElement = lstClaseUrl.get(i).findElement(By.tagName("a"));
            String urlSoltero = linkElement.getAttribute("href");
            if(urlSoltero.contains(soltero.getNombre().toLowerCase())){
                solteroSeleccionadoUrl = urlSoltero;
            }
        }
        return solteroSeleccionadoUrl;
    }

    // Funcion para convertir la puntuacion de string a double
    private static Double convertirPuntuacion(String puntuacion) {
        String[] puntuacionSplit = puntuacion.split("-");
        double puntuacionDouble = 0.0;

        for (int i = 0; i < puntuacionSplit.length; i++) {
            if (puntuacionSplit.length == 3) { // Si al hacer el split el tamaño del array es de 3, lo convertimos de una forma (ya que será x.5)
                puntuacionDouble = Double.parseDouble(puntuacionSplit[1] + "." + puntuacionSplit[2]);
            } else if (puntuacionSplit.length == 2) { // Si el tamaño del array es de 2, lo convertiremos de otra forma (Será un numero entero)
                puntuacionDouble = Double.parseDouble(puntuacionSplit[1]);
            }
        }
        return puntuacionDouble;
    }

    // Función para convertir el precio a double, es decir, le quitaremos el simbolo del $
    private static Double convertirPrecio(String precio){
        try{
            return Double.parseDouble(precio.substring(1, precio.length() - 1));
        }catch (Exception e){
            return 0.0;
        }
    }

    // Funciones para preguntar al usuario por la edad minima, maxima y precio máximo.
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