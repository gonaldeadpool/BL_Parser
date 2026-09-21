

import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import BL.Parser;
import BL.CsvEditorFrame;
import Utility.InputDefaultLoader;
import Utility.ParserPath;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;

public class InputPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textFieldInputFile;
	private JTextField textFieldOutputFolder;
	private JButton btnNewButton;

	 private static final Logger logger = LogManager.getLogger(InputPanel.class);
	 private JLabel lblNewLabel;
	 private JLabel lblNewLabel_2;
	 private JLabel msg_label;
	 private JLabel lblNewLabel_1;
	 private JButton btnOutputFolder;
	 private JLabel lblNewLabel_4;
	 private JLabel lbl_combowhere;
	 private JComboBox<ComboItem> comboWhere;
	 private JLabel lbl_comboseller;
	 private JComboBox<ComboItem> comboBox_seller;
	 private JLabel lbl_combotype;
	 private JComboBox<ComboItem> comboBox_type;
	 private JLabel lblNewLabel_5;
	 private JComboBox<ComboItem> combo_maxseller;
	 
	 private String comboBox_seller_value ="ONLYDESC";
	 private String comboBox_where_value = "ALL";
	 private String comboBox_type_value = "ALL";
	 private String comboBox_maxseller_value = "10";
	 private boolean chckbx_Lego_PaB_value = false;
	 private boolean chckAggiorna_value=true;
	 private JLabel lblNewLabel_6;
	 private JTextField textField_minqty;
	 private JCheckBox chckbx_minqty;
	 private JCheckBox chckbx_Lego_PaB;
	 private JCheckBox chckAggiorna;
	 private JButton btnOpenRes;


	/**
	 * Create the panel.
	 */
	public InputPanel() {
		
		//precarico i default
		InputDefaultLoader.loadProperties();
		
		setLayout(new MigLayout("", "[][69.00][][][][grow][grow][grow][]", "[21px][][][][][][][][][][][][][][][][][][][][][][][][][]"));
		
		lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(new ImageIcon(getClass().getResource("banner.jpg")));
		add(lblNewLabel_1, "cell 1 1 8 1,alignx center,growy");
		
		lblNewLabel_2 = new JLabel("Seleziona file da analizzare (xml)");
		add(lblNewLabel_2, "cell 1 3");
		
		textFieldInputFile = new JTextField();
		textFieldInputFile.setText(InputDefaultLoader.INPUT_XML);
		textFieldInputFile.setEditable(false);
		add(textFieldInputFile, "cell 1 4 6 1,growx,aligny center");
		textFieldInputFile.setColumns(40);
		
		JButton btnInputFile = new JButton("...");
		btnInputFile.setVisible(true);
		btnInputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInputFile(textFieldInputFile);
			}
		});
		add(btnInputFile, "cell 7 4,alignx left,aligny top");
		
		lblNewLabel = new JLabel("Seleziona cartella di destinazione");
		add(lblNewLabel, "cell 1 5");
		
		textFieldOutputFolder = new JTextField();
		textFieldOutputFolder.setText(InputDefaultLoader.OUTPUT_CSV_FOLDER);
		textFieldOutputFolder.setEditable(false);
		add(textFieldOutputFolder, "cell 1 6 6 1,growx");
		textFieldOutputFolder.setColumns(40);
		
		btnOutputFolder = new JButton("...");
		btnOutputFolder.setVisible(true);
		btnOutputFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setOutputFolder(textFieldOutputFolder);
			}
		});
		add(btnOutputFolder, "cell 7 6");
		
		lblNewLabel_4 = new JLabel("Parametri di elaborazione");
		add(lblNewLabel_4, "flowx,cell 1 10 7 1,alignx center,aligny center");
		
		lbl_comboseller = new JLabel("Cerca:");
		add(lbl_comboseller, "flowx,cell 1 11,alignx left");
		
		comboBox_seller = new JComboBox<ComboItem>();
		comboBox_seller.addItem(new ComboItem("Solo descrizione", "ONLYDESC"));
		comboBox_seller.addItem(new ComboItem("Solo tra i sellers preferiti", "PREF"));
		comboBox_seller.addItem(new ComboItem("Sellers con prezzo migliore", "BEST"));
		comboBox_seller.addItem(new ComboItem("Sellers migliori con precedenza ai preferiti", "BESTPREF"));

		comboBox_seller.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ComboItem selectedItem = (ComboItem) e.getItem();
                    comboBox_seller_value = selectedItem.getValue();
                    System.out.println("Valore selezionato: " + comboBox_seller_value);
                    if(comboBox_seller_value.equals("BEST") || comboBox_seller_value.equals("BESTPREF")) {
                    	
                    	if(!combo_maxseller.isVisible()) combo_maxseller.setSelectedIndex(0);
                    	
                    	combo_maxseller.setVisible(true);
                    	lblNewLabel_5.setVisible(true);
                    	
                    	lbl_combowhere.setVisible(true);
                    	comboWhere.setVisible(true);
                    }
                    else {
                    	combo_maxseller.setSelectedIndex(0);
                    	combo_maxseller.setVisible(false);
                    	lblNewLabel_5.setVisible(false);
                    	
                    	lbl_combowhere.setVisible(false);
                    	comboWhere.setVisible(false);
                    	comboWhere.setSelectedIndex(0);
                    }
                }
            }

        });
		add(comboBox_seller, "cell 1 12 3 1,growx");
		
		chckbx_Lego_PaB = new JCheckBox("Cerca anche con Lego Pick a Brick");
		add(chckbx_Lego_PaB, "cell 5 12");
		chckbx_Lego_PaB.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				chckbx_Lego_PaB_value = chckbx_Lego_PaB.isSelected();
			}
		});
		
		lbl_combotype = new JLabel("Condizione");
		add(lbl_combotype, "cell 1 13,alignx left");
		
		comboBox_type = new JComboBox<ComboItem>();
		comboBox_type.addItem(new ComboItem("Nuovo/Usato", "ALL"));
		comboBox_type.addItem(new ComboItem("Nuovo", "N"));
		comboBox_type.addItem(new ComboItem("Usato", "U"));
		comboBox_type.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ComboItem selectedItem = (ComboItem) e.getItem();
                    comboBox_type_value = selectedItem.getValue();
                    System.out.println("Valore selezionato: " + comboBox_type_value);
                    
                    if(comboBox_type_value.equals("U")) {
                    	chckbx_Lego_PaB.setSelected(false);
                    	chckbx_Lego_PaB.setVisible(false);
                    }
                    else {
                    	chckbx_Lego_PaB.setVisible(true);                    	
                    }
                }
            }

        });
		
		lblNewLabel_6 = new JLabel("Numero min pezzi: (se 0 usa la quantità w list)");
		add(lblNewLabel_6, "cell 5 13");
		add(comboBox_type, "cell 1 14 3 1,growx");
		
		chckbx_minqty = new JCheckBox("");
		add(chckbx_minqty, "flowx,cell 5 14");
		chckbx_minqty.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(chckbx_minqty.isSelected()) {
					textField_minqty.setEditable(true);
				}
				else{
					textField_minqty.setEditable(false);
					textField_minqty.setText("0");
				}
			}
		});
		
		lbl_combowhere = new JLabel("Dove:");
		add(lbl_combowhere, "flowx,cell 1 16,alignx left");
		
		comboWhere = new JComboBox<ComboItem>();
		comboWhere.addItem(new ComboItem("Ovunque", "ALL"));
		comboWhere.addItem(new ComboItem("Italia", "IT"));
		comboWhere.addItem(new ComboItem("Europa", "EU"));
		comboWhere.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ComboItem selectedItem = (ComboItem) e.getItem();
                    comboBox_where_value = selectedItem.getValue();
                    System.out.println("Valore selezionato: " + comboBox_where_value);
                }
            }

        });
		
    	lbl_combowhere.setVisible(false);
    	comboWhere.setVisible(false);
		
		lblNewLabel_5 = new JLabel("N max seller:");
		add(lblNewLabel_5, "cell 5 16,alignx left");
		lblNewLabel_5.setVisible(false);
		
		add(comboWhere, "cell 1 17 3 1,growx");
		
		combo_maxseller = new JComboBox<ComboItem>();
		combo_maxseller.addItem(new ComboItem("10", "10"));
		combo_maxseller.addItem(new ComboItem("25", "25"));
		combo_maxseller.addItem(new ComboItem("50", "50"));
		combo_maxseller.addItem(new ComboItem("100", "100"));
		combo_maxseller.addItem(new ComboItem("200", "200"));
		combo_maxseller.addItem(new ComboItem("500", "500"));
		combo_maxseller.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ComboItem selectedItem = (ComboItem) e.getItem();
                    comboBox_maxseller_value = selectedItem.getValue();
                    System.out.println("Valore selezionato: " + comboBox_maxseller_value);
                }
            }

        });
		
		combo_maxseller.setVisible(false);
		
		add(combo_maxseller, "cell 5 17,growx");
		
		btnNewButton = new JButton("Elabora");
		btnNewButton.setVisible(true);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				if(checkBeforeRun()) {

					logger.info("Inizio elaborazione");
					elabora();
				}

			}
		});
		
		chckAggiorna = new JCheckBox("Utilizza dati ultima estrazione");
		add(chckAggiorna, "cell 1 22");
		chckAggiorna.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				chckAggiorna_value = !chckAggiorna.isSelected();
			}
		});
		
		add(btnNewButton, "flowx,cell 1 23,alignx center");
		
		btnOpenRes = new JButton("Apri risultato");
		btnOpenRes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				new CsvEditorFrame("res",new File(textFieldOutputFolder.getText()+File.separator+ParserPath.getNameWithoutExtension(textFieldInputFile.getText())+".csv"),false).setVisible(true);
			}
		});
		add(btnOpenRes, "cell 5 23");
		
		msg_label = new JLabel("");
		add(msg_label, "cell 1 25 7 1,alignx center,aligny center");
		
		textField_minqty = new JTextField();
		textField_minqty.setEditable(false);
		textField_minqty.setText("0");
		add(textField_minqty, "cell 5 14,growx");
		textField_minqty.setColumns(10);

	}
	
	private void setInputFile(JTextField textpath) {
		

	    JFileChooser fileChooser = new JFileChooser("C:\\Tmp");
	    int returnValue = fileChooser.showOpenDialog(null);
	    if (returnValue == JFileChooser.APPROVE_OPTION)
	    {
	        File selectedFile = fileChooser.getSelectedFile();
				
				textpath.setText(selectedFile.getPath());
	    }
	}
	
	private void setOutputFolder(JTextField textpath) {
		

	    JFileChooser fileChooser = new JFileChooser("C:\\Tmp");
	    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    int returnValue = fileChooser.showOpenDialog(null);
	    if (returnValue == JFileChooser.APPROVE_OPTION)
	    {
	        File selectedFile = fileChooser.getSelectedFile();
				
				textpath.setText(selectedFile.getPath());
	    }
	}
	
	private void aggiornaDefault() {
		
		InputDefaultLoader.set_INPUT_XML(textFieldInputFile.getText());
		InputDefaultLoader.set_INPUT_CSV_FOLDER(textFieldOutputFolder.getText());

		InputDefaultLoader.saveProperties();
	}
	
	private void elabora() {
		
		aggiornaDefault();
		
		new Parser(comboBox_seller_value,textFieldInputFile.getText(),textFieldOutputFolder.getText(),
				chckAggiorna_value,comboBox_maxseller_value,comboBox_where_value,textField_minqty.getText(),comboBox_type_value,chckbx_Lego_PaB_value,null,null);
		
		msg_label.setText("Elaborazione Terminata correttamente");
	}
	
	public String getNameWithoutExtension(String file) {
		
			File input = new File(file);
			String name = input.getName();
		
		   int dotIndex = name.lastIndexOf('.');
		   return (dotIndex == -1) ? name : name.substring(0, dotIndex);
	}
	
	private boolean checkBeforeRun() {
		

		logger.info("check before start elab");
		msg_label.setText("");
		msg_label.paintImmediately(msg_label.getVisibleRect());
		
		
		if(textFieldInputFile.getText().equals("*.xml") || textFieldInputFile.getText().trim().isEmpty()) {
			logger.info("Selezione File: KO");
			msg_label.setText("Selezionare un file da analizzare");
			return false;
		}
		else {
			logger.info("Selezione File: OK");
		}
		
		if(!textFieldInputFile.getText().substring(textFieldInputFile.getText().length()-4,textFieldInputFile.getText().length()).equals(".xml")) {
			logger.info("Controllo estensione: KO");
			msg_label.setText("Estensione file non supportata");
			return false;
		}
		else {
			logger.info("Controllo estensione: OK");
		}
		
		msg_label.setText("Elaborazione in corso...");
		msg_label.paintImmediately(msg_label.getVisibleRect());
		return true;
	}

	public class ComboItem {
	    private String label;
	    private String value;

	    public ComboItem(String label, String value) {
	        this.label = label;
	        this.value = value;
	    }

	    @Override
	    public String toString() {
	        return label; // visualizzato nel JComboBox
	    }

	    public String getValue() {
	        return value;
	    }

	    public String getLabel() {
	        return label;
	    }
	}

	
}
