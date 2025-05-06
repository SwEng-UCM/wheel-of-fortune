package utils;

import javax.swing.*;
import java.awt.*;

public class CustomUIManager {
    public static void apply() {
        // Solo modificamos el fondo
        Color background = new Color(230, 224, 248); // Morado claro
        UIManager.put("Panel.background", background);
        UIManager.put("OptionPane.background", background);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.DARK_GRAY);
    }
}
