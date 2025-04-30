package src.logicGui;

public class NonActionCard extends Card {
    private final int value;
    public NonActionCard(int value,Colors color) {
        super(color);
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Please Enter A Valid Value");
        }
        this.value = value;
    }
    public int getValue() {
        return this.value;
    }
    @Override
    public boolean isPlayable(Card lastPlayedCard) {
        if (lastPlayedCard instanceof NonActionCard) {
            return ((NonActionCard) lastPlayedCard).getValue() == this.value;
        }
        return super.isPlayable(lastPlayedCard);
    }
}
