package src.Pages;

import src.CSGui.*;
import src.logicGui.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Page3 {
    // Constants for colors and styling
    private static final Color ORANGE_COLOR = new Color(255, 102, 0);
    private static final Color BROWN_COLOR = new Color(0x7b2c24);
    
    // Column indices for the game log table
    private static final int LOG_PLAYER_COL = 0;
    private static final int LOG_ACTION_COL = 1;
    private static final int LOG_CARD_COL = 2;
    private static final int LOG_TIME_COL = 3;
    
    // Game state properties
    private int numPlayers;
    private String gameDirection = "Clockwise"; // "Clockwise" or "Counter-Clockwise"
    
    // Game logic
    private GameRest gameLogic;
    
    // Game log data
    private UTableModel tableModel;
    private String[] logColumns;
    
    // UI Components
    private Uframe frame;
    private UButton backButton;
    private UCard currentCard;
    private UScroll playerCardsScroll;
    private UTable gameLogTable;
    private ULabel directionLabel;
    private UPanel playerCardsPanel;
    
    public Page3(int players) {
        this.numPlayers = players;
        this.gameLogic = new GameRest(players, players);

        initializeGameUI();
        updateGameUI();
        showNextPlayerTurnDialog();
    }
    
    private void initializeGameUI() {
        frame = new Uframe("UNO GAME", 1200, 800, "PAGE 1.1.png");
        frame.setExtendedState(Uframe.MAXIMIZED_BOTH);
        PanelGBLayout panel = frame.getMainPanel();
        panel.setLayout(null);
        
        ULabel titleLabel = new ULabel(numPlayers + " PLAYER GAME", 0);
        titleLabel.setFont(new Font("MV Boli", Font.ITALIC, 36));
        titleLabel.setTextColor(ORANGE_COLOR);
        titleLabel.setSize(400, 60);
        titleLabel.setLocation(20, 10);
        panel.add(titleLabel);
        
        backButton = new UButton("◄", null, 180, Color.RED, Color.RED.darker(), Color.RED.darker(), Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setCustomSize(60, 60);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setBounds(20, 700, 60, 60);
        backButton.addActionListener(e -> handleBackButtonClick());
        panel.add(backButton);
        
        directionLabel = new ULabel("Direction: " + gameDirection, 0);
        directionLabel.setFont(new Font("MV Boli", Font.PLAIN, 18));
        directionLabel.setTextColor(ORANGE_COLOR);
        directionLabel.setSize(250, 30);
        directionLabel.setLocation(900, 25);
        panel.add(directionLabel);
        
        currentCard = new UCard();
        currentCard.setCardSize(150, 220);
        currentCard.setBorderColor(ORANGE_COLOR);
        currentCard.setBgColor(BROWN_COLOR);
        currentCard.setBounds(525, 250, 150, 220);
        
        String cardImagePath = "assets/cards/" + gameLogic.getLastPlayedCardString() + ".png";
        currentCard.setImageFromPath(cardImagePath);
        panel.add(currentCard);
        
        ULabel currentCardLabel = new ULabel("Current Card", 0);
        currentCardLabel.setFont(new Font("MV Boli", Font.PLAIN, 18));
        currentCardLabel.setTextColor(ORANGE_COLOR);
        currentCardLabel.setSize(150, 30);
        currentCardLabel.setLocation(525, 215);
        panel.add(currentCardLabel);
        
        // Create game log table with column names corresponding to our constants
        logColumns = new String[4];
        logColumns[LOG_PLAYER_COL] = "Player";
        logColumns[LOG_ACTION_COL] = "Action";
        logColumns[LOG_CARD_COL] = "Card";
        logColumns[LOG_TIME_COL] = "Time";
        tableModel = new UTableModel(logColumns, 0);
        
        // Create the game log table FIRST before adding any data to it
        gameLogTable = new UTable(new Object[0][0], logColumns);
        gameLogTable.setBgColors(Color.WHITE, new Color(255, 240, 230), new Color(255, 220, 200));
        gameLogTable.setRowH(30);
        
        // Now add initial card to log
        addToGameLog("Game Start", "Initial Card", gameLogic.getLastPlayedCardString());
        
        UScroll tableScroll = new UScroll(gameLogTable);
        tableScroll.setBounds(800, 70, 380, 600);
        panel.add(tableScroll);
        
        // Create label for game log
        ULabel gameLogLabel = new ULabel("Game Log", 0);
        gameLogLabel.setFont(new Font("MV Boli", Font.PLAIN, 18));
        gameLogLabel.setTextColor(ORANGE_COLOR);
        gameLogLabel.setSize(150, 30);
        gameLogLabel.setLocation(950, 70 - 30);
        panel.add(gameLogLabel);
        
        // Create player cards panel
        playerCardsPanel = new UPanel();
        playerCardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        playerCardsPanel.setOpaque(false);
        
        // Add player's cards from the game logic
        addPlayerCards(playerCardsPanel);
        
        // Add scroll area for player cards
        playerCardsScroll = new UScroll(playerCardsPanel);
        playerCardsScroll.setBounds(20, 500, 750, 180);
        panel.add(playerCardsScroll);
        
        // Create label for player cards
        ULabel playerCardsLabel = new ULabel("Your Cards", 0);
        playerCardsLabel.setFont(new Font("MV Boli", Font.PLAIN, 18));
        playerCardsLabel.setTextColor(ORANGE_COLOR);
        playerCardsLabel.setSize(150, 30);
        playerCardsLabel.setLocation(20, 470);
        panel.add(playerCardsLabel);
        
        // Player info panel (shows current player and other players' card counts)
        createPlayerInfoPanel(panel);
        
        
        // Draw card button
        UButton drawCardBtn = new UButton("Draw Card", null, 180, new Color(70, 130, 180), new Color(51, 102, 153), new Color(102, 178, 255), Color.WHITE);
        drawCardBtn.setFont(new Font("Arial", Font.BOLD, 14));
        drawCardBtn.setCustomSize(120, 40);
        drawCardBtn.setBounds(currentCard.getX() - 140, currentCard.getY() + 80, 120, 40);
        drawCardBtn.addActionListener(e -> drawCard());
        panel.add(drawCardBtn);
        
        // Say UNO button
        UButton sayUnoBtn = new UButton("SAY UNO!", null, 180, Color.RED, Color.RED.darker(), new Color(255, 102, 102), Color.WHITE);
        sayUnoBtn.setFont(new Font("Arial", Font.BOLD, 18));
        sayUnoBtn.setCustomSize(120, 40);
        sayUnoBtn.setBounds(currentCard.getX() + currentCard.getWidth() + 20, currentCard.getY() + 80, 120, 40);
        sayUnoBtn.addActionListener(e -> sayUno());
        panel.add(sayUnoBtn);
        
        // Make the frame visible
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        
        // All players are human, nothing special to do at initialization
    }
    
    private void updatePlayerInfo() {
        // Update player info panel with current player information
        if (frame != null && frame.getMainPanel() != null) {
            // Find the existing player info panel and remove it
            Component[] components = frame.getMainPanel().getComponents();
            for (Component component : components) {
                if (component instanceof UPanel && component.getBounds().y == 100) {
                    frame.getMainPanel().remove(component);
                    break;
                }
            }
            
            // Create a new player info panel
            createPlayerInfoPanel(frame.getMainPanel());
            frame.getMainPanel().revalidate();
            frame.getMainPanel().repaint();
        }
    }
    
    /**
     * Updates all UI elements based on the current game state
     */
    private void updateGameUI() {
        // Check if the last played card was a wild card with a chosen color
        Card lastPlayedCard = gameLogic.getLastPlayedCard();
        
        String cardImagePath = "assets/cards/" + gameLogic.getLastPlayedCardString() + ".png";
        
        if (lastPlayedCard instanceof ActionCard) {
            ActionCard actionCard = (ActionCard) lastPlayedCard;
            if ((actionCard.getAction() == ActionCard.Actions.Wild || 
                 actionCard.getAction() == ActionCard.Actions.DRAW_4_Wild) && 
                lastPlayedCard.getForcedColor() != null) {
                
                // Get the forced color for the card image path
                
                if (actionCard.getAction() == ActionCard.Actions.Wild) {
                    cardImagePath = "assets/cards/Black_Wild_" + lastPlayedCard.getForcedColor().toString() + ".png";
                } else {
                    cardImagePath = "assets/cards/Black_DRAW_4_Wild_" + lastPlayedCard.getForcedColor().toString() + ".png";
                }
            }
        }
        
        currentCard.setImageFromPath(cardImagePath);
        directionLabel.setText("Direction: " + gameDirection);
        
        if (playerCardsPanel != null) {
            playerCardsPanel.removeAll();
            addPlayerCards(playerCardsPanel);
            playerCardsPanel.revalidate();
            playerCardsPanel.repaint();
        }
        
        updatePlayerInfo();
    }
    
    private void createPlayerInfoPanel(Container container) {
        UPanel playerInfoPanel = new UPanel();
        playerInfoPanel.setOpaque(true);
        playerInfoPanel.setBackground(new Color(255, 240, 220));
        playerInfoPanel.setLayout(new GridLayout(0, 1, 5, 5));
        playerInfoPanel.setBounds(20, 100, 270, numPlayers * 45 + 10);
        
        // Add a player info header
        ULabel headerLabel = new ULabel("Player Information", 0);
        headerLabel.setFont(new Font("MV Boli", Font.BOLD, 16));
        headerLabel.setTextColor(BROWN_COLOR);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerInfoPanel.add(headerLabel);
        
        // Current player index
        int currentPlayerIndex = gameLogic.getCurrentPlayerIndex();
        
        // Add information for each player
        for (int i = 0; i < numPlayers; i++) {
            Player player = gameLogic.getPlayers().get(i);
            ULabel playerLabel = new ULabel("Player " + player.getId() + 
                                          " - Cards: " + player.getHand().size(), 0);
            
            // Highlight current player
            if (i == currentPlayerIndex) {
                playerLabel.setFont(new Font("MV Boli", Font.BOLD, 14));
                playerLabel.setTextColor(Color.RED.darker());
                playerLabel.setText("→ " + playerLabel.getText());
            } else {
                playerLabel.setFont(new Font("MV Boli", Font.PLAIN, 14));
                playerLabel.setTextColor(BROWN_COLOR);
            }
            
            playerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            playerInfoPanel.add(playerLabel);
        }
        
        container.add(playerInfoPanel);
    }
    
    private void addPlayerCards(UPanel cardsPanel) {
        // Get the current player's hand
        Player currentPlayer = gameLogic.getCurrentPlayer();
        List<Card> hand = currentPlayer.getHand();
        Card lastPlayedCard = gameLogic.getLastPlayedCard();
        
        // Add each card as a UCard component
        for (int i = 0; i < hand.size(); i++) {
            Card playerCard = hand.get(i);
            UCard card = new UCard();
            
            // Set card properties
            card.setCardSize(100, 150);
            card.setBorderColor(playerCard.isPlayable(lastPlayedCard) ? Color.GREEN : Color.GRAY);
            card.setBgColor(BROWN_COLOR);
            
            // Get the card image path
            String cardImagePath = "assets/cards/" + gameLogic.cardToString(playerCard) + ".png";
            card.setImageFromPath(cardImagePath);
            
            // Add event listener for card clicks
            final int cardIndex = i;
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleCardClick(cardIndex);
                }
            });
            
            cardsPanel.add(card);
        }
        
        // Refresh the panel
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
    
    private String showColorSelectionDialog() {
        String[] colors = {"Red", "Green", "Blue", "Yellow"};
        String selectedColor = (String) UOptionPane.showInputDialog(
                frame,
                "Choose a color:",
                "Color Selection",
                UOptionPane.QUESTION_MESSAGE,
                null,
                colors,
                colors[0]);
        
        return selectedColor;
    }
    
    private void toggleGameDirection() {
        // This is just a visual button - actual direction changes happen through game actions
        // So we'll just update the UI to show the current state
        updateGameUI();
    }
    
    private void drawCard() {
        if (!gameLogic.isGameOver()) {
            // Get current player info
            Player currentPlayer = gameLogic.getCurrentPlayer();
            
            // Draw a card
            gameLogic.drawCard();
            
            // Add to game log
            addToGameLog("Player " + currentPlayer.getId(), "Drew Card", "");
            
            // Update UI to show the drawn card
            updateGameUI();
            
            // If player cannot play after drawing
            if (!gameLogic.hasPlayableCard()) {
                UOptionPane.showMessageDialog(frame,
                    "No playable cards. Your turn is over.", 
                    "Turn Ended", 
                    UOptionPane.INFORMATION_MESSAGE);
                
                // Move to next player
                gameLogic.moveToNextPlayer();
                updateGameUI();
                showNextPlayerTurnDialog();
            }
        }
    }
    
    private void sayUno() {
        if (!gameLogic.isGameOver()) {
            if (gameLogic.getCurrentPlayer().getHand().size() == 2) {
                UOptionPane.showMessageDialog(frame, "UNO!", "UNO Called", UOptionPane.INFORMATION_MESSAGE);
            } else {
                UOptionPane.showMessageDialog(frame, "You can only say UNO when you have 2 cards left!", "Invalid UNO", UOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void showNextPlayerTurnDialog() {
        // Create a JOptionPane to show who's turn is next
        Player nextPlayer = gameLogic.getCurrentPlayer();
        String message = "It's Player " + nextPlayer.getId() + "'s turn now.\n\n" +
                     "Please pass the device to Player " + nextPlayer.getId() + ".\n" +
                     "Make sure other players don't see your cards!";
        
        UOptionPane.showMessageDialog(frame, message, "Next Turn", UOptionPane.INFORMATION_MESSAGE);
        
        // Hide cards briefly when the dialog closes to prevent peeking
        hideAndRevealCards();
    }
    
    private void hideAndRevealCards() {
        // Briefly hide cards by replacing the card area with a blank panel
        if (playerCardsScroll != null) {
            playerCardsScroll.setVisible(false);
            
            // Show a message
            ULabel hiddenLabel = new ULabel("Click here when you're ready to see your cards", 0);
            hiddenLabel.setFont(new Font("MV Boli", Font.BOLD, 18));
            hiddenLabel.setTextColor(ORANGE_COLOR);
            hiddenLabel.setSize(700, 50);
            hiddenLabel.setLocation(20, 550);
            frame.getMainPanel().add(hiddenLabel);
            
            hiddenLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Show cards again
                    playerCardsScroll.setVisible(true);
                    frame.getMainPanel().remove(hiddenLabel);
                    frame.getMainPanel().repaint();
                }
            });
        }
    }
    
    private void showGameOverDialog() {
        Player winner = gameLogic.getWinner();
        String message = "Game Over! Player " + winner.getId() + " wins!";
        
        UOptionPane.showMessageDialog(frame, message, "Game Over", UOptionPane.INFORMATION_MESSAGE);
        
        // Return to player selection after a brief delay
        UTimer exitTimer = new UTimer(2000, e -> {
            new Page2();
            if (frame != null) {
                frame.dispose();
            }
        });
        exitTimer.setRepeats(false);
        exitTimer.start();
    }
    
    private void handleBackButtonClick() {
        System.out.println("Back button clicked");
        // Go back to the player selection screen
        new Page2();
        if (frame != null) {
            frame.dispose();
        }
    }
    
    private void handleCardClick(int cardIndex) {
        System.out.println("Card " + cardIndex + " clicked");
        
        // Check if the card is playable
        Card selectedCard = gameLogic.getCurrentPlayer().getHand().get(cardIndex);
        Card lastPlayedCard = gameLogic.getLastPlayedCard();
        
        if (selectedCard.isPlayable(lastPlayedCard)) {
            // For Wild cards, we need special handling to set the chosen color
            if (selectedCard instanceof ActionCard) {
                ActionCard actionCard = (ActionCard) selectedCard;
                if (actionCard.getAction() == ActionCard.Actions.Wild || 
                    actionCard.getAction() == ActionCard.Actions.DRAW_4_Wild) {
                    handleWildCardAction(actionCard, cardIndex);
                    return;
                }
            }
            
            // Get player and card info before playing the card
            Player currentPlayer = gameLogic.getCurrentPlayer();
            String cardString = gameLogic.cardToString(selectedCard);
            
            // Play the card with no color choice (it's not a wild card)
            gameLogic.playCard(cardIndex, null);
            
            // Add played card to game log
            addToGameLog("Player " + currentPlayer.getId(), "Played", cardString);
            
            // Update the UI
            updateGameUI();
            
            // Check if game has ended
            if (gameLogic.isGameOver()) {
                showGameOverDialog();
                return;
            }
            
            // Show dialog for next player's turn
            showNextPlayerTurnDialog();
        } else {
            // Show error message
            UOptionPane.showMessageDialog(frame, "This card cannot be played on the current card!", "Invalid Move", UOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleWildCardAction(ActionCard actionCard, int cardIndex) {
        // Create color selection dialog
        String[] colorOptions = {"Red", "Blue", "Green", "Yellow"};
        int colorChoice = UOptionPane.showOptionDialog(
            frame,
            "Choose a color:",
            "Color Selection",
            UOptionPane.DEFAULT_OPTION,
            UOptionPane.QUESTION_MESSAGE,
            null, colorOptions, colorOptions[0]);
        
        if (colorChoice >= 0) {
            String chosenColor = colorOptions[colorChoice];
            // Use the chosen color for the wild card image
            
            Player currentPlayer = gameLogic.getCurrentPlayer();
            gameLogic.playCard(cardIndex, chosenColor);
            
            String wildCardImagePath;
            if (actionCard.getAction() == ActionCard.Actions.Wild) {
                wildCardImagePath = "assets/cards/Black_Wild_" + chosenColor + ".png";
            } else if (actionCard.getAction() == ActionCard.Actions.DRAW_4_Wild) {
                wildCardImagePath = "assets/cards/Black_DRAW_4_Wild_" + chosenColor + ".png";
            } else {
                wildCardImagePath = "assets/cards/" + gameLogic.getLastPlayedCardString() + ".png";
            }
            
            currentCard.setImageFromPath(wildCardImagePath);
            
            addToGameLog(
                "Player " + currentPlayer.getId(),
                "Played WILD",
                actionCard.getAction() + " → " + chosenColor
            );
            
            if (gameLogic.isGameOver()) {
                showGameOverDialog();
                return;
            }
            
            showNextPlayerTurnDialog();
        }
    }
    
    private void addToGameLog(String player, String action, String card) {
        java.time.LocalTime now = java.time.LocalTime.now();
        String timeString = String.format("%02d:%02d", now.getMinute(), now.getSecond());
        
        Object[] newRow = new Object[4];
        newRow[LOG_PLAYER_COL] = player;
        newRow[LOG_ACTION_COL] = action;
        newRow[LOG_CARD_COL] = card;
        newRow[LOG_TIME_COL] = timeString;
        
        // Add row to table model
        tableModel.addRow(newRow);
        
        // Only update the table if it exists (to prevent NullPointerException)
        if (gameLogTable != null) {
            // Refresh table with updated model using our column constants
            gameLogTable.setModel(new UTableModel(
                tableModel.getDataArray(),
                logColumns
            ));
        }
        
        // Scroll to the bottom to show newest entries
        if (gameLogTable != null && gameLogTable.getRowCount() > 0) {
            gameLogTable.scrollRectToVisible(gameLogTable.getCellRect(
                gameLogTable.getRowCount() - 1, 0, true));
        }
    }
}
