/* SOOOOOOOOO this code is to do with card deck where we have to lock down the deck so that more than one thread cant interact with it at once, and we have encaspulated the cards so that they cannot be changed by the threads themselves
we use draw() to take a card from the top and we use add() to add a card to the bottom of the deck but as add and draw are meant to be one atomic action we will need to lock 2 decks at once and then unlock 2 for the players action
the size() getst he size of the deck isempty() checks if the deck is empty getContents() gets the an array of the deck lock denies access to external threads and unlock() unlocks it
contentsLine() prints the deck as a readable string and writeOutputFile creates a file which holds relevant infomation*/


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
public final class CardDeck {

    private final int id;
    private final List<Card> cards = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    public CardDeck(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("Deck id must be at least 1 " + id);
        }
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public Card draw() {
        lock.lock();
        try {
            if (cards.isEmpty()) {
                return null;
            }
            return cards.remove(0);
        } finally {
            lock.unlock();
        }
    }
    public void add(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot add a null card to deck " + id);
        }
        lock.lock();
        try {
            cards.add(card);
        } finally {
            lock.unlock();
        }
    }
    public int size() {
        lock.lock();
        try {
            return cards.size();
        } finally {
            lock.unlock();
        }
    }
    public boolean isEmpty() {
        lock.lock();
        try {
            return cards.isEmpty();
        } finally {
            lock.unlock();
        }
    }
    public List<Card> getContents() {
        lock.lock();
        try {
            return new ArrayList<>(cards);
        } finally {
            lock.unlock();
        }
    }
    public void lock() {
        lock.lock();
    }
    public void unlock() {
        lock.unlock();
    }
    public String contentsLine() {
        lock.lock();
        try {
            String results = "deck" + id + " contents:";
            for (Card card : cards) {
                results += " " + card.getValue();
            }
            return results;
        } finally {
            lock.unlock();
        }
    }
    public void writeOutputFile(String directory) throws IOException {
        File dir = new File(directory);
        dir.mkdirs();
        File file = new File(dir, "deck" + id + "_output.txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println(contentsLine());
        }
    }
    public void writeOutputFile() throws IOException {
        writeOutputFile(".");
    }
    @Override
    public String toString() {
        return contentsLine();
    }
}
