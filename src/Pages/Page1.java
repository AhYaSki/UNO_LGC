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
    
    public Page1() {
        // Initialize main frame with background image
        Uframe frame = new Uframe("UNO GAME", 800, 600, "PAGE 1.1.png");
        frame.setExtendedState(Uframe.MAXIMIZED_BOTH);

        PanelGBLayout panel = frame.getMainPanel();
        panel.setLayout(null);

        // UNO logo setup
        int logoWidth = 500;
        int logoHeight = 370;
        ImageIcon unoIcon = panel.resizeIcon("UNO LOGO (STARTUP).png", logoWidth, logoHeight);
        ULabel unoLogo = new ULabel(unoIcon);
        panel.add(unoLogo);

        // Press to start label setup
        Font font = new Font("MV Boli", Font.ITALIC, 50);
        ULabel label = new ULabel("PRESS 'ENTER' TO START...", SwingConstants.CENTER);
        label.setFont(font);
        label.setTextColor(ORANGE_COLOR);
        label.setAlign(SwingConstants.CENTER);
        label.setSize(700, 60);
        panel.add(label);

        // Animation variables
        final int[] yOffsetLogo = {0};
        final int[] logoDir = {1};
        final int bounceRange = 15;
        final float[] glowIntensity = {0f};
        final int[] glowDir = {1};
        final float maxGlow = 0.8f;

        // Position update function
        Runnable updatePositions = () -> {
            int pw = panel.getWidth();
            int ph = panel.getHeight();

            // Center logo with bounce effect
            int logoX = (pw - logoWidth) / 2;
            int logoY = (ph - logoHeight) / 2 + yOffsetLogo[0];
            unoLogo.setBounds(logoX, logoY, logoWidth, logoHeight);

            // Position label at bottom with glow effect
            int textX = (pw - label.getWidth()) / 2;
            int textY = ph - 100;
            label.setLocation(textX, textY);
            
            // Update glow effect
            float glow = glowIntensity[0];
            label.setTextColor(new Color(
                ORANGE_COLOR.getRed(),
                ORANGE_COLOR.getGreen(),
                ORANGE_COLOR.getBlue(),
                (int)(255 * (0.5f + glow/2))
            ));
        };

        updatePositions.run();

        // Window resize listener
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updatePositions.run();
            }
        });

        // Animation timer
        Timer timer = new Timer(50, e -> {
            // Logo bounce animation
            yOffsetLogo[0] += logoDir[0];
            if (yOffsetLogo[0] > bounceRange || yOffsetLogo[0] < -bounceRange) {
                logoDir[0] *= -1;
            }

            // Text glow animation
            glowIntensity[0] += glowDir[0] * 0.05f;
            if (glowIntensity[0] > maxGlow || glowIntensity[0] < 0) {
                glowDir[0] *= -1;
            }

            updatePositions.run();
        });
        timer.start();
        
        // Keyboard listener for start action
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timer.stop();
                    Page2 mypg2=new Page2();
                    frame.dispose();
                    // Add game start logic here
                }
            }
        });
        
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }
}
