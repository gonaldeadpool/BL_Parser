package Utility;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigLoader {

    // Variabili globali
    public static int THREAD_COUNT;
    public static int DELAY_MAX;
    public static int DELAY_INNER_MAX;

    // Percorso del file modificabile
    private static final String CONFIG_PATH = "config/config.properties"; // es: può essere fuori da resources

    private static Properties props = new Properties();

    public static void loadProperties() {
        try (InputStream input = Files.newInputStream(Paths.get(CONFIG_PATH))) {
            props.load(input);

            THREAD_COUNT = Integer.parseInt(props.getProperty("thread.count"));
            DELAY_MAX = Integer.parseInt(props.getProperty("delay.max"));
            DELAY_INNER_MAX = Integer.parseInt(props.getProperty("delayInner.max"));

        } catch (IOException e) {
            throw new RuntimeException("❌ Errore nel caricamento delle proprietà: " + e.getMessage(), e);
        }
    }

    public static void saveProperties() {
        try (OutputStream output = Files.newOutputStream(Paths.get(CONFIG_PATH))) {
            // Aggiorna il contenuto delle proprietà
            props.setProperty("thread.count", String.valueOf(THREAD_COUNT));
            props.setProperty("delay.max", String.valueOf(DELAY_MAX));
            props.setProperty("delayInner.max", String.valueOf(DELAY_INNER_MAX));

            // Salva nel file
            props.store(output, "Configurazione aggiornata");

        } catch (IOException e) {
            throw new RuntimeException("❌ Errore nella scrittura delle proprietà: " + e.getMessage(), e);
        }
    }
}