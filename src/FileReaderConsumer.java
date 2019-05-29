import java.io.BufferedReader;
import java.io.IOException;

public interface FileReaderConsumer<T> {
    T accept(BufferedReader reader) throws IOException;
}
