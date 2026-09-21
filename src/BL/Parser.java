package BL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ParserXML.ItemListSaxHandler;
import Utility.ConfigLoader;
import Utility.CurrencyConverter;
import Utility.ParserPath;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Parser {
	
	private String nRighe = "";//"10";
	private String location = "";//"%22loc%22:%22IT%22,";
	private String minQty = "";//"%22minqty%22:%2250%22,";
	private String condizione = "";//"%22cond%22:%22N%22,";
	private String minPrice ="";//"%22min%22:%220.14%22,";
	private String maxPrice ="";//"%22max%22:%220.19%22,";
	private boolean usePref;
	
	private String xmlInput;
	private String csvOutputPath;
	private String typeSearch;
	private boolean searchToLegoSite;
	private int outerDelay;
	private int innerDelay;
	
	
	ArrayList<LegoItem> itemlist;
	private boolean aggiorna;
	ArrayList<String> sellerPref=null;
	sellerPreferClass SPC=null;
	CurrencyConverter CC=null;
	ColorItemClass COLLIST=null;
	
	private static final Logger logger = LogManager.getLogger(Parser.class);

	public Parser(String typeSearch,String XmlPath,String csvOutputPath,boolean aggiorna,String nRighe,String location,String minQty,String condizione,boolean searchToLegoSite, String minPrice, String maxPrice) {
		
		this.typeSearch=typeSearch;
		this.aggiorna =aggiorna;
		this.xmlInput=XmlPath;
		this.csvOutputPath = csvOutputPath;
		this.searchToLegoSite = searchToLegoSite;
		
		ConfigLoader.loadProperties();
		
		setFilter(nRighe,location,minQty,condizione, minPrice, maxPrice);
		
		run();
		if(!this.typeSearch.equals("ONLYDESC"))
			PriceAnalyzer();
		
		csvCreate();
		
	}

	private void csvCreate() {
		logger.info("creazione file csv");
		new CsvWriter(csvOutputPath+File.separator+ParserPath.getNameWithoutExtension(xmlInput)+".csv",itemlist);
		logger.info("File Csv creato correttamente");
	}
	
	private void PriceAnalyzer() {
		AnalisiPrezzi ap = new AnalisiPrezzi(itemlist,usePref,searchToLegoSite,SPC);
		itemlist  = ap.loadBestPrice();
	}
	
	private void setFilter(String nRighe,String location,String minQty,String condizione, String minPrice, String maxPrice) {
		
		this.nRighe = (nRighe !=null)?nRighe:"10"; // 10,25,50,100,200,500
		 
				this.location="";
				if(location.equals("ALL")) this.location="";
				if(location.equals("IT")) this.location="%22loc%22:%22IT%22,";	
				if(location.equals("EU")) this.location="%22reg%22:%226%22,";	
		
		this.minQty = (!minQty.equals("0"))?"%22minqty%22:%22"+minQty+"%22,":"0";
		
		if(typeSearch.equals("PREF")) {
			this.condizione = (!condizione.equals("ALL"))?"%22invNew%22:%22"+condizione+"%22,":""; //U:usato N:nuovo null:entrambi
		}
		else {
			this.condizione = (!condizione.equals("ALL"))?"%22cond%22:%22"+condizione+"%22,":""; //U:usato N:nuovo null:entrambi
		}
		this.minPrice = (minPrice!=null)?"%22min%22:%22"+minPrice+"%22,":""; // es 0.14
		this.maxPrice = (maxPrice!=null)?"%22max%22:%22"+maxPrice+"%22,":""; // es 0.19
		
        switch (typeSearch) {

		case "ONLYDESC":
        	this.usePref = false;
        	break;

        case "BEST":
        	this.usePref = false;
            break;

        case "BESTPREF":
        	this.usePref = true;
            break;

        case "PREF":
        	this.usePref = true;
            break;
    }
		
	}
	
//	public static void main(String[] args) {
//		// uso il costruttore vuoto per il test, normalmente il path xml verrà passato
//		new BricklinkParser("PREF","C:\\Tmp\\prova_es.xml","C:\\Tmp\\sellerPref.txt","C:\\Tmp",false,"200","EU","0","ALL",null,null);
//		
//
//	}

	
	private void run() {

        SPC = new sellerPreferClass();
        CC = new CurrencyConverter();
        COLLIST = new ColorItemClass();
        
		File xmlItemList = new File(ParserPath.ITEMLISTFILE_PATH);
		ItemListSaxHandler lis = new ItemListSaxHandler();

		try {
			if(!xmlItemList.exists() || aggiorna) {
			    logger.info("Caricamento XML Wanted List");
				//creo lista dei pezzi leggendo il wanted list  file
				itemlist = new WantedListParser().parse(xmlInput);
				
				logger.info("Caricamento dati da BL");
				// Parse bricklink
				itemlist = BLWebParser(ConfigLoader.THREAD_COUNT);
				
				if(searchToLegoSite) {
					logger.info("Caricamento dati da Lego");
					// parse lego
					itemlist = LEGOWebParser(ConfigLoader.THREAD_COUNT);
				}
				
				//scrittura del file itemList.xml
				lis.writeToXml(itemlist, xmlItemList);

			}
			else {
				// se il file esiste e non voglio scaricare di nuovo uso l'ultimo itemlist.xml creato
				itemlist = lis.readFromXml(xmlItemList);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		
	}
	
//	public String getNameWithoutExtension(String file) {
//		
//		File input = new File(file);
//		String name = input.getName();
//	
//	   int dotIndex = name.lastIndexOf('.');
//	   return (dotIndex == -1) ? name : name.substring(0, dotIndex);
//}
	
	private ArrayList<LegoItem> BLWebParser(int threadCount) {

	    ArrayList<LegoItem> risultati = new ArrayList<>();


	    logger.info("Avvio parsing multi-thread con " + threadCount + " thread");

	    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
	    List<Future<LegoItem>> futures = new ArrayList<>();

	    int index = 0;
	    for (LegoItem itm : itemlist) {
	        final int current = ++index;

	        futures.add(executor.submit(() -> {
	            logger.info("Thread " + Thread.currentThread().getName() + " → componente " + current + "/" + itemlist.size());

	            // Delay randomico INTERNO all'interno del thread (prima della richiesta)
	            try {
	                outerDelay = new Random().nextInt(ConfigLoader.DELAY_INNER_MAX + 1);
	                logger.debug("first time out: Thread " + Thread.currentThread().getName() + " aspetta " + outerDelay + "ms prima della richiesta");
	                Thread.sleep(outerDelay);
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	                logger.warn("Sleep interno interrotto nel thread");
	            }

	            ChromeOptions options = new ChromeOptions();
	            options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
	            WebDriver threadDriver = new ChromeDriver(options);

	            LegoItem result = null;
	            try {
	                switch (typeSearch) {

						case "ONLYDESC":
							result = loadOnlyDescFromSalePage(itm, threadDriver);
							break;

	                    case "BEST":

	                        result = loadDataFromSalePage(itm, threadDriver);
	                        break;

	                    case "BESTPREF":

	                        result = loadDataFromSalePage(itm, threadDriver);
	                        break;

	                    case "PREF":

	                        result = loadDataFromStorePage(itm, threadDriver);
	                        break;
	                }

	                result = COLLIST.setItemColorName(result);

	            } catch (Exception e) {
	                logger.info("Errore su componente " + itm.id + ": " + e.getMessage(), e);
	                result = itm;
	            } finally {
	                threadDriver.quit();
	            }

	            return result;
	        }));
	    }

	    for (Future<LegoItem> future : futures) {
	        try {
	            risultati.add(future.get());
	        } catch (Exception e) {
	            logger.error("Errore nel recupero del risultato da un thread", e);
	        }
	    }

	    executor.shutdown();
	    return risultati;
	}
	
	private ArrayList<LegoItem> LEGOWebParser(int threadCount) {

	    ArrayList<LegoItem> risultati = new ArrayList<>();


	    logger.info("Avvio parsing multi-thread con " + threadCount + " thread");

	    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
	    List<Future<LegoItem>> futures = new ArrayList<>();

	    int index = 0;
	    for (LegoItem itm : itemlist) {
	        final int current = ++index;

	        futures.add(executor.submit(() -> {
	            logger.info("Thread " + Thread.currentThread().getName() + " → componente " + current + "/" + itemlist.size());

	            // Delay randomico INTERNO all'interno del thread (prima della richiesta)
	            try {
	                outerDelay = new Random().nextInt(ConfigLoader.DELAY_INNER_MAX + 1);
	                logger.debug("first time out: Thread " + Thread.currentThread().getName() + " aspetta " + outerDelay + "ms prima della richiesta");
	                Thread.sleep(outerDelay);
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	                logger.warn("Sleep interno interrotto nel thread");
	            }

	            ChromeOptions options = new ChromeOptions();
	            options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
	            WebDriver threadDriver = new ChromeDriver(options);

	            LegoItem result = null;
	            try {
	            	
	            	result = loadDataLegoPage(itm, threadDriver);

	            } catch (Exception e) {
	                logger.info("Errore su componente " + itm.id + ": " + e.getMessage(), e);
	                result = itm;
	            } finally {
	                threadDriver.quit();
	            }

	            return result;
	        }));
	    }

	    for (Future<LegoItem> future : futures) {
	        try {
	            risultati.add(future.get());
	        } catch (Exception e) {
	            logger.error("Errore nel recupero del risultato da un thread", e);
	        }
	    }

	    executor.shutdown();
	    return risultati;
	}

	private LegoItem loadDataLegoPage(LegoItem itm, WebDriver threadDriver) {
	    String storeUrl="";
        
	    String legoColorId = COLLIST.colorLinkMap.get(itm.id_color);
	    
	    if(!legoColorId.equals("0")) {
	    		logger.info("analisi item: "+itm.id+" per store LEGO ");

    			storeUrl = "https://www.lego.com/it-it/pick-and-build/pick-a-brick?query="+itm.id+"&color="+legoColorId;
    		    
    			logger.debug(storeUrl);
    			
    		    try {
    		        threadDriver.get(storeUrl);

    		        WebDriverWait wait = new WebDriverWait(threadDriver, Duration.ofSeconds(15));
    		        List<WebElement> items;

					// Attende la presenza di almeno 1 div con data-test="pab-item"
					try {
						items = wait.until(d -> {
							List<WebElement> divs = d.findElements(By.cssSelector("div[data-test='pab-item']"));
							return !divs.isEmpty() ? divs : null;
						});
					} catch (Exception e) {
						logger.warn("⚠️ Nessun elemento 'pab-item' trovato per item " + itm.id);
						return itm;
					}

    		        JavascriptExecutor js = (JavascriptExecutor) threadDriver;
    		        int index = 1;
					
					for (WebElement div : items) {
						try {
							js.executeScript("arguments[0].scrollIntoView(true);", div);
							Thread.sleep(200);

							String title = div.findElement(By.cssSelector("h2[data-test='pab-item-title']")).getText().trim();
							//String elementId = div.findElement(By.cssSelector("p[data-test='pab-item-elementId']")).getText().trim();
							//elementId=elementId.replace("ID: ", "").trim().split("/")[0]; 
							String price = div.findElement(By.cssSelector("div[data-test='pab-item-price']")).getText().trim();
							price = price.replace("€", "").trim();
							
							//logger.info("Elemento #" + index++ + " → Titolo: " + title + ", ID: " + elementId + ", Prezzo: " + price);
							logger.info("Elemento #" + index++ + " → Titolo: " + title + ", Prezzo: " + price);
							
							//itm.alternativeLegoId=elementId;
							itm.sellersList.put("LEGO", price);

						} catch (Exception e) {
							logger.warn("Errore nel parsing del div #" + index + ": " + e.getMessage());
						}
					}

    		        
    		    } catch (Exception e) {
    		        logger.error("Errore generale durante il parsing dello store: " + storeUrl, e);
    		    }
       
	    }
	    else {
	    	logger.info("Codifica colore: non trovate per store LEGO ");
	    }
	    return itm;
	}
	
	private LegoItem loadDataFromStorePage(LegoItem itm, WebDriver threadDriver) {
	    Map<String, String> sellerList = new LinkedHashMap<>();
	    String storeUrl="";
        sellerPref = SPC.getSellerUserList();
        
    	if(sellerPref!=null && !sellerPref.isEmpty()) {
    		for(int i=0; i<sellerPref.size(); i++) {
    			
	            // Delay randomico INTERNO all'interno del thread (prima della richiesta)
	            try {
	                innerDelay = new Random().nextInt(ConfigLoader.DELAY_INNER_MAX + 1);
	                logger.debug("second time out: Thread " + Thread.currentThread().getName() + " aspetta " + innerDelay + "ms prima della richiesta");
	                Thread.sleep(innerDelay);
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	                logger.warn("Sleep interno interrotto nel thread");
	            }
	            
    			logger.info("analisi item: "+itm.id+" per store: "+sellerPref.get(i));
    			// impostare il filtro per la tipologia, ovviamente il parametro ha un nome diverso
    			// valutare nel metodo setfilter come fare
    			storeUrl = "https://store.bricklink.com/"+sellerPref.get(i)+"?p="+sellerPref.get(i)+"#/shop?o={%22q%22:%22"+itm.id+"%22,%22pgSize%22:10,%22Qmin%22:%22"+itm.qty+"%22,%22itemTypeFilter%22:%22P%22,%22colorIDFilter%22:"+itm.id_color+","+this.condizione+"%22showHomeItems%22:0}";
    		    
    			logger.debug(storeUrl);
    			
    		    try {
    		        threadDriver.get(storeUrl);

    		        WebDriverWait wait = new WebDriverWait(threadDriver, Duration.ofSeconds(15));
    		        List<WebElement> articles;

    		        try {
    		            articles = wait.until(d -> {
    		                List<WebElement> art = d.findElements(By.cssSelector("article.item.component.table-row"));
    		                return !art.isEmpty() ? art : null;
    		            });
    		        } catch (Exception e) {
    		            logger.warn("⚠️ Nessun articolo trovato nello store "+sellerPref.get(i)+" per item " + itm.id);
    		            continue;
    		        }

    		        JavascriptExecutor js = (JavascriptExecutor) threadDriver;

    		        int index = 1;
    		        for (WebElement article : articles) {
    		            try {
    		                js.executeScript("arguments[0].scrollIntoView(true);", article);
    		                Thread.sleep(200);

    		                itm.name = (article.findElements(By.cssSelector("div.description p strong"))).get(1).getText().trim();
    		                String condition = article.findElement(By.cssSelector("div.condition strong")).getText().trim();
    		                String quantity = article.findElement(By.cssSelector("div.buy p strong span")).getText().trim().replace(",", "");
    		                String price = article.findElement(By.cssSelector("div.buy div strong")).getText().trim();
    		                price = CC.convertToEuro(price);

    		                // Unisci prezzo e quantità per semplicità (oppure usa un oggetto dedicato)
    		                String summary = "Condizione: " + condition + ", Quantità: " + quantity + ", Prezzo: " + price;
    		                sellerList.put(sellerPref.get(i), price);

    		            } catch (Exception e) {
    		                logger.warn("❌ Errore articolo #" + index + ": " + e.getMessage());
    		            }
    		        }

    		        
    		    } catch (Exception e) {
    		        logger.error("❌ Errore generale durante il parsing dello store: " + storeUrl, e);
    		    }
    		}
    		
    		itm.setSeller(sellerList);

    	}        
        
	    return itm;
	}


		 private LegoItem loadOnlyDescFromSalePage(LegoItem itm,WebDriver threadDriver){
		 
		 	Map<String, String> sellerList; 
		 		
		 		// se no è stata impostata una quantità minima uso il valore della wanted list
		 		minQty=(minQty.equals("0"))?"%22minqty%22:%22"+itm.qty+"%22,":"%22minqty%22:%22"+minQty+"%22,";
		 		
		 		String url = "https://www.bricklink.com/v2/catalog/catalogitem.page?P="+itm.id+"&C="+itm.id_color+"#T=S&C="+itm.id_color+"&O={%22color%22:%22"+itm.id_color+"%22,"+condizione+minPrice+maxPrice+minQty+location+"%22rpp%22:%22"+nRighe+"%22,%22iconly%22:0}";

	        	System.out.println(url);

	        	try {
	        		threadDriver.get(url);
	
		         // Estrai il nome dell'oggetto
		            WebElement itemNameElement = threadDriver.findElement(By.id("item-name-title"));
		            itm.name = itemNameElement.getText();
				}
				catch (Exception e) {

	        		logger.error("ERRORE",e);
	        	}
	            return itm;
		}
	
	 private LegoItem loadDataFromSalePage(LegoItem itm,WebDriver threadDriver){
		 
		 	Map<String, String> sellerList; 
		 		
		 		// se no è stata impostata una quantità minima uso il valore della wanted list
		 		minQty=(minQty.equals("0"))?"%22minqty%22:%22"+itm.qty+"%22,":"%22minqty%22:%22"+minQty+"%22,";
		 		
		 		String url = "https://www.bricklink.com/v2/catalog/catalogitem.page?P="+itm.id+"&C="+itm.id_color+"#T=S&C="+itm.id_color+"&O={%22color%22:%22"+itm.id_color+"%22,"+condizione+minPrice+maxPrice+minQty+location+"%22rpp%22:%22"+nRighe+"%22,%22iconly%22:0}";

	        	System.out.println(url);

	        	try {
	        		threadDriver.get(url);
	
		         // Estrai il nome dell'oggetto
		            WebElement itemNameElement = threadDriver.findElement(By.id("item-name-title"));
		            itm.name = itemNameElement.getText();
      
		            JavascriptExecutor js = (JavascriptExecutor) threadDriver; 
		            WebDriverWait wait = new WebDriverWait(threadDriver, Duration.ofSeconds(15));
		            List<WebElement> rows;

		            try {
		                rows = wait.until(d -> {
		                    List<WebElement> righe = d.findElements(By.cssSelector("tr.pciItemRowEven, tr.pciItemRowOdd"));
		                    return righe.size() >= Integer.valueOf(nRighe) ? righe : null; // Cambia 10 con nRighe se vuoi renderlo dinamico
		                });
		            } catch (Exception e) {
		                logger.warn("Meno di "+nRighe+" righe venditori trovate per item " + itm.id + " / colore " + itm.id_color);
		                return itm;
		            }	
		            
		            sellerList = new LinkedHashMap<String, String>();
		            
		            int index = 1;
		            for (WebElement row : rows) {
		                try {
		                    // Forza lo scroll sulla riga per attivare eventuali contenuti lazy-loaded
		                    js.executeScript("arguments[0].scrollIntoView(true);", row);
		                    Thread.sleep(200); // Breve pausa per permettere il rendering
	
		                    List<WebElement> tds = row.findElements(By.tagName("td"));
	
		                    if (tds.size() >= 5) {
	
		                        String seller = "";
		                        try {
		                            WebElement sellerSpan = tds.get(3).findElement(By.cssSelector("span.pspStoreName"));
		                            seller = sellerSpan.getText().trim();
		                        } catch (Exception e) {
		                            seller = "[Non trovato]";
		                        }
	
		                        String rawPrice = tds.get(4).getText();
		                        String price = "";
		                        for (String line : rawPrice.split("\\n")) {
		                            if (line.contains("EUR")) {
		                                price = CC.convertToEuro(line.trim());
		                                break;
		                            }
		                        }
	
		                        sellerList.put(seller, price);
		                        
		                        
		                    } else {
		                        System.out.println("Riga #" + index++ + " ignorata: meno di 5 colonne");
		                    }
		                } catch (Exception e) {
		                    System.out.println("Errore nella riga #" + index++ + ": " + e.getMessage());
		                }
		            }
		            
		            itm.setSeller(sellerList);
	        	}
	        	catch (Exception e) {

	        		logger.error("ERRORE",e);
	        	}
	            return itm;
	            
	    }
}
