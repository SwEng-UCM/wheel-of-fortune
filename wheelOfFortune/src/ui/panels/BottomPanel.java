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
    
    public void clearLastMessages(int numLines) {
        String text = messageArea.getText();
        for (int i = 0; i < numLines; i++) {
            int lastLineBreak = text.lastIndexOf("\n");
            if (lastLineBreak != -1) {
                text = text.substring(0, lastLineBreak);
            } else {
                text = ""; // Si es la última línea, limpiar todo
                break;
            }
        }
        messageArea.setText(text);
    }


    
    public int getMessageCount() {
        return messageArea.getText().split("\n").length;
    }


}
