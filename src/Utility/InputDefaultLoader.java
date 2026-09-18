package Utility;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class InputDefaultLoader {

    // Variabili globali
    public static String INPUT_XML;
    public static String OUTPUT_CSV_FOLDER;
    public static String INPUT_PREF;

    // Percorso del file modificabile
    private static final String INPUT_DEFAULT_PATH = "config/input_default.properties"; // es: può essere fuori da resources

    private static Properties props = new Properties();

    public static void loadProperties() {
        try (InputStream input = Files.newInputStream(Paths.get(INPUT_DEFAULT_PATH))) {
            props.load(input);

            INPUT_XML = props.getProperty("inputxml.path");
            OUTPUT_CSV_FOLDER = props.getProperty("outputcsv.folder");
            INPUT_PREF = props.getProperty("inputPref.path");

        } catch (IOException e) {
            throw new RuntimeException("❌ Errore nel caricamento delle proprietà: " + e.getMessage(), e);
        }
    }

    public static void saveProperties() {
        try (OutputStream output = Files.newOutputStream(Paths.get(INPUT_DEFAULT_PATH))) {
            // Aggiorna il contenuto delle proprietà
            props.setProperty("inputxml.path", INPUT_XML);
            props.setProperty("outputcsv.folder", OUTPUT_CSV_FOLDER);
            props.setProperty("inputPref.path", String.valueOf(INPUT_PREF));

            // Salva nel file
            props.store(output, "Configurazione aggiornata");

        } catch (IOException e) {
            throw new RuntimeException("❌ Errore nella scrittura delle proprietà: " + e.getMessage(), e);
        }
    }

	public static void set_INPUT_PREF(String text) {
		// TODO Auto-generated method stub
		INPUT_PREF=text;
	}
	public static void set_INPUT_XML(String text) {
		// TODO Auto-generated method stub
		INPUT_XML=text;
	}
	public static void set_INPUT_CSV_FOLDER(String text) {
		// TODO Auto-generated method stub
		OUTPUT_CSV_FOLDER=text;
	}
}