package src.logicGui;

import java.util.ArrayList;
import java.util.List;

public class GameRest {
    private final ArrayList<Player> players;
    private boolean isReversed;
    private boolean isGameOver;
    private final Deck deck;
    private final ArrayList<Card> discardPile;
    private int currentPlayerIndex;
    private Player winner;

    public GameRest(int numberOfHumanPlayers, int numberOfPlayers) {
        this.players = new ArrayList<>();
        this.isGameOver = false;
        this.isReversed = false;
        this.deck = new Deck();
        this.discardPile = new ArrayList<>();
        this.winner = null;
        this.initializeGame(numberOfHumanPlayers, numberOfPlayers);
    }

    private void initializeGame(int numberOfHumanPlayers, int numberOfPlayers) {
        this.initializePlayers(numberOfHumanPlayers, numberOfPlayers);
        this.distributeCards();
        this.currentPlayerIndex = (int) (Math.random() * players.size());
        Card firstCard = deck.drawCard();
        while (firstCard instanceof ActionCard firstActionCard) {
            if (firstActionCard.getAction() == ActionCard.Actions.DRAW_4_Wild) {
                deck.addToDeck(firstCard);
                deck.shuffleDeck();
                firstCard = deck.drawCard();
            } else if (firstActionCard.getAction() == ActionCard.Actions.Wild) {
                this.players.get(this.currentPlayerIndex).chooseColor();
                this.moveToNextPlayer();
                break;
            } else {
                this.isReversed = true;
                this.moveToNextPlayer();
                this.isReversed = false;
                this.applyCardEffect(firstActionCard);
                if (firstActionCard.getAction() != ActionCard.Actions.Reverse) {
                    this.moveToNextPlayer();
                }
                break;
            }
        }
        discardPile.add(firstCard);
    }

    private void initializePlayers(int numberOfHumanPlayers, int numberOfPlayers) {
        for (int i = 0; i < numberOfHumanPlayers; i++) {
            this.players.add(new HumanPlayer(i));
        }
        for (int i = numberOfHumanPlayers; i < numberOfPlayers; i++) {
            this.players.add(new BotPlayer(i));
        }
    }

    private void distributeCards() {
        for (Player player : players) {
            for (int j = 0; j < 7; j++) {
                player.drawCard(this.deck);
            }
        }
    }

    public void playCard(int cardIndex, String chosenColor) {
        Player currentPlayer = getCurrentPlayer();
        Card playedCard = currentPlayer.getHand().get(cardIndex);

        if (playedCard instanceof ActionCard actionCard) {
            if (actionCard.getAction() == ActionCard.Actions.Wild || actionCard.getAction() == ActionCard.Actions.DRAW_4_Wild) {
                if (chosenColor == null) {
                    throw new IllegalArgumentException("A color must be chosen for Wild or DRAW_4_Wild cards.");
                }
                playedCard.setForcedColor(Card.Colors.valueOf(chosenColor));
            }
        }

        discardPile.add(playedCard);
        currentPlayer.getHand().remove(playedCard);

        if (currentPlayer.getHand().isEmpty()) {
            this.isGameOver = true;
            this.winner = currentPlayer;
        } else {
            if (playedCard instanceof ActionCard) {
                applyCardEffect((ActionCard) playedCard);
            }
            moveToNextPlayer();
        }
    }

    public void drawCard() {
        Player currentPlayer = getCurrentPlayer();
        currentPlayer.drawCard(deck);
    }

    public boolean hasPlayableCard() {
        return getCurrentPlayer().hasPlayableCard(getLastPlayedCard());
    }

    public void applyCardEffect(ActionCard actionCard) {
        switch (actionCard.getAction()) {
            case Skip -> moveToNextPlayer();
            case Reverse -> isReversed = !isReversed;
            case Draw_2 -> {
                Player nextPlayer = getNextPlayer();
                nextPlayer.drawCard(deck);
                nextPlayer.drawCard(deck);
                moveToNextPlayer();
            }
            case DRAW_4_Wild -> {
                Player nextPlayer = getNextPlayer();
                for (int i = 0; i < 4; i++) {
                    nextPlayer.drawCard(deck);
                }
                moveToNextPlayer();
            }
            default -> {
            }
        }
    }

    public Player getNextPlayer() {
        return players.get((currentPlayerIndex + (isReversed ? players.size() - 1 : 1)) % players.size());
    }

    public void moveToNextPlayer() {
        currentPlayerIndex = isReversed ? (currentPlayerIndex + players.size() - 1) % players.size() : (currentPlayerIndex + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Card getLastPlayedCard() {
        return discardPile.get(discardPile.size() - 1);
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isReversed() {
        return isReversed;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    // New Methods for GUI Interaction

    public List<String> getCurrentPlayerCards() {
        List<String> cardStrings = new ArrayList<>();
        for (Card card : getCurrentPlayer().getHand()) {
            cardStrings.add(cardToString(card)); // Use updated cardToString method
        }
        return cardStrings;
    }

    public String getLastPlayedCardString() {
        Card lastCard = getLastPlayedCard();
        return cardToString(lastCard); // Use updated cardToString method
    }

    public boolean isCurrentPlayerBot() {
        return getCurrentPlayer() instanceof BotPlayer;
    }

    public void botPlayTurn() {
        if (isCurrentPlayerBot()) {
            BotPlayer bot = (BotPlayer) getCurrentPlayer();
            if (!hasPlayableCard()) {
                drawCard();
            } else {
                Card playedCard = bot.getHand().get(0); // Simplified bot logic: play the first playable card
                String chosenColor = null;

                if (playedCard instanceof ActionCard actionCard) {
                    if (actionCard.getAction() == ActionCard.Actions.Wild || actionCard.getAction() == ActionCard.Actions.DRAW_4_Wild) {
                        // Bot chooses a color
                        chosenColor = bot.chooseColor().toString();
                        playedCard.setForcedColor(Card.Colors.valueOf(chosenColor));
                    }
                }

                playCard(0, chosenColor);

                // Display the chosen color if the bot played a Wild card
                if (chosenColor != null) {
                    System.out.println("Bot chose color: " + chosenColor);
                }
            }
        }
    }

    public String cardToString(Card card) {
        if (card instanceof ActionCard actionCard) {
            return card.getColor() + "_" + actionCard.getAction(); // Include action for ActionCard
        } else if (card instanceof ValueCard valueCard) {
            return card.getColor() + "_" + valueCard.getValue(); // Include value for ValueCard
        }
        return card.getColor().toString(); // Default to color for other cards
    }
}
