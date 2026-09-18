import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import BL.CsvEditorFrame;
import Utility.ParserPath;

public class StartMenuFrame extends JMenuBar{
	
	StartMenuFrame(){
		
		// Crea un menu "File"
	    JMenu fileMenu = new JMenu("File");


	    // Aggiungi voci al menu "File"

	    JMenuItem exitItem = new JMenuItem("Esci");

	    exitItem.addActionListener(e -> System.exit(0));

	    fileMenu.add(exitItem);

	    JMenu ImpostazioniMenu = new JMenu("Impostazioni");
	    
	    JMenuItem prefItem = new JMenuItem("Preferiti");
	    JMenuItem valItem = new JMenuItem("Valute");
	    JMenuItem colorItem = new JMenuItem("Mappa colori");
	    JMenuItem impItem = new JMenuItem("Setup parametri");
	    
	    prefItem.addActionListener(e -> new CsvEditorFrame("seller Preferiti",new File(ParserPath.SELLERFILE_PATH),true).setVisible(true));
	    valItem.addActionListener(e -> new CsvEditorFrame("Valute",new File(ParserPath.VALUTEFILE_PATH),true).setVisible(true));
	    colorItem.addActionListener(e -> new CsvEditorFrame("Mappa colori",new File(ParserPath.COLORFILE_PATH),false).setVisible(true));
	    
	    impItem.addActionListener(e -> new PropertiesEditorFrame("Setup Parametri",new File(ParserPath.CONFIGPROP_PATH)).setVisible(true));
	    
	    ImpostazioniMenu.add(prefItem);
	    ImpostazioniMenu.add(valItem);
	    ImpostazioniMenu.add(colorItem);
	    ImpostazioniMenu.addSeparator(); // linea separatrice
	    ImpostazioniMenu.add(impItem);
	    
	    
	    // Aggiungi menu alla barra dei menu
	    add(fileMenu);
	    add(ImpostazioniMenu);
		
	}


	
}
