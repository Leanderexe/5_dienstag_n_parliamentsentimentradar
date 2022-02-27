import API.RestAPI;
import NLP.Pipeline;
import org.xml.sax.SAXException;
import protocolscraping.XmlConversion;
import runner.AppRunner;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Is the Main class in which the user can interact with the application.
 * @author Leander Hermanns
 */
public class Main {

    /**
     * Is the Main methode in which the user can interact with the application.
     * @author Leander Hermanns
     */
    public static void main (String[] args) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        List<String> searchList = Arrays.asList("Plenarprotokolle der 20. Wahlperiode",
                "Plenarprotokolle der 19. Wahlperiode");
        String url = "https://www.bundestag.de/services/opendata";
        XmlConversion conversion = new XmlConversion(url, searchList);
        Pipeline pip = new Pipeline();
        Scanner User_Input = new Scanner(System.in);
        AppRunner app = new AppRunner();
        List valid_input = new ArrayList();
        for (int i = 0; i < 4; i++) {
            valid_input.add(i);
        }
        boolean Valid = true;
        // Ask User for a valid Input.
        while (Valid) {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println('\n' + "Geben Sie die Nummer der Aufgabe ein (0-18):" + '\n' +
                        "[1]: Aufsetzten der API Verbindung" + '\n' +
                        "[2]: Redner, Reden und Protokolle in die Datenbank lesen" + '\n' +
                        "[3]: POS, named entities, token und sentiments in die Datenbank einlesen" + '\n' +
                        "[0]: Exit");
                String Input = User_Input.nextLine();
                // Checks if the Input is a valid number.
                if (valid_input.contains(Integer.parseInt(Input))) {
                } else {
                    System.out.println(">>>>>>>>>>Das hat nicht geklappt! Bitte geben Sie eine Nummer zwischen 0 und 18 ein.<<<<<<<<<<" + '\n');
                }

                if (Input.equals("1")) {
                    System.out.println("---------------------- Die APIRest wird initialisiert ---------------------");
                    RestAPI.getAPI();
                    System.out.println("--------------------------------------------------------------------------");
                } else if (Input.equals("2")) {
                    System.out.println("---------------------- Die Protokolle werden eingelesen ---------------------");
                    AppRunner.getProtokolls();
                    System.out.println("------------------------------------------------------------------------------");
                } else if (Input.equals("3")) {
                    System.out.println("---------------------- Die JCas Objekte werden erstellt ---------------------");
                    pip.generatejCAStop(); // generates the JCas objects.
                    System.out.println("--------------------------------------------------------------------------");
                } else if (Input.equals("0")) {
                    System.out.println("Das Programm wurde erfolgreich beendet.");
                    Valid = false;
                }
            } catch (Exception e) {
                System.out.println(">>>>>>>>>>Das hat nicht geklappt! Bitte geben Sie eine Nummer zwischen 0 und 3 ein.<<<<<<<<<<" + '\n');
            }
        }
    }
}
