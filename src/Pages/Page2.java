package src.Pages;

import src.CSGui.*;

import java.awt.*;
import java.awt.event.*;


public class Page2 {
    private static final Color ORANGE_COLOR = new Color(255, 102, 0);
    private static final Color BROWN_COLOR = new Color(0x7b2c24);
    private UTimer animationTimer;
    private Uframe frame;
    private UButton backButton;
    private UButton btn2;
    private UButton btn3;
    private UButton btn4;


    
    public Page2(){
    // Initialize main frame with background image
    this.frame = new Uframe("UNO GAME", 1000, 650, "PAGE 1.1.png");
    frame.setExtendedState(Uframe.MAXIMIZED_BOTH);

    PanelGBLayout panel = frame.getMainPanel();
    panel.setLayout(null);

    // Create and configure title label
    ULabel titleLabel = new ULabel("Multiplayer", 0);
    titleLabel.setFont(new Font("MV Boli", Font.ITALIC, 110));
    titleLabel.setTextColor(ORANGE_COLOR);
    titleLabel.setSize(800, 130);
    panel.add(titleLabel);

    // Glow animation variables
    final float[] glowIntensity = {0f};
    final int[] glowDir = {1};
    final float maxGlow = 0.8f;

    // Load player icons
    UImageIcon icon2 = panel.resizeIcon("2 PLAYERS.png", 150, 150);
    UImageIcon icon3 = panel.resizeIcon("3 PLAYERS.png", 150, 150);
    UImageIcon icon4 = panel.resizeIcon("4 PLAYERS.png", 150, 150);

    // Button colors
    Color bgNormal = Color.WHITE;
    Color bgHover = new Color(255, 102, 0);
    Color bgClick = new Color(204, 85, 0);

    // Create player buttons
    this.btn2 = new UButton("2 PLAYERS", null, 70, bgNormal, bgHover, bgClick, new Color(180, 180, 180));
    this.btn3 = new UButton("3 PLAYERS", null, 70, bgNormal, bgHover, bgClick, new Color(180, 180, 180));
    this.btn4 = new UButton("4 PLAYERS", null, 70, bgNormal, bgHover, bgClick, new Color(180, 180, 180));

    // Create and configure back button (circular)
    this.backButton = new UButton("◄", null, 180, Color.RED, Color.RED.darker(), Color.RED.darker(), Color.BLACK);
    backButton.setFont(new Font("Arial", Font.BOLD, 25));
    backButton.setCustomSize(80, 80);
    backButton.setBorderPainted(false);
    backButton.setFocusPainted(false);
    backButton.setBounds(30, panel.getHeight() - 90, 80, 80);
    panel.add(backButton);
    
    // Create player panels
    UPanel p2 = createPlayerPanel(icon2, btn2);
    UPanel p3 = createPlayerPanel(icon3, btn3);
    UPanel p4 = createPlayerPanel(icon4, btn4);

    // Create rounded panel container
    RoundedCornerPanel roundedPanel = new RoundedCornerPanel(150);
    roundedPanel.setBackground(BROWN_COLOR);
    roundedPanel.setBackgroundImage(new UImageIcon("PAGE 2.2.png").getImage());
    roundedPanel.setLayout(new UboxLyout(roundedPanel, UboxLyout.X_AXIS));
    
    // Add components to rounded panel
    roundedPanel.add(Ubox.createHorizontalGlue());
    roundedPanel.add(p2);
    roundedPanel.add(Ubox.createHorizontalStrut(40));
    roundedPanel.add(p3);
    roundedPanel.add(Ubox.createHorizontalStrut(40));
    roundedPanel.add(p4);
    roundedPanel.add(Ubox.createHorizontalGlue());

    // Position rounded panel
    int rpWidth = 900;
    int rpHeight = 450;
    roundedPanel.setSize(rpWidth, rpHeight);
    panel.add(roundedPanel);

    // Centering function for responsive layout
    Runnable centerPanel = () -> {
        int pw = panel.getWidth();
        int ph = panel.getHeight();

        roundedPanel.setLocation((pw - rpWidth) / 2, (ph - rpHeight) / 2);
        titleLabel.setLocation((pw - titleLabel.getWidth()) / 2, 30);
        backButton.setLocation(30, ph - 90);

        // Update title glow effect
        float glow = glowIntensity[0];
        titleLabel.setTextColor(new Color(
            ORANGE_COLOR.getRed(),
            ORANGE_COLOR.getGreen(),
            ORANGE_COLOR.getBlue(),
            (int)(255 * (0.5f + glow / 2))
        ));
    };
    centerPanel.run();

    // Window resize listener
    frame.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            centerPanel.run();
        }
    });

    // Title glow animation
    animationTimer = new UTimer(50, e -> {
        glowIntensity[0] += glowDir[0] * 0.05f;
        if (glowIntensity[0] > maxGlow || glowIntensity[0] < 0) {
            glowDir[0] *= -1;
        }
        centerPanel.run();
    });
    animationTimer.start();

    // Enter key listener
    frame.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (animationTimer != null) animationTimer.stop();
                frame.dispose();
                System.out.println("Game Started!");
            }
        }
    });

    // Add action listeners to all buttons
    btn2.addActionListener(e -> handle2PlayersButtonClick());
    btn3.addActionListener(e -> handle3PlayersButtonClick());
    btn4.addActionListener(e -> handle4PlayersButtonClick());
    backButton.addActionListener(e -> handleBackButtonClick());

    frame.setFocusable(true);
    frame.requestFocusInWindow();
}

private static UPanel createPlayerPanel(UImageIcon icon, UButton button) {
    UPanel container = new UPanel();
    container.setLayout(new UboxLyout(container, UboxLyout.Y_AXIS));
    container.setOpaque(false);

    Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
    // Create UImageIcon from the scaled image to stay within CSGui components
    ULabel image = new ULabel(new UImageIcon(img));
    image.setAlignmentX(Component.CENTER_ALIGNMENT);

    button.setCustomSize(250, 40);
    button.setMaximumSize(new Dimension(250, 40));
    button.setAlignmentX(Component.CENTER_ALIGNMENT);

    container.add(Ubox.createVerticalGlue());
    container.add(image);
    container.add(Ubox.createRigidArea(new Dimension(0, 50)));
    container.add(button);
    container.add(Ubox.createVerticalGlue());

    return container;

    }
    
    // Button event handler methods
    private void handle2PlayersButtonClick() {
        System.out.println("2 PLAYERS button clicked");
        // Initialize a 2-player game
        if (animationTimer != null) animationTimer.stop();
        new Page3(2);
        if (frame != null) {
            frame.dispose();
        }
    }
    
    private void handle3PlayersButtonClick() {
        System.out.println("3 PLAYERS button clicked");
        // Initialize a 3-player game
        if (animationTimer != null) animationTimer.stop();
        new Page3(3);
        if (frame != null) {
            frame.dispose();
        }
    }
    
    private void handle4PlayersButtonClick() {
        System.out.println("4 PLAYERS button clicked");
        // Initialize a 4-player game
        if (animationTimer != null) animationTimer.stop();
        new Page3(4);
        if (frame != null) {
            frame.dispose();
        }
    }
    
    private void handleBackButtonClick() {
        System.out.println("Back button clicked");
        // Go back to the previous screen (Page1)
        if (animationTimer != null) animationTimer.stop();
        new Page1();
        // Dispose the current frame
        if (frame != null) {
            frame.dispose();
        }
    }
}
