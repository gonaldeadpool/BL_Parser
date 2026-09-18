package Utility;

import java.io.File;

public class ParserPath {
	
	public static final String SELLERFILE_PATH = "in/sellerPref.txt";
	public static final String ITEMLISTFILE_PATH = "out/itemList.xml";
	public static final String VALUTEFILE_PATH = "in/cambio_valute.csv";
	public static final String COLORFILE_PATH = "in/color_list.csv";
	public static final String CONFIGPROP_PATH = "config/config.properties";
	
	
	public static String getNameWithoutExtension(String file) {
		
		File input = new File(file);
		String name = input.getName();
	
	   int dotIndex = name.lastIndexOf('.');
	   return (dotIndex == -1) ? name : name.substring(0, dotIndex);
}

}
