package BL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnalisiPrezzi {

	public ArrayList<LegoItem> itemList;
	public boolean usePref;
	public boolean searchToLegoSite;
	
	Map<String, String> item_seller_list;
	ArrayList<String> sellerUserPref=null;
	ArrayList<String> sellerStorePref=null;
	sellerPreferClass SPC=null;
	LinkedHashMap<String,Integer> itemToSeller;
	
	AnalisiPrezzi(ArrayList<LegoItem> itemList, boolean usePref,boolean searchToLegoSite,sellerPreferClass SPC){
		
		this.itemList=itemList;
		this.usePref=usePref;
		this.searchToLegoSite=searchToLegoSite;
		
    	if(this.usePref) {
            sellerUserPref = SPC.getSellerUserList();
            sellerStorePref = SPC.getSellerStoreList();
    	}
	}
	
	public ArrayList<LegoItem> loadBestPrice() {
		
		// prima passata carico il prezzo migliore per ogni pezzo
		// se flag usa preferiti = true hanno priorità rispetto al miglior prezzo
       
		for (LegoItem item : this.itemList) {
            // Ottieni primo elemento della mappa seller
            String sellerKey = "";
            String sellerValue = "";

            if (item.sellersList != null && !item.sellersList.isEmpty()) {
            	
            	sellerKey = searchSeller(item.sellersList);
            	sellerValue = item.sellersList.get(sellerKey);
            	
            	item.bestSeller = sellerKey;
            	item.bestPrice = sellerValue;
            }

		}
		
        return this.itemList;
		
	}
	
	    private String searchSeller(Map<String,String> sellerList) {
	    	
	    	String key="";
	    	String keyPart="";
	    	//ordinaMappa(sellerList);
	    	
	    	Double bestPrice=-1.0;
	    	String bestSeller="";

	    	if(searchToLegoSite) {
	    		if(sellerUserPref!=null) sellerUserPref.add("LEGO");
	    		if(sellerStorePref!=null) sellerStorePref.add("LEGO");
	    	}
	    	
	    	if(usePref) {
	        	if(sellerUserPref!=null && !sellerUserPref.isEmpty()) {
	        		for(int i=0; i<sellerUserPref.size(); i++) {
	        			for(String seller : sellerList.keySet()) {
	        				keyPart = (sellerUserPref.get(i).length()>15)?sellerUserPref.get(i).substring(0,15):sellerUserPref.get(i);
	        				if(seller.startsWith(keyPart)) {
	        					
	        					// se trovo più seller preferiti prendo quello con prezzo minore
	        					if(bestPrice<0 || bestPrice > Double.valueOf(sellerList.get(seller).replace(",", "."))){
	        						bestPrice = Double.valueOf(sellerList.get(seller).replace(",", "."));
	        						bestSeller= seller;
	        					}
	        				}
	        			}
	        		}
	        	}
	        	
	        	if(sellerStorePref!=null && !sellerStorePref.isEmpty() && bestSeller.isEmpty()) {
	        		for(int i=0; i<sellerStorePref.size(); i++) {
	        			for(String seller : sellerList.keySet()) {
	        				keyPart = (sellerStorePref.get(i).length()>15)?sellerStorePref.get(i).substring(0,15):sellerStorePref.get(i);
	        				if(seller.startsWith(keyPart)) {
	        					
	        					// se trovo più seller preferiti prendo quello con prezzo minore
	        					if(bestPrice<0 || bestPrice > Double.valueOf(sellerList.get(seller).replace(",", "."))){
	        						bestPrice = Double.valueOf(sellerList.get(seller).replace(",", "."));
	        						bestSeller= seller;
	        					}
	        				}
	        			}
	        		}
	        	}
	    	}	
	    	
        	if (!usePref && bestSeller.isEmpty()) {
	        	// se la lista dei preferiti è nulla o tra i preferiti non ho trovato nessun seller vantaggioso metto
	        	//metto il primo della lista in ordine di prezzo
	            Map.Entry<String, String> firstEntry = sellerList.entrySet().iterator().next();
	            bestSeller = firstEntry.getKey();
			}

        	return bestSeller;
	    }
}
