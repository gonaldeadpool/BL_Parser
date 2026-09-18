package Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import BL.ColorItemClass;

public class CurrencyConverter  {

		HashMap<String, String> currencyMap;
		
		 private static final Logger logger = LogManager.getLogger(CurrencyConverter.class);
		
		public CurrencyConverter () {
			
			currencyMap = new HashMap<>();
			loadCurrencyMap();
			
		}
		
		public String convertToEuro(String price) {
			String res="";
			
			for(String k: currencyMap.keySet()) {
				
				if (price.contains(k)){
					res=String.format("%.2f",(Double.valueOf(price.replace(k, "").trim())*Double.valueOf(currencyMap.get(k).trim().replace(",", "."))));
							
				}
			}
			
			return res;
		}
		
		
		
	    /**
	     * Legge un file e costruisce una mappa da righe nel formato "chiave;valore"
	     * 
	     * @param filePath percorso del file
	     * @return HashMap con chiave=prima parte, valore=seconda parte
	     */
	    public void loadCurrencyMap() {
	    	
	        try (InputStream is = Files.newInputStream(Paths.get(ParserPath.VALUTEFILE_PATH))) {
	            if (is == null) {
	                logger.error("Il file "+ParserPath.VALUTEFILE_PATH+" non è stato trovato!");
	                return;
	            }

	            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	            String line;
	            while ((line = reader.readLine()) != null) {
	            	
	                if (line.trim().isEmpty() || line.startsWith("#")) continue;

	                String[] parts = line.split(";", 3); // max 2 pezzi

	                if (parts.length == 3) {
	                	String description = parts[0].trim();
	                    String key = parts[1].trim();
	                    String value = parts[2].trim();
	                    currencyMap.put(key, value);
	                } else {
	                    System.out.println("Riga ignorata (non valida): " + line);
	                }
	            	
	            }
	        } catch (IOException e) {
	            logger.error("Errore nella lettura del file "+ParserPath.VALUTEFILE_PATH, e);
	        }

	    }
	
}
