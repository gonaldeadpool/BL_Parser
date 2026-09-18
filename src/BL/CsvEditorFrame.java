package BL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class CsvEditorFrame extends JFrame {
    private JPanel panel;
    private ArrayList<JPanel> rows = new ArrayList<>();
    private File csvFile;
    private int numColonne = 0;
    private String headerLine = null;
    private boolean checkBoxEditable;
    private JScrollPane scrollPane;
    private JPanel headerPanel;

    public CsvEditorFrame(String title, File file, boolean checkBoxEditable) {
        this.csvFile = file;
        this.checkBoxEditable = checkBoxEditable;
        setTitle(title);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Calcolo altezza approssimativa di una riga
        int approxRowHeight = 40;
        scrollPane.setPreferredSize(new Dimension(800, approxRowHeight * 10));

        JButton addButton = new JButton("Aggiungi riga");
        addButton.addActionListener(e -> aggiungiRigaVuota());

        JButton saveButton = new JButton("Salva");
        saveButton.addActionListener(e -> salvaCSV());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(saveButton);

        headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(230, 230, 230));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(scrollPane, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.CENTER);

        caricaCSV();
        ridimensionaFrame();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void caricaCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#$")) continue;

                if (line.startsWith("#!")) {
                    headerLine = line.substring(2);
                    String[] headerValues = headerLine.split(";");
                    numColonne = Math.max(numColonne, headerValues.length);
                    aggiungiIntestazioneVisuale(headerValues);
                    continue;
                }

                boolean selected = !line.startsWith("#");
                if (!selected) line = line.substring(1);

                String[] values = line.split(";");
                numColonne = Math.max(numColonne, values.length);
                aggiungiRiga(values, selected);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void aggiungiIntestazioneVisuale(String[] valori) {
        headerPanel.removeAll();
        headerPanel.add(Box.createRigidArea(new Dimension(20, 0))); // checkbox placeholder
        headerPanel.add(Box.createRigidArea(new Dimension(20, 0))); // icona placeholder

        for (String val : valori) {
            JLabel label = new JLabel(val);
            label.setPreferredSize(new Dimension(150, 25));
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            headerPanel.add(label);
        }
    }

    private void aggiungiRiga(String[] values, boolean selected) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(selected);
        checkBox.setEnabled(checkBoxEditable);
        rowPanel.add(checkBox);

        JLabel deleteLabel = new JLabel();
        deleteLabel.setIcon(new ImageIcon(getClass().getClassLoader().getResource("trash.png")));
        deleteLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rowPanel.add(deleteLabel);

        for (String val : values) {
            JTextField textField = new JTextField(val, 15);
            rowPanel.add(textField);
        }

        deleteLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                panel.remove(rowPanel);
                rows.remove(rowPanel);
                panel.revalidate();
                panel.repaint();
                ridimensionaFrame();
            }
        });

        rows.add(rowPanel);
        panel.add(rowPanel);
    }

    private void aggiungiRigaVuota() {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(true);
        checkBox.setEnabled(checkBoxEditable);
        rowPanel.add(checkBox);

        JLabel deleteLabel = new JLabel();
        deleteLabel.setIcon(new ImageIcon(getClass().getClassLoader().getResource("trash.png")));
        deleteLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rowPanel.add(deleteLabel);

        ArrayList<JTextField> campi = new ArrayList<>();
        for (int i = 0; i < numColonne; i++) {
            JTextField textField = new JTextField(15);
            rowPanel.add(textField);
            campi.add(textField);
        }

        deleteLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                panel.remove(rowPanel);
                rows.remove(rowPanel);
                panel.revalidate();
                panel.repaint();
                ridimensionaFrame();
            }
        });

        rows.add(rowPanel);
        panel.add(rowPanel);
        panel.revalidate();
        panel.repaint();

        SwingUtilities.invokeLater(() -> {
            Rectangle bounds = rowPanel.getBounds();
            panel.scrollRectToVisible(bounds);
            if (!campi.isEmpty()) {
                campi.get(0).requestFocusInWindow();
            }
            ridimensionaFrame();
        });
    }

    private void ridimensionaFrame() {
        int columnWidth = 200;
        int baseWidth = 40; // spazio per checkbox e icona
        int totalWidth = baseWidth + (numColonne * columnWidth);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxWidth = (int)(screenSize.width * 0.9);
        int width = Math.min(totalWidth, maxWidth);

        int height = scrollPane.getPreferredSize().height + 120;
        setSize(new Dimension(width, height));
    }

    private void salvaCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            if (headerLine != null) {
                writer.write("#!" + headerLine);
                writer.newLine();
            }

            for (JPanel rowPanel : rows) {
                Component[] components = rowPanel.getComponents();
                JCheckBox checkBox = (JCheckBox) components[0];

                ArrayList<String> campi = new ArrayList<>();
                for (int i = 2; i < components.length; i++) {
                    if (components[i] instanceof JTextField textField) {
                        campi.add(textField.getText().trim());
                    }
                }

                String riga = String.join(";", campi);
                if (!checkBox.isSelected()) {
                    riga = "#" + riga;
                }
                writer.write(riga);
                writer.newLine();
            }

            JOptionPane.showMessageDialog(this, "CSV salvato con successo!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final String CURRENCY_PATH = "in/cambio_valute.csv";
        SwingUtilities.invokeLater(() -> {
            File file = new File(CURRENCY_PATH);
            new CsvEditorFrame("Prova", file, true).setVisible(true);
        });
    }
}
