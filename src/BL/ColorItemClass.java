package BL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ColorItemClass {

	private static final String COLOR_LIST_PATH = "in/color_list.csv";
	
	HashMap<String, String> colorBLMap;
	HashMap<String, String> colorLEGOMap;
	HashMap<String, String> colorLinkMap;
	
	 private static final Logger logger = LogManager.getLogger(ColorItemClass.class);
	
	public ColorItemClass(){
		
		colorBLMap = new HashMap<>();
		colorLEGOMap = new HashMap<>();
		colorLinkMap = new HashMap<>();
		loadColorMap();
		
	}
	
	
	
	
	
    /**
     * Legge un file e costruisce una mappa da righe nel formato "chiave;valore"
     * 
     * @param filePath percorso del file
     * @return HashMap con chiave=prima parte, valore=seconda parte
     */
    public void loadColorMap() {
    	
        try (InputStream is = Files.newInputStream(Paths.get(COLOR_LIST_PATH))) {
            if (is == null) {
                logger.error("Il file "+COLOR_LIST_PATH+" non è stato trovato nel classpath!");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
            	
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(";", 4); // max 2 pezzi

                if (parts.length == 4) {
                    String keyLego = parts[0].trim();
                    String valueLego = parts[1].trim();
                    String keyBL = parts[2].trim();
                    String valueBL = parts[3].trim();
                    
                    colorBLMap.put(keyBL, valueBL);
                    if(!keyLego.isEmpty()) {
                    	colorLEGOMap.put(keyLego, valueLego);
                    	colorLinkMap.put(keyBL, keyLego);
                    }
                    else {
                    	colorLinkMap.put(keyBL, "0");
                    }
                    
                    
                } else {
                    System.out.println("Riga ignorata (non valida): " + line);
                }
            	
                //logger.info("Colore letto: " + line);
            }
        } catch (IOException e) {
            logger.error("Errore nella lettura del file "+COLOR_LIST_PATH, e);
        }

    }

    public void setColorName(ArrayList<LegoItem> legoItemList) {
    	
    	//ArrayList<LegoItem>  res= legoItemList;
    	    	
		for(LegoItem itm: legoItemList) {
			String color = colorBLMap.get(itm.id_color);
			itm.color = color;
		}

		//return res;
    }
    
    public LegoItem setItemColorName(LegoItem itm) {
    		
    		if(itm!=null)
			itm.color = colorBLMap.get(itm.id_color);
			
    		return itm;
	}

    
}
