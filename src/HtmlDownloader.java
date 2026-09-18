import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HtmlDownloader {

    public static String download(String urlString) {
        StringBuilder html = new StringBuilder();

        try {
            // Crea oggetto URL
            URL url = new URL(urlString);
            // Apre connessione HTTP
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Java HtmlDownloader");

            // Legge la risposta
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    html.append(inputLine).append("\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String res= html.toString();
        
        return res;
    }


}
