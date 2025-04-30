package src.logicGui;

public class ActionCard extends Card {
    public enum Actions {
        Skip, Reverse, Draw_2, Wild, DRAW_4_Wild;

        static boolean isCompatible(Actions action, Colors color) {
            try {
                if (color == Colors.Black) {
                    return action == Actions.DRAW_4_Wild || action == Actions.Wild;
                } else {
                    return action == Actions.Skip || action == Actions.Reverse || action == Actions.Draw_2;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Error checking compatibility: " + e.getMessage(), e);
            }
        }
    }

    private final Actions action;

    public ActionCard(Actions action, Colors color) {
        super(color);
        try {
            if (Actions.isCompatible(action, color)) {
                this.action = action;
            } else {
                throw new IllegalArgumentException("Please Enter A Compatible Action And Color");
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to create ActionCard: " + e.getMessage(), e);
        }
    }

    public Actions getAction() {
        return this.action;
    }

    @Override
    public boolean isPlayable(Card lastPlayedCard) {
        try {
            if (lastPlayedCard instanceof ActionCard) {
                return ((ActionCard) lastPlayedCard).getAction() == this.action || super.isPlayable(lastPlayedCard);
            }
            return super.isPlayable(lastPlayedCard);
        } catch (Exception e) {
            throw new RuntimeException("Error checking playability: " + e.getMessage(), e);
        }
    }
}

