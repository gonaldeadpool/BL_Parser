package BL;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Utility.ParserPath;

public class sellerPreferClass {

	private static final Logger logger = LogManager.getLogger(ColorItemClass.class);
	ArrayList<String> sellerPreferUserList;
	ArrayList<String> sellerPreferStoreList;
	String pathPreferFile = ParserPath.SELLERFILE_PATH;
	public sellerPreferClass() {
		// TODO Auto-generated constructor stub
		loadSellerPreferList();

	}
	
	private void loadSellerPreferList(){
		sellerPreferUserList = new ArrayList<String>();
		sellerPreferStoreList = new ArrayList<String>();
		
		try (InputStream is = new FileInputStream(this.pathPreferFile)) {
            if (is == null) {
                logger.error("Il file "+pathPreferFile+" non è stato trovato");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            String[] coppia;
            
            while ((line = reader.readLine()) != null) {
            	
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                coppia = line.split(";");
                
                sellerPreferUserList.add(coppia[0]);
                sellerPreferStoreList.add(coppia[1]);
            	
            }
        } catch (IOException e) {
            logger.error("Errore nella lettura del file "+this.pathPreferFile, e);
        }
	}
	
	public ArrayList<String> getSellerUserList() {
		return this.sellerPreferUserList;
	}
	
	public ArrayList<String> getSellerStoreList() {
		return this.sellerPreferStoreList;
	}
	
}
