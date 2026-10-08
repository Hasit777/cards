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
            StringBuilder sb = new StringBuilder("deck").append(id).append(" contents:");
            for (Card c : cards) {
                sb.append(' ').append(c);
            }
            return sb.toString();
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