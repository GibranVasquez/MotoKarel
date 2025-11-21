package interfaz;

import codigo.Command;
import codigo.CommandParser;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MainWindow extends JFrame {

    private JTextArea editorArea;
    private JTextArea tripletsArea;
    private JTextArea consoleArea;

    private PanelMoto panelMoto;

    private JButton btnAnalizar;
    private JButton btnEjecutar;
    private JButton btnLimpiar;

    public MainWindow() {
        super("Simulador Moto — Lenguaje Karel-like");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Ajuste general de tamaño
        setSize(1450, 900);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {

        JSplitPane root = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Aumentado para más espacio en la columna izquierda
        root.setResizeWeight(0.60);
        root.setDividerSize(10);

        // -------------------------------------------
        // PANEL IZQUIERDO (Editor / Tripletas / Consola)
        // -------------------------------------------
        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // ----- Editor -----
        editorArea = new JTextArea();
        editorArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        editorArea.setText(sampleCode());

        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBorder(BorderFactory.createTitledBorder("Editor de código"));
        editorPanel.add(new JScrollPane(editorArea), BorderLayout.CENTER);
        editorPanel.setPreferredSize(new Dimension(0, 240));   // <-- Más grande

        // ----- Tripletas -----
        tripletsArea = new JTextArea();
        tripletsArea.setEditable(false);
        tripletsArea.setFont(new Font("Consolas", Font.PLAIN, 13));

        JPanel tripletsPanel = new JPanel(new BorderLayout());
        tripletsPanel.setBorder(BorderFactory.createTitledBorder("Tripletas generadas"));
        tripletsPanel.add(new JScrollPane(tripletsArea), BorderLayout.CENTER);
        tripletsPanel.setPreferredSize(new Dimension(0, 240)); // <-- Más grande

        // ----- Consola -----
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font("Consolas", Font.PLAIN, 13));

        JPanel consolePanel = new JPanel(new BorderLayout());
        consolePanel.setBorder(BorderFactory.createTitledBorder("Consola de mensajes"));
        consolePanel.add(new JScrollPane(consoleArea), BorderLayout.CENTER);
        consolePanel.setPreferredSize(new Dimension(0, 240));  // <-- Más grande

        left.add(editorPanel, BorderLayout.NORTH);
        left.add(tripletsPanel, BorderLayout.CENTER);
        left.add(consolePanel, BorderLayout.SOUTH);

        // -------------------------------------------
       
       // PANEL DERECHO (Simulación + botones)
JPanel right = new JPanel(new BorderLayout());

// Aumentamos el margen izquierdo para recorrer todo el panel a la derecha.
// Puedes ajustar el valor 80 → más grande o más pequeño según lo que necesites.
right.setBorder(BorderFactory.createEmptyBorder(2, 100, 2, 2));

panelMoto = new PanelMoto();
right.add(panelMoto, BorderLayout.CENTER);


        // ----- Botones -----
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));

        btnAnalizar = new JButton("Analizar");
        btnEjecutar = new JButton("Ejecutar");
        btnLimpiar = new JButton("Limpiar");

        Dimension btnSize = new Dimension(130, 38);
        btnAnalizar.setPreferredSize(btnSize);
        btnEjecutar.setPreferredSize(btnSize);
        btnLimpiar.setPreferredSize(btnSize);

        btnPanel.add(btnAnalizar);
        btnPanel.add(btnEjecutar);
        btnPanel.add(btnLimpiar);

        right.add(btnPanel, BorderLayout.SOUTH);

        // Unimos paneles
        root.setLeftComponent(left);
        root.setRightComponent(right);

        setContentPane(root);

        // -------------------------------------------
        // ACCIONES DE BOTONES
        // -------------------------------------------
        btnAnalizar.addActionListener(e -> doAnalyze());
        btnEjecutar.addActionListener(e -> doGenerateAndRun());
        btnLimpiar.addActionListener(e -> doClear());
    }

    // --------------------------
    // ANALIZAR
    // --------------------------
    private void doAnalyze() {
        consoleArea.setText("");
        tripletsArea.setText("");
        try {
            CommandParser.Result res = CommandParser.parseAndGenerate(editorArea.getText());
            tripletsArea.setText(String.join("\n", res.triplets));
            consoleArea.append("✔ Análisis correcto. " + res.commands.size() + " comandos.\n");
        } catch (CommandParser.ParseException ex) {
            consoleArea.append("❌ Error de sintaxis: " + ex.getMessage() + "\n");
        } catch (Exception ex) {
            consoleArea.append("❌ Error inesperado: " + ex.getMessage() + "\n");
        }
    }

    // --------------------------
    // EJECUTAR
    // --------------------------
    private void doGenerateAndRun() {
        consoleArea.setText("");
        tripletsArea.setText("");
        try {
            CommandParser.Result res = CommandParser.parseAndGenerate(editorArea.getText());
            tripletsArea.setText(String.join("\n", res.triplets));

            Consumer<String> notifier =
                    msg -> SwingUtilities.invokeLater(() -> consoleArea.append(msg + "\n"));

            panelMoto.runCommands(res.commands, notifier);

            consoleArea.append("✔ Simulación iniciada.\n");
        } catch (CommandParser.ParseException ex) {
            consoleArea.append("❌ Error de sintaxis: " + ex.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --------------------------
    // LIMPIAR
    // --------------------------
    private void doClear() {
        editorArea.setText("");
        tripletsArea.setText("");
        consoleArea.setText("");
        panelMoto.resetWorld();
    }

    private String sampleCode() {
        return """
                mover 5;
                girar derecha;

                repetir 3 {
                    mover 2;
                    girar izquierda;
                };
                """;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow w = new MainWindow();
            w.setVisible(true);
        });
    }
}
