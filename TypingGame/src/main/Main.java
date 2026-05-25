package main;

import controller.GameController;
import view.GameWindow;
import javax.swing.SwingUtilities;

/*
    Anggota:
        123240097 - Alexander
        1232340099 - Rabbani
*/
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