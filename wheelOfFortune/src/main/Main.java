package main;

import javax.swing.SwingUtilities;
import ui.GameUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameUI::new);
    }
}
