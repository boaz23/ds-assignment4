import java.io.BufferedReader;
import java.io.IOException;

/**
 * An action to perform on a reader
 * @param <T> The type of value to return from the action
 */
interface FileReaderConsumer<T> {
    T accept(BufferedReader reader) throws IOException;
}
