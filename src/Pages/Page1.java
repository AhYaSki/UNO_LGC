package src.Pages;

import src.CSGui.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class Page1 {
    private static final Color ORANGE_COLOR = new Color(255, 102, 0);

    // Constructor: Initializes the welcome screen with animated logo and start prompt
    public Page1() {
        Uframe frame = new Uframe("UNO GAME", 800, 600, "assets/PAGE 1.1.png");
        frame.setExtendedState(Uframe.MAXIMIZED_BOTH);

        PanelGBLayout panel = frame.getMainPanel();
        panel.setLayout(null);

        // Setup UNO logo
        int logoWidth = 500;
        int logoHeight = 370;
        ImageIcon unoIcon = panel.resizeIcon("assets/UNO LOGO (STARTUP).png", logoWidth, logoHeight);
        ULabel unoLogo = new ULabel(unoIcon);
        panel.add(unoLogo);

        // Setup animated label text
        Font font = new Font("MV Boli", Font.ITALIC, 50);
        ULabel label = new ULabel("PRESS 'ENTER' TO START...", SwingConstants.CENTER);
        label.setFont(font);
        label.setTextColor(ORANGE_COLOR);
        label.setAlign(SwingConstants.CENTER);
        label.setSize(700, 60);
        panel.add(label);

        // Animation state variables
        final int[] yOffsetLogo = {0};
        final int[] logoDir = {1};
        final int bounceRange = 15;
        final float[] glowIntensity = {0f};
        final int[] glowDir = {1};
        final float maxGlow = 0.8f;

        // Position update: centers and animates logo and label
        Runnable updatePositions = () -> {
            int pw = panel.getWidth();
            int ph = panel.getHeight();

            int logoX = (pw - logoWidth) / 2;
            int logoY = (ph - logoHeight) / 2 + yOffsetLogo[0];
            unoLogo.setBounds(logoX, logoY, logoWidth, logoHeight);

            int textX = (pw - label.getWidth()) / 2;
            int textY = ph - 100;
            label.setLocation(textX, textY);

            float glow = glowIntensity[0];
            label.setTextColor(new Color(
                ORANGE_COLOR.getRed(),
                ORANGE_COLOR.getGreen(),
                ORANGE_COLOR.getBlue(),
                (int)(255 * (0.5f + glow / 2))
            ));
        };
        updatePositions.run();

        // Recenter elements on window resize
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updatePositions.run();
            }
        });

        // Animation timer: handles logo bounce and label glow
        Timer timer = new Timer(50, e -> {
            yOffsetLogo[0] += logoDir[0];
            if (yOffsetLogo[0] > bounceRange || yOffsetLogo[0] < -bounceRange) {
                logoDir[0] *= -1;
            }

            glowIntensity[0] += glowDir[0] * 0.05f;
            if (glowIntensity[0] > maxGlow || glowIntensity[0] < 0) {
                glowDir[0] *= -1;
            }

            updatePositions.run();
        });
        timer.start();

        // Start game when user presses Enter
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timer.stop();
                    new Page2();
                    frame.dispose();
                }
            }
        });

        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }
}
