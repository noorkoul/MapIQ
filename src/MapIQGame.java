import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ==========================================
// DATA LAYER: Country Class
// ==========================================
class Country {
    private String name;
    private String flagUrl; // Remote image link URL
    private String geographicalFact; // Educational bonus fact

    public Country(String name, String flagUrl, String geographicalFact) {
        this.name = name;
        this.flagUrl = flagUrl;
        this.geographicalFact = geographicalFact;
    }

    public String getName() { return name; }
    public String getFlagUrl() { return flagUrl; }
    public String getGeographicalFact() { return geographicalFact; }
}

// ==========================================
// APPLICATION ENTRY & UI LAYER
// ==========================================
public class MapIQGame extends JFrame {

    // Modern Color Palette (Deep Sapphire, Soft Off-White, Vibrant Accents)
    private final Color COLOR_PRIMARY = new Color(26, 54, 93);     // Deep Blue
    private final Color COLOR_SECONDARY = new Color(43, 108, 176);  // Light Slate Blue
    private final Color COLOR_BACKGROUND = new Color(247, 250, 252); // Off-White
    private final Color COLOR_TEXT = new Color(45, 55, 72);         // Dark Charcoal
    private final Color COLOR_CORRECT = new Color(72, 187, 120);    // Emerald Green
    private final Color COLOR_INCORRECT = new Color(245, 101, 101);  // Coral Red

    // Game Variables
    private List<Country> database;
    private List<Country> questionPool;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private final int TOTAL_ROUNDS = 5; 

    // UI Components
    private JLabel lblQuestionNum, lblScore, lblFeedback, lblFact, lblFlagDisplay;
    private JButton[] btnOptions;
    private JButton btnNext;

    public MapIQGame() {
        // Initialize Window Configuration
        setTitle("Map IQ: Interactive Educational Game");
        setSize(650, 570);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BACKGROUND);
        setLayout(new BorderLayout(15, 15));

        initializeDatabase();
        startNewGame();
        initUI();
        displayQuestion();
    }

    // ==========================================
    // DATA INITIALIZATION (Using Image URLs)
    // ==========================================
    private void initializeDatabase() {
        database = new ArrayList<>();
        
        // High-quality, reliable image links from FlagCDN API
        database.add(new Country("France", "https://flagcdn.com/w320/fr.png", "Home to the famous Eiffel Tower and the largest art museum, the Louvre."));
        database.add(new Country("Germany", "https://flagcdn.com/w320/de.png", "Known for its rich history, engineering, and the famous Autobahn highway system."));
        database.add(new Country("Italy", "https://flagcdn.com/w320/it.png", "Famous for its historic Roman cuisine, ancient Colosseum, and renaissance art."));
        database.add(new Country("Japan", "https://flagcdn.com/w320/jp.png", "An island nation known for its bullet trains, historic shrines, and iconic Mount Fuji."));
        database.add(new Country("India", "https://flagcdn.com/w320/in.png", "World's largest democracy, celebrated for its structural icon, the Taj Mahal."));
        database.add(new Country("Canada", "https://flagcdn.com/w320/ca.png", "The second-largest country by landmass, widely famous for its maple syrup and wilderness."));
        database.add(new Country("United Kingdom", "https://flagcdn.com/w320/gb.png", "An island nation composed of England, Scotland, Wales, and Northern Ireland."));
        database.add(new Country("Australia", "https://flagcdn.com/w320/au.png", "Famous for its Outback, the Great Barrier Reef, and unique wildlife like kangaroos."));
    }

    private void startNewGame() {
        questionPool = new ArrayList<>(database);
        Collections.shuffle(questionPool); 
        if (questionPool.size() > TOTAL_ROUNDS) {
            questionPool = questionPool.subList(0, TOTAL_ROUNDS);
        }
        currentQuestionIndex = 0;
        score = 0;
    }

    // ==========================================
    // UI LAYOUT ASSEMBLY
    // ==========================================
    private void initUI() {
        // --- Top Bar (Scoreboard & Trackers) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(COLOR_PRIMARY);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        lblQuestionNum = new JLabel("Question: 1 / " + TOTAL_ROUNDS);
        lblQuestionNum.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblQuestionNum.setForeground(Color.WHITE);

        lblScore = new JLabel("Score: 0");
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblScore.setForeground(Color.WHITE);

        pnlHeader.add(lblQuestionNum, BorderLayout.WEST);
        pnlHeader.add(lblScore, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- Center Canvas (Visual Image Area) ---
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setBackground(COLOR_BACKGROUND);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 30, 5, 30));

        // Using a standard JLabel to present web images dynamically
        lblFlagDisplay = new JLabel("", SwingConstants.CENTER);
        lblFlagDisplay.setPreferredSize(new Dimension(320, 200));
        lblFlagDisplay.setOpaque(true);
        lblFlagDisplay.setBackground(Color.WHITE);
        lblFlagDisplay.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 2));
        pnlCenter.add(lblFlagDisplay, BorderLayout.CENTER);

        // --- Bottom Response Mechanics (Options) ---
        JPanel pnlInteraction = new JPanel(new GridLayout(2, 2, 12, 12));
        pnlInteraction.setBackground(COLOR_BACKGROUND);
        btnOptions = new JButton[4];
        
        for (int i = 0; i < 4; i++) {
            btnOptions[i] = new JButton();
            btnOptions[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnOptions[i].setBackground(Color.WHITE);
            btnOptions[i].setForeground(COLOR_TEXT);
            btnOptions[i].setFocusPainted(false);
            btnOptions[i].setBorder(BorderFactory.createLineBorder(new Color(203, 213, 224), 1));
            btnOptions[i].addActionListener(new OptionClickListener());
            pnlInteraction.add(btnOptions[i]);
        }
        pnlCenter.add(pnlInteraction, BorderLayout.SOUTH);
        add(pnlCenter, BorderLayout.CENTER);

        // --- Footer Navigation Area ---
        JPanel pnlFooter = new JPanel(new BorderLayout(10, 5));
        pnlFooter.setBackground(COLOR_BACKGROUND);
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

        lblFeedback = new JLabel("Select the correct nation matching the graphics above.", SwingConstants.CENTER);
        lblFeedback.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFeedback.setForeground(COLOR_SECONDARY);
        pnlFooter.add(lblFeedback, BorderLayout.NORTH);

        lblFact = new JLabel("", SwingConstants.CENTER);
        lblFact.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblFact.setForeground(COLOR_TEXT);
        pnlFooter.add(lblFact, BorderLayout.CENTER);

        btnNext = new JButton("Next Question");
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNext.setBackground(COLOR_SECONDARY);
        btnNext.setForeground(Color.WHITE);
        btnNext.setEnabled(false);
        btnNext.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnNext.addActionListener(e -> advanceGameFlow());
        pnlFooter.add(btnNext, BorderLayout.SOUTH);

        add(pnlFooter, BorderLayout.SOUTH);
    }

    // ==========================================
    // LOGIC LAYER: Game Controller Routines
    // ==========================================
    private void displayQuestion() {
        if (currentQuestionIndex >= questionPool.size()) {
            endGame();
            return;
        }

        btnNext.setEnabled(false);
        lblFeedback.setText("Identify the correct country!");
        lblFeedback.setForeground(COLOR_SECONDARY);
        lblFact.setText("");
        lblQuestionNum.setText("Question: " + (currentQuestionIndex + 1) + " / " + TOTAL_ROUNDS);

        Country currentCountry = questionPool.get(currentQuestionIndex);
        
        // --- ASYNC IMAGE LOADER NETWORK PIPELINE ---
        lblFlagDisplay.setIcon(null);
        lblFlagDisplay.setText("Loading image link...");
        
        // Running network fetch in a background thread to prevent UI freezing
        new Thread(() -> {
            try {
                URL url = new URL(currentCountry.getFlagUrl());
                BufferedImage img = ImageIO.read(url);
                
                // Scale smooth adjustments to keep image crisp
                Image scaledImg = img.getScaledInstance(300, 180, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaledImg);
                
                // Drop back to main Event Dispatch Thread to safely update UI components
                SwingUtilities.invokeLater(() -> {
                    lblFlagDisplay.setText("");
                    lblFlagDisplay.setIcon(icon);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    lblFlagDisplay.setText("Error loading flag imagery: Link unavailable.");
                });
            }
        }).start();

        // Generate Multiple-Choice Options
        List<String> options = new ArrayList<>();
        options.add(currentCountry.getName());

        List<Country> wrongChoices = new ArrayList<>(database);
        wrongChoices.remove(currentCountry);
        Collections.shuffle(wrongChoices);

        for (int i = 0; i < 3 && i < wrongChoices.size(); i++) {
            options.add(wrongChoices.get(i).getName());
        }
        Collections.shuffle(options);

        // Assign to UI Buttons
        for (int i = 0; i < 4; i++) {
            btnOptions[i].setText(options.get(i));
            btnOptions[i].setBackground(Color.WHITE);
            btnOptions[i].setForeground(COLOR_TEXT);
            btnOptions[i].setEnabled(true);
        }
    }

    private class OptionClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();
            Country currentCountry = questionPool.get(currentQuestionIndex);

            for (JButton btn : btnOptions) {
                btn.setEnabled(false);
            }

            if (clickedButton.getText().equals(currentCountry.getName())) {
                clickedButton.setBackground(COLOR_CORRECT);
                clickedButton.setForeground(Color.WHITE);
                lblFeedback.setText("Correct Answer! 🎉");
                lblFeedback.setForeground(COLOR_CORRECT);
                score += 20; 
                lblScore.setText("Score: " + score);
            } else {
                clickedButton.setBackground(COLOR_INCORRECT);
                clickedButton.setForeground(Color.WHITE);
                lblFeedback.setText("Incorrect. That is " + currentCountry.getName() + ".");
                lblFeedback.setForeground(COLOR_INCORRECT);

                for (JButton btn : btnOptions) {
                    if (btn.getText().equals(currentCountry.getName())) {
                        btn.setBackground(COLOR_CORRECT);
                        btn.setForeground(Color.WHITE);
                    }
                }
            }

            lblFact.setText("Did you know? " + currentCountry.getGeographicalFact());
            btnNext.setEnabled(true);
        }
    }

    private void advanceGameFlow() {
        currentQuestionIndex++;
        displayQuestion();
    }

    private void endGame() {
        lblFeedback.setText("Game Over! Evaluation Finished.");
        lblFeedback.setForeground(COLOR_PRIMARY);
        
        int choice = JOptionPane.showConfirmDialog(this,
                "Your Final Score: " + score + " / 100\nWould you like to restart?", 
                "Results reached", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
            lblScore.setText("Score: " + score);
            displayQuestion();
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MapIQGame().setVisible(true));
    }
}