package src.logicGui;

import java.util.ArrayList;

public class GameGui {
    private final ArrayList<Player> players;
    private boolean isReversed;
    private boolean isGameOver;
    private final Deck deck; // Deck is defined as a private field
    private final ArrayList<Card> discardPile;
    private int currentPlayerIndex;
    private Player winner;

    public GameGui(int numberOfHumanPlayers,int numberOfPlayers) {
        this.players = new ArrayList<>();
        this.isGameOver = false;
        this.isReversed = false;
        this.deck = new Deck(); // Initialize the deck
        this.discardPile = new ArrayList<>();
        this.winner = null;
        this.initializeGame(numberOfHumanPlayers, numberOfPlayers);
    }

    // Add this method to provide access to the deck
    public Deck getDeck() {
        return this.deck;
    }

    private void distributeCards() {
        for (Player player : players) {
            for (int j = 0; j < 7; j++) {
                player.drawCard(this.deck);
            }
        }
    }

    private void initializePlayers(int numberOfHumanPlayers, int numberOfPlayers) {
        // Add human players
        for (int i = 0; i < numberOfHumanPlayers; i++) {
            this.players.add(new HumanPlayer(i)); // Add HumanPlayer
        }
        // Add bot players
        for (int i = numberOfHumanPlayers; i < numberOfPlayers; i++) {
            this.players.add(new BotPlayer(i)); // Add BotPlayer
        }
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

    public String cardToString(Card card) {
        if (card instanceof ActionCard) {
            return card.getColor() + "_" + ((ActionCard) card).getAction();
        } else if (card instanceof ValueCard) {
            return card.getColor() + "_" + ((ValueCard) card).getValue();
        }
        return card.getColor().toString();
    }

    public void executeTurn(int cardChoice, String colorChoice) {
        Player currentPlayer = this.getCurrentPlayer();

        if (currentPlayer instanceof HumanPlayer humanPlayer) {
            // Handle human player's turn
            humanPlayer.setPlayIndex(cardChoice);

            if (!currentPlayer.hasPlayableCard(this.getLastPlayedCard())) {
                currentPlayer.drawCard(this.deck);
                if (!currentPlayer.hasPlayableCard(this.getLastPlayedCard())) {
                    if (currentPlayer.getForcedColor() != null) {
                        this.getNextPlayer().setForcedColor(currentPlayer.getForcedColor());
                    }
                    this.moveToNextPlayer();
                    return;
                }
            }

            Card lastPlayedCard = currentPlayer.play(this.getLastPlayedCard());

            if (lastPlayedCard instanceof ActionCard actionCard &&
                    (actionCard.getAction() == ActionCard.Actions.Wild || actionCard.getAction() == ActionCard.Actions.DRAW_4_Wild)) {
                Card.Colors chosenColor = humanPlayer.chooseColor(colorChoice);
                lastPlayedCard.setForcedColor(chosenColor); // Set the forced color on the card
            }

            this.discardPile.add(lastPlayedCard);

            if (currentPlayer.getHand().isEmpty()) {
                this.winner = currentPlayer;
                this.isGameOver = true;
                return;
            }

            if (lastPlayedCard instanceof ActionCard) {
                applyCardEffect((ActionCard) lastPlayedCard);
            }

            this.moveToNextPlayer();
        } else if (currentPlayer instanceof BotPlayer botPlayer) {
            // Handle bot player's turn
            ArrayList<Card> playableCards = botPlayer.getPlayableCards(this.getLastPlayedCard());

            if (playableCards.isEmpty()) {
                // Bot has no playable cards, so it draws a card
                botPlayer.drawCard(this.deck);
                Card drawnCard = botPlayer.getHand().get(botPlayer.getHand().size() - 1);

                if (drawnCard.isPlayable(this.getLastPlayedCard())) {
                    // If the drawn card is playable, the bot plays it
                    Card.Colors chosenColor = null;
                    if (drawnCard instanceof ActionCard actionCard) {
                        if (actionCard.getAction() == ActionCard.Actions.Wild || actionCard.getAction() == ActionCard.Actions.DRAW_4_Wild) {
                            chosenColor = botPlayer.chooseColor(); // Bot chooses a color for Wild cards
                        }
                    }
                    this.discardPile.add(drawnCard);
                    if (drawnCard instanceof ActionCard) {
                        applyCardEffect((ActionCard) drawnCard);
                    }
                } else {
                    // If the drawn card is not playable, the bot skips its turn
                    this.moveToNextPlayer();
                }
            } else {
                // Bot has playable cards, so it chooses one randomly
                Card chosenCard = botPlayer.chooseCard(this.getLastPlayedCard());
                Card.Colors chosenColor = null;

                if (chosenCard instanceof ActionCard actionCard) {
                    if (actionCard.getAction() == ActionCard.Actions.Wild || actionCard.getAction() == ActionCard.Actions.DRAW_4_Wild) {
                        chosenColor = botPlayer.chooseColor(); // Bot chooses a color for Wild cards
                    }
                }

                this.discardPile.add(chosenCard);
                if (chosenCard instanceof ActionCard) {
                    applyCardEffect((ActionCard) chosenCard);
                }
            }

            if (currentPlayer.getHand().isEmpty()) {
                this.winner = currentPlayer;
                this.isGameOver = true;
                return;
            }

            this.moveToNextPlayer();
        }
    }

    public void applyCardEffect(ActionCard actionCard) {
        switch (actionCard.getAction()) {
            case Skip:
                System.out.println("Player " + this.getNextPlayer().getId() + " is skipped!");
                this.moveToNextPlayer();
                break;
            case Reverse:
                this.isReversed = !isReversed;
                break;
            case Draw_2:
                Player nextPlayer = this.getNextPlayer();
                nextPlayer.drawCard(this.deck);
                nextPlayer.drawCard(this.deck);
                this.moveToNextPlayer();
                break;
            case DRAW_4_Wild:
                Card.Colors chosenColor = this.getCurrentPlayer().chooseColor();
                nextPlayer = this.getNextPlayer();
                for (int i = 0; i < 4; i++) {
                    nextPlayer.drawCard(this.deck);
                }
                this.moveToNextPlayer();
                this.getNextPlayer().setForcedColor(chosenColor);
                break;
            case Wild:
                Card.Colors choosenColor = this.getCurrentPlayer().chooseColor();
                this.getNextPlayer().setForcedColor(choosenColor);
                break;
        }
    }

    public Player getNextPlayer() {
        return this.players.get((this.currentPlayerIndex + (this.isReversed ? this.players.size() - 1 : 1)) % players.size());
    }

    public void moveToNextPlayer() {
        this.currentPlayerIndex = isReversed ? (this.currentPlayerIndex + this.players.size() - 1) % this.players.size() : (this.currentPlayerIndex + 1) % this.players.size();
    }

    public Player getCurrentPlayer() {
        return this.players.get(this.currentPlayerIndex);
    }

    public Card getLastPlayedCard() {
        return this.discardPile.getLast();
    }

    public boolean isGameOver() {
        return this.isGameOver;
    }

    public Player getWinner() {
        return winner;
    }
}
