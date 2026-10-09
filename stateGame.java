public class stateGame {

    private boolean finished = false;

    public boolean isFinished() {
        return finished;
    }

    public void endGame() {
        finished = true;
    }
}
