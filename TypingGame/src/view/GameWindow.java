package view;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import model.Word;

public class GameWindow extends JFrame{
    public JTextField inputField;
    public JLabel lblScore, lblHp, lblLevel;
    public JTextArea txtLeaderboard;
    public JButton btnReset;
    private GamePanel gamePanel;

    public GameWindow() {
        setTitle("Type-Attack: Invasion");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel 
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        lblScore = new JLabel("Score: 0");
        lblHp = new JLabel("HP: 3");
        lblLevel = new JLabel("Level: 1");
        topPanel.add(lblScore);
        topPanel.add(lblHp);
        topPanel.add(lblLevel);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel (Game Area)
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.BOLD, 18));
        bottomPanel.add(new JLabel(" Ketik di sini: "), BorderLayout.WEST);
        bottomPanel.add(inputField, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Sidebar Leaderboard
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(200, 600));
        txtLeaderboard = new JTextArea();
        txtLeaderboard.setEditable(false);
        btnReset = new JButton("Main Lagi");
        
        sidePanel.add(new JLabel(" TOP SCORE ", JLabel.CENTER), BorderLayout.NORTH);
        sidePanel.add(new JScrollPane(txtLeaderboard), BorderLayout.CENTER);
        sidePanel.add(btnReset, BorderLayout.SOUTH);
        add(sidePanel, BorderLayout.EAST);
    }

    public void setWords(ArrayList<Word> words) {
        gamePanel.setWords(words);
    }

    public void refreshScreen() {
        gamePanel.repaint();
    }

    // Inner class khusus untuk menggambar kata custom
    private static class GamePanel extends JPanel {
        private ArrayList<Word> words = new ArrayList<>();

        public void setWords(ArrayList<Word> words) {
            this.words = words;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK); 
            g.setFont(new Font("Arial", Font.BOLD, 16));

            // Gambar semua kata yang sedang aktif di layar
            for (int i = 0; i < words.size(); i++) {
                Word w = words.get(i);
                g.setColor(w.getColor());
                g.drawString(w.getText(), w.getX(), w.getY());
            }
        }
    }
}
