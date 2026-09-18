package BL;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvWriter {

    /**
     * Scrive una lista di oggetti in un file CSV, usando ";" come separatore.
     *
     * @param filePath percorso del file di output
     * @param data     lista di oggetti da scrivere
     */

	public CsvWriter (String fileName, List<LegoItem> items) {
		try {
			saveToCsv(fileName, items);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public <T> CsvWriter (String filePath, ArrayList<T> data) {
//		writeToCsv(filePath, data);
//	}
	
    public <T> void writeToCsv(String filePath, ArrayList<T> data) {
        if (data == null || data.isEmpty()) {
            System.out.println("Nessun dato da scrivere.");
            return;
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            Class<?> clazz = data.get(0).getClass();
            Field[] fields = clazz.getDeclaredFields();

            // Intestazione (nomi dei campi)
            for (int i = 0; i < fields.length; i++) {
                writer.write(fields[i].getName());
                if (i < fields.length - 1) writer.write(";");
            }
            writer.write("\n");

            // Righe
            for (T item : data) {
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Object value = fields[i].get(item);
                    writer.write(value != null ? value.toString() : "");
                    if (i < fields.length - 1) writer.write(";");
                }
                writer.write("\n");
            }

            System.out.println("File CSV scritto correttamente: " + filePath);

        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public void saveToCsv(String fileName, List<LegoItem> items) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        
        // Intestazione CSV
        
        writer.write("#!id;descrizione;id_colore;descrizione colore;qty;venditore;prezzo unitario;prezzo totale\n");

        int riga=0;
        for (LegoItem item : items) {
        	
            riga++;
            System.out.println("Scrivendo riga " + riga + ": color = '" + item.color + "'");

            // Scrivi riga
            writer.write(String.join(";",
                    item.id,
                    item.name,
                    item.id_color,
                    item.color,
                    item.qty,
                    item.bestSeller,
                    item.bestPrice,
                    (item.bestPrice!=null && !item.bestPrice.isEmpty())?String.valueOf(Double.valueOf(item.bestPrice.replace(",", "."))*Double.valueOf(item.qty)).replace(".", ","):"0"
            ) + "\n");
        }

        writer.close();
        System.out.println("✅ CSV creato: " + fileName);
    }

    public Map<String, String> ordinaMappa(Map<String, String> mapToOrder){
    	
    	Map<String, String> sortedMap = mapToOrder.entrySet()
    		    .stream()
    		    .sorted(
    		        Comparator.comparing(Map.Entry<String, String>::getValue)
    		                  .thenComparing(Map.Entry::getKey)
    		    )
    		    .collect(
    		        LinkedHashMap::new,
    		        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
    		        LinkedHashMap::putAll
    		    );
    	
    	return sortedMap;
    }
    
}
