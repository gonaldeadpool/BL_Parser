import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HtmlParser {

   
    public static String findElementById(String html, String id) {
        Document doc = Jsoup.parse(html);
        
        Element elemento = doc.getElementById(id);
        
        if (elemento != null) {
            return elemento.text();
        } else {
            return "";
        }
        
 
    }

}
