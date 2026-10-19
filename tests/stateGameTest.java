import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class stateGameTest {

    private stateGame state;

    @Before
    public void setUp() {
        state = new stateGame();
    }

    @Test
    public void GameNotFInshed() {
        assertFalse(state.isFinished());
    }

    @Test
    public void GameFinished() {
        state.endGame();
        assertTrue(state.isFinished());
    }

    @Test
    public void EndCalledTwice() {
        state.endGame();
        state.endGame();
        assertTrue(state.isFinished());
    }

}
