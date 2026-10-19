//basically allows you to create an object called card that allows one variable in its constructor for its
//vairable and allows you to call its value, compare if it is equal, convert it to a stribg
// and return its hash code with functions
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
