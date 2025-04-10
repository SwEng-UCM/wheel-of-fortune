package ui.panels;

import ui.GameUI;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * Panel inferior: muestra un área de texto para los mensajes y feedback.
 */
public class BottomPanel extends JPanel {
    private GameUI gameUI;
    private JTextPane messagePane;
    private StyledDocument doc;

    public BottomPanel(GameUI gameUI) {
        this.gameUI = gameUI;
        setLayout(new BorderLayout());
        
        setPreferredSize(new Dimension(800, 100));

        // Initialize the JTextPane for styled text
        messagePane = new JTextPane();
        messagePane.setEditable(false);
        messagePane.setFont(new Font("SansSerif", Font.PLAIN, 18));
        doc = messagePane.getStyledDocument();
        
        JScrollPane scrollPane = new JScrollPane(messagePane);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Appends a message in a specific color.
     * @param message The text to display.
     * @param color   The color for the message.
     */
    public void appendMessage(String message, Color color) {
        Style style = messagePane.addStyle("Style", null);
        StyleConstants.setForeground(style, color);
        StyleConstants.setFontSize(style, 18);
        try {
            doc.insertString(doc.getLength(), message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        messagePane.setCaretPosition(doc.getLength());
    }
    
    /**
     * Appends a message in black (default).
     */
    public void appendMessage(String message) {
        appendMessage(message, Color.BLACK);
    }
    
    /**
     * Clears all messages.
     */
    public void clearMessages() {
        messagePane.setText("");
    }
    
    public void clearLastMessages(int numLines) {
        String fullText = messagePane.getText();
        String[] lines = fullText.split("\n");

        if (numLines >= lines.length) {
            // Si hay menos líneas que las que queremos borrar, lo limpiamos todo
            clearMessages();
            return;
        }

        // Construir nuevo texto sin las últimas `numLines`
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length - numLines; i++) {
            sb.append(lines[i]).append("\n");
        }

        messagePane.setText(sb.toString());
    }
    
    public int getMessageCount() {
        String[] lines = messagePane.getText().split("\n");
        return lines.length;
    }
}
