class Link<E> {
    private E data;
    private Link<E> next;

    public Link(E data, Link<E> next) {
        this.data = data;
        this.next = next;
    }

    public Link(E data) {
        this(data, null);
    }

    public Link<E> getNext() {
        return this.next;
    }

    public void setNext(Link<E> next) {
        this.next = next;
    }

    public E getData() {
        return this.data;
    }

    public String toString() {
        return this.data.toString();
    }
}