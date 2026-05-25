package main;

import controller.GameController;
import view.GameWindow;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            new GameController(window);
            window.setVisible(true);
            
            window.inputField.requestFocusInWindow();
        });
    }
}