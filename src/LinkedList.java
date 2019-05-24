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
        
        Link<E> newLink = this.createLink(element);
        if (this.isEmpty()) {
            this.head = newLink;
            this.tail = newLink;
        }
        else {
        	this.tail.setNext(newLink);
        	this.tail = newLink;
        }
    }

    protected Link<E> createLink(E data) {
         return new Link<E>(data);
    }

    public boolean contains(E element) {
        boolean found = false;
        for (E data : this){
            if (data.equals(element)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public Iterator<E> iterator() {
        return new LinkedListIterator<>(this.head);
    }
}