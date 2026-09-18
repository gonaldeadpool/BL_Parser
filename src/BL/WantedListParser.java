package BL;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;

public class WantedListParser {
	

    public ArrayList<LegoItem>  parse(String filePath) {
        
    	ArrayList<LegoItem> legoList = null;;
    	
    	try {
            // Ottieni il parser SAX
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            GenericHandler handler = new GenericHandler();
            // Esegui il parsing con un handler personalizzato
            saxParser.parse(new File(filePath), handler);
            
            legoList =  handler.legoItemList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return legoList;
    }

    // Classe handler per gestire eventi SAX
    private static class GenericHandler extends DefaultHandler {
    	public ArrayList<LegoItem> legoItemList = new ArrayList<LegoItem>();
    	private StringBuilder contenuto = new StringBuilder();
    	
    	LegoItem item;


        @Override
        public void startDocument() throws SAXException {
            System.out.println("Inizio lettura wanted list");

            contenuto.setLength(0); 
        }

        @Override
        public void endDocument() throws SAXException {
            System.out.println("Fine lettura wanted list");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            //System.out.println("Inizio elemento: " + qName);
            
            if(qName.equalsIgnoreCase("ITEM")) {
            	item = new LegoItem();
            }
            contenuto.setLength(0);
//
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {

            
            contenuto.append(ch, start, length);

        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            //System.out.println("Fine elemento: " + qName);
	        	switch (qName) {
	            case "ITEMID":
	            	item.id = contenuto.toString().trim();
	                break;
	            case "COLOR":
	            	item.id_color=contenuto.toString().trim();
	                break;
	            case "MINQTY":
	                item.qty = contenuto.toString().trim();
	                break;
	            case "ITEM":
	            	legoItemList.add(item);
	                break;
	        	}
        }
    }

}