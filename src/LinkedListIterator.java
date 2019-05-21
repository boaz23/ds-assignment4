import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListIterator<E> implements Iterator<E> {
    private Link<E> current;

    public LinkedListIterator(Link<E> start) {
        this.current = start;
    }

    public boolean hasNext() {
        return this.current != null;
    }

    public E next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        
        E data = this.current.getData();
        this.current = this.current.getNext();
        return data;
    }
}