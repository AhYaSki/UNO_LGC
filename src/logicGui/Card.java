package src.logicGui;

public class Card {
    public enum Colors {
        Red, Green, Black, Yellow, Blue
    }

    private final Colors color;
    private Colors forcedColor; // Add this field

    public Card(Colors color) {
        this.color = color;
        this.forcedColor = null; // Initialize to null
    }

    public Colors getColor() {
        return this.color;
    }

    public Colors getForcedColor() {
        return this.forcedColor;
    }

    public void setForcedColor(Colors forcedColor) {
        this.forcedColor = forcedColor;
    }

    public boolean isPlayable(Card lastPlayedCard) {
        if (this.color == Colors.Black) {
            return true; // Wild cards can always be played
        }
        
        if (lastPlayedCard.getForcedColor() != null) {
            return this.color == lastPlayedCard.getForcedColor();
        }
        
        return lastPlayedCard.getColor() == this.color;
    }
}
