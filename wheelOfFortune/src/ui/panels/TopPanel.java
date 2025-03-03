package ui.panels;

import ui.GameUI;

import javax.swing.*;
import java.awt.*;

public class TopPanel extends JPanel {
    // Tamaño del banner
    private static final int BANNER_WIDTH = 400;
    private static final int BANNER_HEIGHT = 80;

    private GameUI gameUI;
    private JLabel phraseLabel;

    public TopPanel(GameUI gameUI) {
        this.gameUI = gameUI;

        // Fijamos un layout
        setLayout(new BorderLayout());

        // -- COLOR DE FONDO BLANCO --
        setBackground(Color.WHITE);
        setOpaque(true);

        // --- 1. Banner reducido ---
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/TopPanel.png"));
        Image originalImage = originalIcon.getImage();

        Image scaledImage = originalImage.getScaledInstance(BANNER_WIDTH, BANNER_HEIGHT, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel bannerLabel = new JLabel(scaledIcon);
        bannerLabel.setPreferredSize(new Dimension(BANNER_WIDTH, BANNER_HEIGHT));
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Si quieres que el label muestre el fondo blanco en lugar de gris, hazlo opaco
        bannerLabel.setBackground(Color.WHITE);
        bannerLabel.setOpaque(true);

        // Añadimos el banner en la parte superior
        add(bannerLabel, BorderLayout.NORTH);

        // --- 2. Panel para la frase ---
        JPanel phrasePanel = new JPanel(new BorderLayout());
        // También lo ponemos en blanco
        phrasePanel.setBackground(Color.WHITE);
        phrasePanel.setOpaque(true);

        phraseLabel = new JLabel("", SwingConstants.CENTER);
        phraseLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        phraseLabel.setForeground(Color.BLACK);  // Ajusta según tu gusto
        // No es obligatorio hacerlo opaco si el fondo ya es blanco:
        phraseLabel.setOpaque(false);

        phrasePanel.add(phraseLabel, BorderLayout.CENTER);
        add(phrasePanel, BorderLayout.CENTER);

        // Mostramos la frase inicial
        updatePhraseLabel();
    }

    /**
     * Actualiza la etiqueta que muestra la frase (con '_' o letras descubiertas).
     */
    public void updatePhraseLabel() {
        char[] revealed = gameUI.getRevealed();
        StringBuilder sb = new StringBuilder("<html><body style='width:600px; text-align:center; font-family:monospace;'>");
        for (char c : revealed) {
            if (c == ' ') {
                sb.append("&nbsp; ");
            } else {
                sb.append(c).append(' ');
            }
        }
        sb.append("</body></html>");
        phraseLabel.setText(sb.toString());
    }
}
