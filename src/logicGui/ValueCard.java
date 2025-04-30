package src.logicGui;

public class ValueCard extends Card {
    private final int value;

    public ValueCard(int value,Colors color) {
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
        if (lastPlayedCard instanceof ValueCard) {
            return ((ValueCard) lastPlayedCard).getValue() == this.value || super.isPlayable(lastPlayedCard);
        }
        return super.isPlayable(lastPlayedCard);
    }

}
