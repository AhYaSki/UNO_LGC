package src.logicGui;
import java.util.ArrayList;

public abstract class Player {
    private final int id;
    private final ArrayList<Card> hand;
    private Card.Colors forcedColor;
    private int choice;


    public void setPlayIndex(int newChoice) {
        this.choice = newChoice;
    }
    public Player(int id) {
        this.id = id;
        this.hand = new ArrayList<>();
        this.forcedColor = null;
    }
    public int getId() {
        return this.id;
    }
    public ArrayList<Card> getHand() {
        return this.hand;
    }
    public Card.Colors getForcedColor() {
        return this.forcedColor;
    }
    public void setForcedColor(Card.Colors forcedColor) {
        this.forcedColor = forcedColor;
    }

    public void drawCard(Deck deck) {
        this.hand.add(deck.drawCard());
    }

    public ArrayList<Card> getPlayableCards(Card lastPlayedCard) {
        ArrayList<Card> playableCards = new ArrayList<>();
        for (Card card : this.hand) {
            if (card.isPlayable(lastPlayedCard)) {
                playableCards.add(card);
            }
        }
        return playableCards;
    }
    public boolean hasPlayableCard(Card lastPlayedCard) {
        return !this.getPlayableCards(lastPlayedCard).isEmpty();
    }
    public abstract Card.Colors chooseColor();
    public abstract Card chooseCard(Card lastPlayedCard);
    public Card play(Card lastPlayedCard) {
        if (!hasPlayableCard(lastPlayedCard)) {
            drawCard(new Deck()); // Assuming the deck is accessible
            return null;
        }
        Card choosedCard = chooseCard(lastPlayedCard);
        this.hand.remove(choosedCard);
        return choosedCard;
    }
    public Card play(Card lastPlayedCard,int indexC) {
        if(this instanceof HumanPlayer){
            ((HumanPlayer)this).setPlayIndex(indexC);
        }
        Card choosedCard = chooseCard(lastPlayedCard);
        this.hand.remove(choosedCard);
        return choosedCard;
    }

    public int getChoice() {
        return choice;
    }
}
