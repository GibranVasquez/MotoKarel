package interfaz;

import codigo.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PanelMoto extends JPanel {

    private final int GRID = 40;
    private final int COLS = 18;
    private final int ROWS = 17;

    private int col = 2;
    private int row = 2;
    private int orient = 1; // 1 = Este (Derecha, X+)

    private double px = col * GRID;
    private double py = row * GRID;

    private String[] motoURLs = {
        "https://s7g10.scene7.com/is/image/ktm/KTM-motocross-4-stroke-250-sxf-right-side-view?fmt=png-alpha&wid=1000&dpr=off"
    };

    private Image motoImg;
    private Image treeImg, rockImg, logImg;

    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Runnable> actions = new ArrayList<>();

    private final Timer ticker;
    private Consumer<String> notifier;
    private Consumer<List<String>> tripletsConsumer;

    private final Color GRID_COLOR = new Color(220, 220, 220);
    private final Color AXIS_COLOR = new Color(80, 80, 80);
    private final Color INFO_COLOR = new Color(0, 80, 160);
    private final Color OBSTACLE_BORDER = new Color(100, 100, 100);

    public PanelMoto() {
        setLayout(null);
        int marginLeft = 40;
        int marginBottom = 40;
        int panelWidth = COLS * GRID + marginLeft + 140;
        int panelHeight = ROWS * GRID + marginBottom + 40;
        setBounds(380, 10, panelWidth, panelHeight);
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        loadSprites();
        createDefaultObstacles();

        ticker = new Timer(50, e -> {
            if (!actions.isEmpty()) {
                Runnable r = actions.remove(0);
                r.run();
                repaint();
            }
        });
    }

    public void setTripletsConsumer(Consumer<List<String>> tripletsConsumer) {
        this.tripletsConsumer = tripletsConsumer;
    }

    private void loadSprites() {
        motoImg = loadImageFromURLs(motoURLs);
        
        if (motoImg == null) {
            motoImg = createDefaultMotoImage();
            System.out.println("Usando imagen por defecto para la moto");
        } else {
            System.out.println("Imagen de moto cargada correctamente");
        }

        try { 
            treeImg = new ImageIcon(new URL("https://i.pinimg.com/736x/0c/d9/8c/0cd98c19e11c7a5221005a7675444e14.jpg")).getImage(); 
            treeImg = treeImg.getScaledInstance(GRID, GRID, Image.SCALE_SMOOTH);
        } catch (Exception e) { 
            treeImg = createDefaultObstacleImage(new Color(34, 139, 34), "Árbol");
        }
        
        try { 
            rockImg = new ImageIcon(new URL("https://img.freepik.com/psd-gratis/formacion-rocosa-dibujada-mano_23-2151544049.jpg")).getImage(); 
            rockImg = rockImg.getScaledInstance(GRID, GRID, Image.SCALE_SMOOTH);
        } catch (Exception e) { 
            rockImg = createDefaultObstacleImage(new Color(128, 128, 128), "Roca");
        }
        
        try { 
            logImg = new ImageIcon(new URL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSxfJyLHc8HH-bt-y3I5PuROuoZJdfdLngbHfJHUiJqK51fRJkkjLQsBa7CYzpVF1Xwrts&usqp=CAU")).getImage(); 
            logImg = logImg.getScaledInstance(GRID, GRID, Image.SCALE_SMOOTH);
        } catch (Exception e) { 
            logImg = createDefaultObstacleImage(new Color(139, 69, 19), "Tronco");
        }
    }

    private Image loadImageFromURLs(String[] urls) {
        for (String url : urls) {
            try {
                ImageIcon icon = new ImageIcon(new URL(url));
                Image img = icon.getImage();
                if (img != null && icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    return img.getScaledInstance(GRID, GRID, Image.SCALE_SMOOTH);
                }
            } catch (Exception ex) {
                System.out.println("Error cargando imagen: " + ex.getMessage());
            }
        }
        return null;
    }

    private Image createDefaultMotoImage() {
        BufferedImage img = new BufferedImage(GRID, GRID, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, GRID, GRID);
        
        GradientPaint gp = new GradientPaint(0, 15, new Color(220, 20, 60), 0, 25, new Color(178, 34, 34));
        g2d.setPaint(gp);
        g2d.fillRoundRect(5, 15, 30, 10, 8, 8);
        
        g2d.setColor(new Color(40, 40, 40));
        g2d.fillOval(8, 8, 8, 8);
        g2d.fillOval(24, 8, 8, 8);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(8, 8, 8, 8);
        g2d.drawOval(24, 8, 8, 8);
        
        g2d.setColor(new Color(30, 144, 255));
        g2d.fillRoundRect(15, 10, 10, 5, 4, 4);
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(30, 12, 5, 2);
        
        g2d.dispose();
        return img;
    }

    private Image createDefaultObstacleImage(Color color, String text) {
        BufferedImage img = new BufferedImage(GRID, GRID, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(color.darker());
        g2d.fillRoundRect(2, 2, GRID-4, GRID-4, 8, 8);
        g2d.setColor(color);
        g2d.fillRoundRect(4, 4, GRID-8, GRID-8, 6, 6);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (GRID - textWidth) / 2;
        int y = (GRID - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return img;
    }

    private void createDefaultObstacles() {
        obstacles.clear();
        obstacles.add(new Obstacle(6, 4, ObType.ROCK));
        obstacles.add(new Obstacle(8, 6, ObType.LOG));
        obstacles.add(new Obstacle(10, 10, ObType.TREE));
        obstacles.add(new Obstacle(15, 3, ObType.ROCK));
        obstacles.add(new Obstacle(4, 12, ObType.TREE));
        obstacles.add(new Obstacle(12, 8, ObType.LOG));
        obstacles.add(new Obstacle(16, 12, ObType.LOG));
        obstacles.add(new Obstacle(14, 5, ObType.LOG));
        obstacles.add(new Obstacle(2, 15, ObType.ROCK));
    }

   // EJECUCIÓN COMANDOS - MODIFICADO PARA SEPARAR ANÁLISIS DE EJECUCIÓN
    public void runCommands(List<Command> commands, Consumer<String> notifier, boolean ejecutar) {
        this.notifier = notifier;
        
        if (ejecutar) {
            // Modo ejecución: ejecuta los comandos
            actions.clear();
            
            if (!ticker.isRunning()) {
                ticker.start();
            }

            // Ejecutar comandos
            for (Command c : commands) {
                switch (c.type) {
                    case MOVE -> addMoveSteps(c.value);
                    case TURN -> addTurnSteps(c.dir);
                    case REPEAT -> {
                        for (int i = 0; i < c.value; i++) {
                            for (Command sc : c.body) {
                                if (sc.type == Command.Type.MOVE)
                                    addMoveSteps(sc.value);
                                else if (sc.type == Command.Type.TURN)
                                    addTurnSteps(sc.dir);
                            }
                        }
                    }
                }
            }

            if (notifier != null)
                notifier.accept("✔ Comandos listos para ejecutarse");
        } else {
            // Modo análisis: solo genera tripletas
            if (notifier != null)
                notifier.accept("✔ Análisis completado - Tripletas generadas");
        }
    }

    // MÉTODO SEPARADO SOLO PARA GENERAR TRIPLETAS (para el botón Analizar)
    public void analizarComandos(List<Command> commands) {
        // Generar tripletas
        List<String> tripletas = generateTriplets(commands);
        
        if (tripletsConsumer != null) {
            tripletsConsumer.accept(tripletas);
        }
    }

    // MÉTODO PARA EJECUTAR COMANDOS (para el botón Ejecutar)
    public void ejecutarComandos(List<Command> commands, Consumer<String> notifier) {
        runCommands(commands, notifier, true);
    }
    
    private List<String> generateTriplets(List<Command> commands) {
        List<String> tripletas = new ArrayList<>();
        int lineNumber = 1;
        int blockCounter = 1;
        
        for (Command c : commands) {
            switch (c.type) {
                case MOVE -> {
                    tripletas.add("(" + lineNumber + ") (MOVER, -, " + c.value + ")");
                    lineNumber++;
                }
                case TURN -> {
                    String direction = c.dir.equalsIgnoreCase("derecha") ? "DERECHA" : "IZQUIERDA";
                    tripletas.add("(" + lineNumber + ") (GIRAR, -, " + direction + ")");
                    lineNumber++;
                }
                case REPEAT -> {
                    String blockName = "BLOQUE" + blockCounter;
                    blockCounter++;
                    
                    tripletas.add("(" + lineNumber + ") (REPETIR, " + c.value + ", " + blockName + ")");
                    lineNumber++;
                    
                    tripletas.add("(" + lineNumber + ") (ETQ, " + blockName + ", -)");
                    lineNumber++;
                    
                    for (Command sc : c.body) {
                        if (sc.type == Command.Type.MOVE) {
                            tripletas.add("(" + lineNumber + ") (MOVER, -, " + sc.value + ")");
                        } else if (sc.type == Command.Type.TURN) {
                            String scDirection = sc.dir.equalsIgnoreCase("derecha") ? "DERECHA" : "IZQUIERDA";
                            tripletas.add("(" + lineNumber + ") (GIRAR, -, " + scDirection + ")");
                        }
                        lineNumber++;
                    }
                    
                    tripletas.add("(" + lineNumber + ") (FIN_REPETIR, -, " + blockName + ")");
                    lineNumber++;
                }
            }
        }
        
        return tripletas;
    }

    public void resetWorld() {
        col = 2;
        row = 2;
        orient = 1; // Reiniciar a Este (X+)
        px = col * GRID;
        py = row * GRID;
        actions.clear();
        if (notifier != null) notifier.accept("✔ Mundo reiniciado");
        repaint();
    }

    // MOVIMIENTO - SISTEMA INVERTIDO (Y se convierte en X y viceversa)
    private void addMoveSteps(int cells) {
        for (int s = 0; s < cells; s++) {
            // Verificación de colisión ANTES de mover
            actions.add(() -> {
                int tcol = col, trow = row;

                switch (orient) {
                    case 0 -> tcol--;   // NORTE (X-)
                    case 1 -> trow++;   // ESTE (Y+)
                    case 2 -> tcol++;   // SUR (X+)
                    case 3 -> trow--;   // OESTE (Y-)
                }

                if (isBlocked(tcol, trow)) {
                    actions.clear();
                    if (notifier != null) notifier.accept("❌ Movimiento bloqueado en (" + tcol + "," + trow + ")");
                    return;
                }
            });

            // Animación del movimiento
            for (int step = 0; step < GRID; step += 4) {
                actions.add(() -> {
                    switch (orient) {
                        case 0 -> px -= 4; // NORTE (X-)
                        case 1 -> py += 4; // ESTE (Y+)
                        case 2 -> px += 4; // SUR (X+)
                        case 3 -> py -= 4; // OESTE (Y-)
                    }
                });
            }

            // Actualización de posición después del movimiento
            actions.add(() -> {
                switch (orient) {
                    case 0 -> col--; // NORTE (X-)
                    case 1 -> row++; // ESTE (Y+)
                    case 2 -> col++; // SUR (X+)
                    case 3 -> row--; // OESTE (Y-)
                }
                px = col * GRID;
                py = row * GRID;
                
                if (notifier != null) 
                    notifier.accept("✓ Movido a (" + col + "," + row + ") - Dirección: " + orientToText());
            });
        }
    }

    // GIROS CORREGIDOS - SENTIDO HORARIO/ANTIHORARIO
    private void addTurnSteps(String dir) {
        // Pausa para la animación
        actions.add(() -> {
            try { Thread.sleep(300); } catch (InterruptedException e) {}
        });
        
        // Ejecutar el giro
        actions.add(() -> {
            int oldOrient = orient;
            if (dir.equalsIgnoreCase("derecha")) {
                // Giro a la derecha: sentido horario
                orient = (orient + 1) % 4;
                if (notifier != null) 
                    notifier.accept("✓ Girado derecha: " + orientToText(oldOrient) + " → " + orientToText());
            } else {
                // Giro a la izquierda: sentido antihorario  
                orient = (orient + 3) % 4;
                if (notifier != null) 
                    notifier.accept("✓ Girado izquierda: " + orientToText(oldOrient) + " → " + orientToText());
            }
        });
    }

    private boolean isBlocked(int c, int r) {
        // Verificar límites del mundo
        if (c < 0 || r < 0 || c >= COLS || r >= ROWS) {
            if (notifier != null)
                notifier.accept("❌ Límite del mundo en (" + c + "," + r + ")");
            return true;
        }

        // Verificar obstáculos
        for (Obstacle o : obstacles) {
            if (o.col == c && o.row == r) {
                if (notifier != null)
                    notifier.accept("❌ Obstáculo en (" + c + "," + r + ")");
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int marginLeft = 40;
        int marginBottom = 40;
        int baseY = ROWS * GRID;
        int totalHeight = baseY + marginBottom;

        // Fondo
        GradientPaint bgGradient = new GradientPaint(0, 0, new Color(245, 245, 245), 
                                                    getWidth(), getHeight(), Color.WHITE);
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Área del grid
        g2d.setColor(new Color(250, 250, 250));
        g2d.fillRect(marginLeft, 0, COLS * GRID, baseY);

        // Cuadrícula
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(1.0f));
        
        for (int x = 0; x <= COLS * GRID; x += GRID) {
            g2d.drawLine(marginLeft + x, 0, marginLeft + x, baseY);
        }
        for (int y = 0; y <= ROWS * GRID; y += GRID) {
            g2d.drawLine(marginLeft, y, marginLeft + COLS * GRID, y);
        }

        // Ejes
        g2d.setColor(AXIS_COLOR);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(marginLeft, 0, marginLeft, baseY);
        g2d.drawLine(marginLeft, baseY, marginLeft + COLS * GRID, baseY);

        // Obstáculos
        for (Obstacle o : obstacles) {
            int drawX = marginLeft + o.col * GRID;
            int drawY = baseY - (o.row * GRID) - GRID;

            Image img = switch (o.type) {
                case TREE -> treeImg;
                case ROCK -> rockImg;
                default -> logImg;
            };

            if (img != null) {
                g2d.drawImage(img, drawX, drawY, GRID, GRID, this);
            }
            
            g2d.setColor(OBSTACLE_BORDER);
            g2d.drawRect(drawX, drawY, GRID, GRID);
        }

        // Numeración
        g2d.setColor(AXIS_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));

        for (int x = 0; x < COLS; x++) {
            String num = String.valueOf(x);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(num);
            int posX = marginLeft + x * GRID + (GRID - textWidth) / 2;
            int posY = baseY + 20;
            g2d.drawString(num, posX, posY);
            g2d.fillRect(marginLeft + x * GRID, baseY - 3, 1, 6);
        }

        for (int y = 0; y < ROWS; y++) {
            String num = String.valueOf(y);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(num);
            int textHeight = fm.getHeight();
            int posX = marginLeft - textWidth - 8;
            int posY = baseY - (y * GRID) - (GRID / 2) + (textHeight / 3);
            g2d.drawString(num, posX, posY);
            g2d.fillRect(marginLeft - 3, baseY - (y * GRID), 6, 1);
        }

        // Etiquetas de ejes
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Y", 15, baseY / 2);
        g2d.drawString("X", marginLeft + (COLS * GRID) / 2, baseY + 35);

        // Moto - posición corregida
        int motoX = marginLeft + (int) px;
        int motoY = baseY - (int) py - GRID;

        if (motoImg != null) {
            Graphics2D motoG2d = (Graphics2D) g2d.create();
            int centerX = motoX + GRID / 2;
            int centerY = motoY + GRID / 2;

            // ROTACIÓN CORREGIDA - SISTEMA INVERTIDO
            double angle = switch (orient) {
                case 0 -> Math.toRadians(180);  // NORTE: 180° (hacia X negativo)
                case 1 -> Math.toRadians(-90);  // ESTE: -90° (hacia Y positivo)
                case 2 -> Math.toRadians(0);    // SUR: 0° (hacia X positivo)
                case 3 -> Math.toRadians(90);   // OESTE: 90° (hacia Y negativo)
                default -> Math.toRadians(0);
            };

            motoG2d.rotate(angle, centerX, centerY);
            motoG2d.drawImage(motoImg, motoX, motoY, GRID, GRID, this);
            motoG2d.dispose();
        } else {
            GradientPaint motoGradient = new GradientPaint(motoX, motoY, Color.RED, 
                                                         motoX + GRID, motoY + GRID, Color.ORANGE);
            g2d.setPaint(motoGradient);
            g2d.fillRoundRect(motoX, motoY, GRID, GRID, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            
            String dirSymbol = switch (orient) {
                case 0 -> "↑";
                case 1 -> "→";
                case 2 -> "↓";
                default -> "←";
            };
            g2d.drawString(dirSymbol + "MOTO", motoX + 5, motoY + 22);
        }

        g2d.setColor(Color.BLACK);
        g2d.drawRect(motoX, motoY, GRID, GRID);

        // PANEL DE INFORMACIÓN DE LA MOTO
        int infoX = marginLeft + COLS * GRID + 10;
        int infoY = 20;
        
        // Fondo del panel de información
        g2d.setColor(new Color(240, 245, 255));
        g2d.fillRoundRect(infoX - 5, infoY - 5, 180, 100, 10, 10);
        g2d.setColor(new Color(200, 220, 255));
        g2d.drawRoundRect(infoX - 5, infoY - 5, 180, 100, 10, 10);
        
        g2d.setColor(INFO_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Estado Moto", infoX, infoY + 15);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Posición: X = " + col + ", Y = " + row, infoX, infoY + 35);
        g2d.drawString("Dirección: " + orientToText(), infoX, infoY + 50);
        g2d.drawString("Acciones pendientes: " + actions.size(), infoX, infoY + 80);
    }

    private String orientToText() {
        return switch (orient) {
            case 0 -> "↑ Norte (X-)";
            case 1 -> "→ Este (Y+)";
            case 2 -> "↓ Sur (X+)";
            default -> "← Oeste (Y-)";
        };
    }

    // Método sobrecargado para mostrar cambios de orientación
    private String orientToText(int orientation) {
        return switch (orientation) {
            case 0 -> "↑ Norte (X-)";
            case 1 -> "→ Este (Y+)";
            case 2 -> "↓ Sur (X+)";
            default -> "← Oeste (Y-)";
        };
    }

    private enum ObType { ROCK, TREE, LOG }

    private static class Obstacle {
        int col, row;
        ObType type;
        Obstacle(int c, int r, ObType t) { col = c; row = r; type = t; }
    }
    
    public void stopExecution() {
        actions.clear();
        ticker.stop();
        if (notifier != null) notifier.accept("⏹ Ejecución detenida");
    }

    // MÉTODO PARA ESTABLECER POSICIÓN MANUALMENTE (para testing)
    public void setPosition(int x, int y, int direction) {
        this.col = x;
        this.row = y;
        this.orient = direction;
        this.px = col * GRID;
        this.py = row * GRID;
        repaint();
        if (notifier != null) notifier.accept("✓ Posición establecida: (" + x + "," + y + ") - " + orientToText());
    }
}