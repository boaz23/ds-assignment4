public class HashList extends LinkedList<Integer> {
    @Override
    protected Link<Integer> createLink(Integer data) {
        return new HashListElement(data);
    }
}
