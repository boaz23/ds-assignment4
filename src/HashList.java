public class HashList extends LinkedList<Integer> {
    @Override
    protected Link<Integer> createLink(Integer data) {
        if (data == null) {
            throw new RuntimeException("data is null.");
        }

        return new HashListElement(data);
    }
}
