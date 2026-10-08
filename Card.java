public final class Card{
    private final int value;
    public Card(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Card value must be non-negative: " + value);
        }
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Card)) {
            return false;
        }
        return value == ((Card) other).value;
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
    @Override
    public String toString() {
        return Integer.toString(value);
    }

        
    
}