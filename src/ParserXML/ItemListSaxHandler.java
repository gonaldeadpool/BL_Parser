package ParserXML;

import BL.LegoItem;

import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.util.*;

public class ItemListSaxHandler {

    public void writeToXml(ArrayList<LegoItem> items, File file) throws Exception {
        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler handler = factory.newTransformerHandler();

        Transformer serializer = handler.getTransformer();
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        handler.setResult(new StreamResult(file));
        handler.startDocument();
        AttributesImpl attrs = new AttributesImpl();

        handler.startElement("", "", "LegoItems", attrs);

        for (LegoItem item : items) {
            attrs.clear();
            
            handler.startElement("", "", "LegoItem", attrs);

            writeElement(handler, "id", item.id);
            writeElement(handler, "idLegoAlternativo", item.alternativeLegoId);
            writeElement(handler, "name", item.name);
            writeElement(handler, "id_color", item.id_color);
            writeElement(handler, "color", item.color);
            writeElement(handler, "qty", item.qty);
            writeElement(handler, "bestSeller", item.bestSeller);
            writeElement(handler, "bestPrice", item.bestPrice);

            handler.startElement("", "", "sellers", attrs);
            if(item.getSeller()!=null){
	            for (Map.Entry<String, String> seller : item.getSeller().entrySet()) {
//	                attrs.clear();
	                attrs.addAttribute("", "", "name", "CDATA", seller.getKey());
	                handler.startElement("", "", "seller", attrs);
	                char[] priceChars = seller.getValue().toCharArray();
	                handler.characters(priceChars, 0, priceChars.length);
	                handler.endElement("", "", "seller");
	            }
            }
            handler.endElement("", "", "sellers");

            handler.endElement("", "", "LegoItem");
        }

        handler.endElement("", "", "LegoItems");
        handler.endDocument();
    }

    private void writeElement(TransformerHandler handler, String tag, String value) throws SAXException {
        if (value == null) value = "";
        handler.startElement("", "", tag, null);
        char[] chars = value.toCharArray();
        handler.characters(chars, 0, chars.length);
        handler.endElement("", "", tag);
    }

    public ArrayList<LegoItem> readFromXml(File file) throws Exception {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        SAXHandler handler = new SAXHandler();
        parser.parse(file, handler);
        return handler.getItems();
    }

    // SAX parser
    private static class SAXHandler extends DefaultHandler {
        private ArrayList<LegoItem> items = new ArrayList<>();
        private LegoItem currentItem;
        private StringBuilder content = new StringBuilder();
        private String currentSellerName;
        private boolean insideSellers = false;

        public ArrayList<LegoItem> getItems() {
            return items;
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            content.setLength(0);
            if ("LegoItem".equals(qName)) {
                currentItem = new LegoItem();
            } else if ("sellers".equals(qName)) {
                insideSellers = true;
            } else if ("seller".equals(qName)) {
                currentSellerName = attributes.getValue("name");
            }
        }

        public void characters(char[] ch, int start, int length) {
            content.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String qName) {
            switch (qName) {
                case "LegoItem":
                    items.add(currentItem);
                    break;
                case "idLegoAlternativo":
                    currentItem.alternativeLegoId = content.toString();
                    break;
                case "id":
                    currentItem.id = content.toString();
                    break;    
                case "name":
                    currentItem.name = content.toString();
                    break;
                case "id_color":
                    currentItem.id_color = content.toString();
                    break;
                case "color":
                    currentItem.color = content.toString();
                    break;
                case "qty":
                    currentItem.qty = content.toString();
                    break;
                case "bestSeller":
                    currentItem.bestSeller = content.toString();
                    break;
                case "bestPrice":
                    currentItem.bestPrice = content.toString();
                    break;
                case "seller":
                    currentItem.getSeller().put(currentSellerName, content.toString());
                    break;
                case "sellers":
                    insideSellers = false;
                    break;
            }
        }
    }
}
