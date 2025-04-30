package src.logicGui;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deck;

    public Deck() {
        try {
            this.deck = new ArrayList<>();
            this.initializeDeck();
            this.shuffleDeck();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing deck: " + e.getMessage(), e);
        }
    }

    public void initializeDeck() {
        try {
            Card.Colors[] colors = Card.Colors.values();
            ActionCard.Actions[] actions = ActionCard.Actions.values();
            for (Card.Colors color : colors) {
                if (color != Card.Colors.Black) {
                    deck.add(new ValueCard(0, color));
                    for (int j = 1; j <= 9; j++) {
                        deck.add(new ValueCard(j, color));
                        deck.add(new ValueCard(j, color));
                    }
                    for (int j = 0; j <= 2; j++) {
                        deck.add(new ActionCard(actions[j], color));
                        deck.add(new ActionCard(actions[j], color));
                    }
                } else {
                    for (int j = 3; j < actions.length; j++) {
                        deck.add(new ActionCard(actions[j], color));
                        deck.add(new ActionCard(actions[j], color));
                        deck.add(new ActionCard(actions[j], color));
                        deck.add(new ActionCard(actions[j], color));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error initializing deck cards: " + e.getMessage(), e);
        }
    }

    public Card drawCard() {
        try {
            if (deck.isEmpty()) {
                throw new IllegalStateException("Deck is empty. Cannot draw a card.");
            }
            return deck.remove(deck.size() - 1);
        } catch (Exception e) {
            throw new RuntimeException("Error drawing card: " + e.getMessage(), e);
        }
    }

    public void shuffleDeck() {
        try {
            Collections.shuffle(deck);
        } catch (Exception e) {
            throw new RuntimeException("Error shuffling deck: " + e.getMessage(), e);
        }
    }

    public ArrayList<Card> getDeck() {
        return this.deck;
    }

    public void addToDeck(Card card) {
        try {
            this.deck.add(card);
        } catch (Exception e) {
            throw new RuntimeException("Error adding card to deck: " + e.getMessage(), e);
        }
    }
}



