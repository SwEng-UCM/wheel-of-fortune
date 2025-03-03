package ui.panels;

import ui.GameUI;

import javax.swing.*;
import java.awt.*;

/**
 * Panel inferior: muestra un área de texto para los mensajes y feedback.
 */
public class BottomPanel extends JPanel {
    private GameUI gameUI;
    private JTextArea messageArea;

    public BottomPanel(GameUI gameUI) {
        this.gameUI = gameUI;
        setLayout(new BorderLayout());

        messageArea = new JTextArea(6, 50);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Añade un mensaje al área de texto.
     */
    public void appendMessage(String message) {
        messageArea.append(message + "\n");
    }
}
