package src.logicGui;

import java.util.ArrayList;

public class HumanPlayer extends Player {

    public HumanPlayer(int id) {
        super(id);
    }

    @Override
    public Card.Colors chooseColor() {
        try {
            return Card.Colors.Red; // Default to Red
        } catch (Exception e) {
            throw new RuntimeException("Error choosing color: " + e.getMessage(), e);
        }
    }

    public Card.Colors chooseColor(String color) {
        try {
            return Card.Colors.valueOf(color);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid color. Please provide Red, Green, Blue, or Yellow.");
        }
    }

    public Card chooseCard(Card lastPlayableCard) {
        try {
            ArrayList<Card> playableCards = this.getPlayableCards(lastPlayableCard);
            if (this.getChoice() < 0 || this.getChoice() >= playableCards.size()) {
                throw new IllegalArgumentException("Invalid choice index. Please provide a valid index.");
            }
            return playableCards.get(this.getChoice());
        } catch (Exception e) {
            throw new RuntimeException("Error choosing card: " + e.getMessage(), e);
        }
    }
}
