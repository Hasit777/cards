import static org.junit.Assert.*;
import java.util.HashSet;
import org.junit.Test;

public class CardTest
{
    @Test(expected = IllegalArgumentException.class)
    public void negativeCard(){
        new Card(-1);
    }
    
    @Test
    public void validCards(){
        assertEquals(0, new Card(0).getValue());
        assertEquals(Integer.MAX_VALUE, new Card(Integer.MAX_VALUE).getValue());
    }
    
    @Test
    public void equalCardsBehaviour(){
        Card first = new Card(3);
        Card second = new Card(3);
        Card different = new Card(4);
        HashSet<Card> cards = new HashSet<>();
        cards.add(first);
        cards.add(second);
        cards.add(different);
        assertEquals(first, second);
        assertNotEquals(first, different);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(2, cards.size());
    }
    
    @Test
    public void cardOutCheck(){
        assertEquals("12", new Card(12).toString());
        assertEquals("0", new Card(0).toString());
    }
}