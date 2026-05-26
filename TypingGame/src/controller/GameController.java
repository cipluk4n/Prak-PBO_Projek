package controller;

import model.DbConnection; 
import model.Player;
import model.Word;
import model.NormalWord;
import model.BonusWord;
import view.GameWindow;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameController {
    private GameWindow view;
    private Player player;
    private ArrayList<Word> activeWords;
    private boolean isRunning = true;
    private String[] wordPool = {"gajah", "singa", "harimau", "kucing", "anjing", 
        "mangga", "pisang", "apel", "jeruk", "durian", "jerapah", "kelinci", 
        "anggur", "semangka", "kelapa", "kiwi", "melon", "tikus", "babi", "ayam", 
        "alpukat", "murbei"};
    public GameController(GameWindow view) {
        this.view = view;
        this.activeWords = new ArrayList<>();

        String name = JOptionPane.showInputDialog(view, "Masukkan Nama Player:");
        if (name == null || name.trim().isEmpty()) name = "Guest";
        //TODO: kalau Guest gamasuk di topscore
        this.player = new Player(name.trim());

        initController();
        loadLeaderboard();
        startGameLoop();
    }
    
    private void initController() {
        view.inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkTypedWord(view.inputField.getText().trim());
                view.inputField.setText(""); // mengosongkan textfield setelah enter
            }
        });

        view.btnMainLagi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isRunning) { 
                    restartGame();
                } else {
                    JOptionPane.showMessageDialog(view, "Game masih berjalan! Selesaikan permainan terlebih dahulu.");
                }
            }
        });
        view.btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    deleteLeaderboard();
            }
        });
    }
    
    private void startGameLoop() {
        Thread gameThread = new Thread(new Runnable() {
            int spawnTimer = 0;

            @Override
            public void run() {
                while (isRunning && player.getHp() > 0) {
                    spawnTimer++;
                    if (spawnTimer >= 80) { 
                        spawnWord();
                        spawnTimer = 0;
                    }

                    updateGameStatus();
                    view.setWords(activeWords); // menurunkan koordinat Y kata
                    view.refreshScreen(); // menggambar ulang layar

                    try {
                        Thread.sleep(40); // menjalankan game di kisaran 50 FPS
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                gameOver();
            }
        });
        gameThread.start(); 
    }

    private void spawnWord() {
        Random rand = new Random();
        String text = wordPool[rand.nextInt(wordPool.length)];
        int x = rand.nextInt(500) + 50; // Random koordinat X layar

        if (rand.nextBoolean()) {
            activeWords.add(new NormalWord(text, x));
        } else {
            activeWords.add(new BonusWord(text, x));
        }
    }

    private void updateGameStatus() {
        // Mengambil tingkat kesulitan/tambahan kecepatan berdasarkan level pemain
        int speedBonus = player.getLevel() - 1; 

        for (int i = 0; i < activeWords.size(); i++) {
            Word w = activeWords.get(i);

            w.setY(w.getY() + w.getSpeed() + speedBonus);

            if (w.getY() >= 500) {
                player.reduceHp();
                view.lblHp.setText("HP: " + player.getHp());
                activeWords.remove(i);
                i--;
            }
        }

        // naik level setiap kelipatan 50 poin
        int currentLevel = (player.getScore() / 50) + 1;
        player.setLevel(currentLevel);
        view.lblLevel.setText("Level: " + player.getLevel());
    }

    private void checkTypedWord(String typedStr) {
        for (int i = 0; i < activeWords.size(); i++) {
            Word w = activeWords.get(i);
            if (w.getText().equalsIgnoreCase(typedStr)) {
                player.addScore(w.onTypeSuccess());
                view.lblScore.setText("Score: " + player.getScore());
                activeWords.remove(i);
                break;
            }
        }
    }

    // CRUD: Create, Update
    private void gameOver() {
        isRunning = false;
        JOptionPane.showMessageDialog(view, "GAME OVER!\nSkor Akhir Anda: " + player.getScore());

        try (Connection conn = DbConnection.getConnection()) {
            if (conn != null) {
                // update jika skor baru > skor lama
                String query = "INSERT INTO highscores (username, score, level) VALUES (?, ?, ?) " +
                               "ON DUPLICATE KEY UPDATE " +
                               "level = IF(VALUES(score) > score, VALUES(level), level), " +
                               "score = GREATEST(score, VALUES(score))";

                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, player.getUsername().trim());
                    ps.setInt(2, player.getScore());
                    ps.setInt(3, player.getLevel());
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(view, "Skor Anda berhasil diproses!");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Gagal simpan database: " + e.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }

        loadLeaderboard();
    }

    private void restartGame() {
        String name = JOptionPane.showInputDialog(view, "Masukkan Nama Player Baru / Lama:");
        if (name == null || name.trim().isEmpty()) name = "Guest";

        this.player = new Player(name.trim());

        activeWords.clear();
        view.lblScore.setText("Score: 0");
        view.lblHp.setText("HP: 3");
        view.lblLevel.setText("Level: 1");
        view.inputField.setText("");
        view.inputField.requestFocusInWindow();

        this.isRunning = true;
        startGameLoop();
    }
    
    private void loadLeaderboard() {
        view.txtLeaderboard.setText(""); // Kosongkan tampilan lama

        Connection conn = DbConnection.getConnection();
        if (conn == null) {
            view.txtLeaderboard.setText(" Gagal memuat database!\n Periksa koneksi XAMPP.");
            return;
        }

        try {
            String query = "SELECT username, score FROM highscores ORDER BY score DESC LIMIT 10";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            int rank = 1;
            StringBuilder sb = new StringBuilder(); 

            while (rs.next()) {
                sb.append(" ").append(rank).append(". ")
                  .append(rs.getString("username")).append(" : ")
                  .append(rs.getInt("score")).append("\n");
                rank++;
            }

            if (rank == 1) {
                view.txtLeaderboard.setText(" Belum ada data skor.");
            } else {
                view.txtLeaderboard.setText(sb.toString());
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            view.txtLeaderboard.setText(" Error SQL: " + e.getMessage());
            System.out.println("Error Load Leaderboard: " + e.getMessage());
        }
    }

    private void deleteLeaderboard() {
        int confirm = JOptionPane.showConfirmDialog(view, "Hapus semua data skor?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DbConnection.getConnection()) {
                if (conn != null) {
                    String query = "DELETE FROM highscores";
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(query);
                    
                    loadLeaderboard();
                    JOptionPane.showMessageDialog(view, "Leaderboard berhasil dikosongkan!");
                }
            } catch (SQLException e) {
                System.out.println("Error Delete Leaderboard: " + e.getMessage());
            }
        }
    }
}