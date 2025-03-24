package ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Panel that displays the letters that have been used during the game.
 * The panel updates immediately when new letters are added.
 */
public class UsedLettersPanel extends JPanel {
    // Label to display the used letters
    private JLabel lettersLabel;
    // Set to store the used letters while maintaining insertion order
    private Set<Character> usedLetters;

    /**
     * Constructor that initializes the panel.
     */
    public UsedLettersPanel() {
        usedLetters = new LinkedHashSet<>();
        setLayout(new FlowLayout(FlowLayout.LEFT));
        // Set a distinct baby blue background color (RGB 173, 216, 230)
        setBackground(new Color(204, 229, 255));
        // Set a larger preferred size to make the panel bigger
        setPreferredSize(new Dimension(800, 50));
        lettersLabel = new JLabel("Used letters: ");
        // Increase the font size for better readability
        lettersLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lettersLabel);
    }

    /**
     * Adds a letter to the panel and updates the display.
     * @param letter the letter to add.
     */
    public void addLetter(char letter) {
        // Convert letter to uppercase to avoid duplicates in different cases
        letter = Character.toUpperCase(letter);
        if (!usedLetters.contains(letter)) {
            usedLetters.add(letter);
            updateDisplay();
        }
    }
    /**
     * Checks if a letter has already been used.
     * @param letter the letter to check.
     * @return true if the letter is already used, false otherwise.
     */
    public boolean isLetterUsed(char letter) {
        return usedLetters.contains(Character.toUpperCase(letter));
    }

    /**
     * Updates the label to show the current used letters.
     */
    private void updateDisplay() {
        StringBuilder sb = new StringBuilder("Used letters: ");
        // Iterate over the used letters using a standard loop
        for (int i = 0; i < usedLetters.size(); i++) {
            // Since usedLetters is a Set (not indexable), iterate with for-each:
            for (Character c : usedLetters) {
                sb.append(c).append(" ");
            }
            break; // break after first full iteration
        }
        lettersLabel.setText(sb.toString());
        revalidate();
        repaint();
    }

    /**
     * Resets the panel by clearing all used letters.
     */
    public void reset() {
        usedLetters.clear();
        updateDisplay();
    }
    
    public void removeLetter(char letter) {
        usedLetters.remove(Character.toUpperCase(letter));
        updateDisplay();
    }

}

