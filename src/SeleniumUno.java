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
    private static final String URL = "https://amazondating.co/";

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\Desarrollo\\Librerias\\Selenium-driver\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        List<Soltero> lstSolteros;
        List<Soltero> lstFiltradaOrdenada = new ArrayList<>();
        Soltero solteroSeleccionado;
        String solteroSeleccionadoString = "";

        try{
            lstSolteros = obtenerListaDeWeb(driver);
            hacerCaptura("Paso_1_EntrarPag");
            Thread.sleep(5000);

            if (!lstSolteros.isEmpty()) {
                int edadMin = 60; //preguntarMinEdad();
                int edadMax = 80; //preguntarMaxEdad();
                double precioMax = 1200; //preguntarPrecioMax();

                lstFiltradaOrdenada = filtrarLista(edadMin, edadMax, precioMax, lstSolteros);
            }

            solteroSeleccionadoString = obtenerUrlSoltero(getPrimero(lstFiltradaOrdenada), driver);

            hacerPedido(solteroSeleccionadoString, driver);



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

    private static void hacerPedido(String urlSoltero, WebDriver driver) {
        try{
            driver.get(urlSoltero);
            Thread.sleep(2000);
            hacerCaptura("Paso_2_URL_Soltero_Seleccionado");


            WebElement dropSoltero = driver.findElement(By.id("select-demeanor"));
            Select sel = new Select(dropSoltero);

            sel.selectByValue("7’7”");
            Thread.sleep(2000);
            hacerCaptura("Paso_3_Seleccionar_Altura");


            List<WebElement> loveLanguage = driver.findElements(By.className("interest-button"));
            loveLanguage.get(3).click();
            Thread.sleep(2000);
            hacerCaptura("Paso_4_Seleccionar_LoveLanguage");


            WebElement btnComprar = driver.findElement(By.className("buy-now"));
            btnComprar.click();
            Thread.sleep(2000);
            hacerCaptura("Paso_5_Seleccionar_Comprar");


            WebElement btnCompraFinal = driver.findElement(By.xpath("/html/body/div[2]/div/div/div/div/div[2]/button"));
            btnCompraFinal.click();
            Thread.sleep(2000);
            hacerCaptura("Paso_6_Aceptar_Compra_Final");

            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println("Error compra: " + e.getMessage());
        }


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

        List<WebElement> nombresEdad = recuperarListByName("product-name", driver);
        List<WebElement> productos = recuperarListByName("product-price", driver);
        List<WebElement> puntuaciones = recuperarListByName("stars", driver);


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

    private static List<WebElement> recuperarListByName(String nombreClase, WebDriver driver) {
        return driver.findElements(By.className(nombreClase));
    }

    private static Soltero getPrimero(List<Soltero> lstSolteros) {
        return lstSolteros.get(0);
    }

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