import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class PropertiesEditorFrame extends JFrame {
    private final File propertiesFile;
    private final JPanel contentPanel;
    private final Map<String, JTextField> fields = new LinkedHashMap<>();

    public PropertiesEditorFrame(String title, File file) {
        super(title);
        this.propertiesFile = file;

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
        add(createBottomBar(), BorderLayout.SOUTH);

        caricaProperties();
        impostaFrame();
    }

    private void caricaProperties() {
        try (FileReader reader = new FileReader(propertiesFile)) {
            Properties props = new Properties();
            props.load(reader);

            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);

                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel label = new JLabel(key);
                label.setPreferredSize(new Dimension(200, 25));

                JTextField textField = new JTextField(value, 40);

                row.add(label);
                row.add(textField);

                fields.put(key, textField);
                contentPanel.add(row);
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore nella lettura del file: " + e.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createBottomBar() {
        JButton saveButton = new JButton("Salva");
        saveButton.addActionListener(e -> salvaProperties());

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bar.add(saveButton);
        return bar;
    }

    private void salvaProperties() {
        Properties props = new Properties();
        for (Map.Entry<String, JTextField> entry : fields.entrySet()) {
            props.setProperty(entry.getKey(), entry.getValue().getText().trim());
        }

        try (FileWriter writer = new FileWriter(propertiesFile)) {
            props.store(writer, "Aggiornato da PropertiesEditorFrame");
            JOptionPane.showMessageDialog(this, "File salvato con successo!");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore nel salvataggio: " + e.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void impostaFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        File file = new File("config/config.properties"); // Modifica con il tuo percorso
        SwingUtilities.invokeLater(() -> new PropertiesEditorFrame("Editor Properties", file));
    }
}
