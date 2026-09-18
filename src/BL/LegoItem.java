package BL;


import java.util.LinkedHashMap;
import java.util.Map;

public class LegoItem {

	public String id;
	public String name;
	public String id_color;
	public String color;
	public String qty;
	
	public String bestSeller;
	public String bestPrice;

	public String alternativeLegoId;
	
	Map<String, String> sellersList;
	
	public LegoItem() {
		
		sellersList = new LinkedHashMap<String, String>();
	}

	public Map<String, String> getSeller() {
		return sellersList;
	}

	public void setSeller(Map<String, String> seller) {
		this.sellersList = seller;
	}
	
	
}
