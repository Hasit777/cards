public class stateGame {

    private volatile boolean finished = false;

    public boolean isFinished() {
        return finished;
    }

    public void endGame() {
        finished = true;
    }
}
