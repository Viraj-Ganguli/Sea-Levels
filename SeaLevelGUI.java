import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

public class SeaLevelGUI {
    private SeaLevelVisualizer display;

    private JFrame window;

    private JTextField heightInput;

    private JLabel statusLine;

    private JPanel controlPanel;

    private Terrain terrain = null;

    private String terrainFile = "terrains/SouthBayArea.terrain";

    /* Which directory has the terrain files. */
    private static final File TERRAIN_DIRECTORY = new File("terrains");

    /* Makes the "Go!" button that makes the magic happen. */
    private JButton makeGoButton() {
        var result = new JButton("Go!");
        result.addActionListener((ActionEvent e) -> {
            File f = new File(terrainFile);
            runSimulation(f);
        });
        return result;
    }

    /* Builds the control panel. */
    private JPanel makeControlPanel() {
        JPanel container = new JPanel();
        container.setLayout(new GridLayout(2, 1));

        /* The main control panel. */
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        /* Spacer. */
        panel.add(new JLabel("          "));

        heightInput = new JTextField("0.0", 8);
        panel.add(new JLabel("Water Height: "));
        panel.add(heightInput);

        var goButton = makeGoButton();
        panel.add(goButton);

        container.add(panel);

        /* The status line. */
        JPanel statusBox = new JPanel();
        statusLine = new JLabel("");
        statusBox.add(statusLine);
        container.add(statusBox);

        return container;
    }

    private SeaLevelGUI() {
        /* Main window. */
        window = new JFrame();
        window.setLayout(new BorderLayout());
        window.setTitle("South San Francisco Bay Water Levels");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* Main display. */
        display = new SeaLevelVisualizer();
        window.add(display, BorderLayout.CENTER);

        /* Control panel. */
        controlPanel = makeControlPanel();
        window.add(controlPanel, BorderLayout.NORTH);

        window.pack();
        window.setVisible(true);
        initialLoad();
    }

    private void setStatusLine(final String text) {
        SwingUtilities.invokeLater(() -> {
            statusLine.setText(text);
        });
    }

    /*
     * Disables/enables all components in the given container. Taken from
     * https://stackoverflow.com/questions/10985734/java-swing-enabling-disabling-
     * all-components-in-jpanel
     */
    private void setEnabled(Container container, boolean enabled) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enabled);
            if (component instanceof Container) {
                setEnabled((Container) component, enabled);
            }
        }
    }

    private void initialLoad() {
        File f = new File(terrainFile);
        runSimulation(f);
    }

    /* Fires off the simulation based on the configuration. */
    private void runSimulation(File terrainFile) {
        double waterHeight;
        try {
            waterHeight = Double.parseDouble(heightInput.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(window, "Please enter a number for the water height.", "Water Height",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        setEnabled(controlPanel, false);
        new Thread() {
            public void run() {
                try {
                    /* Did the terrain change? */

                    setStatusLine("Loading the Terrain...");
                    terrain = TerrainLoader.loadTerrain(terrainFile, (int bytes, int total) -> {
                        int percent = (int) (100.0 * bytes / total);
                        int totalMB = total / (1 << 20);
                        setStatusLine("Downloading Terrain " + " (" + percent + "% of " + totalMB + " MB)");
                    });
                    display.setTerrain(terrain.heights);

                    setStatusLine("Watering the World... (running your code)");
                    var flooded = SeaLevel.floodedRegionsIn(terrain.heights, terrain.sources, waterHeight);

                    for (boolean b : flooded[300]) {
                        if (b) {
                            System.out.print("0");
                        } else {
                            System.out.print("X");
                        }
                    }

                    display.setFlooding(flooded);

                    try {
                        SwingUtilities.invokeAndWait(() -> display.repaint());
                    } catch (InterruptedException e) {
                        SwingUtilities.invokeLater(() -> display.repaint());
                    } catch (InvocationTargetException e) {
                        throw new IOException(e);
                    }
                    setStatusLine("");
                } catch (IOException e) {
                    setStatusLine("Error: " + e.getMessage());
                    throw new RuntimeException(e);
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        setEnabled(controlPanel, true);
                    });
                }
            }
        }.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Cannot set look and feel; falling back on default.");
            }
            new SeaLevelGUI();

        });
    }
}
