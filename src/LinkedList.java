import java.util.Iterator;

public class LinkedList<E> implements Iterable<E> {
    private Link<E> head;
    private Link<E> tail;

    public LinkedList() {
        this.head = null;
        this.tail = null;
    }
    
    public boolean isEmpty() {
    	return this.head == null;
    }

    public void addLast(E element) {
        if (element == null) {
            throw new RuntimeException("element is null.");
        }
        
        Link<E> newLink = new Link<>(element);
        if (this.isEmpty()) {
            this.head = newLink;
            this.tail = newLink;
        }
        else {
        	this.tail.setNext(newLink);
        	this.tail = newLink;
        }
    }

    public Iterator<E> iterator() {
        return new LinkedListIterator<>(this.head);
    }
}