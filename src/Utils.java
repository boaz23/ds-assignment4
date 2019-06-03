import java.io.*;
import java.text.DecimalFormat;
import java.util.function.Consumer;

class Utils {
    static final int p = 15486907;
    private static final int BYTE_BASE = 256;
    private static final long NANO_SEC_TO_MS = 1000000;

    /**
     * Hash the given string to a integer using horner's rule
     * on the bytes composing the string
     * @param password The password
     */
    static int hornerPassword(String password) {
        if (password == null) {
            throw new RuntimeException("password is null.");
        }

        return hornerPassword(password.getBytes());
    }

    /**
     * Horner's rule algorithm for calculating a polynomial value modulo p.
     * @param bytes The bytes array. We can think of the byte array as
     * P(x) = a_n*x^n+...+a_1*x+a_0 when x=256 and a_n = bytes[0], ..., a_1=bytes[bytes.length -1].
     * We then apply horner's rule for p.
     * @return The 'polynomial' (represented by the byte array) modulo p when x=256
     */
    private static int hornerPassword(byte[] bytes) {
        if (bytes == null) {
            throw new RuntimeException("bytes is null.");
        }

        long horner;
        if (bytes.length == 0) {
            horner = 0;
        }
        else {
            horner = bytes[0];
            for (int i = 1; i < bytes.length; i++) {
                // (A * B) mod C = ((A mod C) * (B mod C)) mod C
                horner = bytes[i] + (((horner % p) * (BYTE_BASE % p)) % p);
            }
        }

        return (int)horner;
    }

    /**
     * Perform an action on file reader of the file at the specified path.
     * The function rethrows any IO exception as wrapping RuntimeException
     * and closes before exiting.
     * @param filePath The path to file
     * @param action The action on the file reader
     * @param <T> The type returned from the action
     * @return The value returned from the action
     */
    static <T> T consumeFileReader(String filePath, FileReaderConsumer<T> action) {
        checkFilePath(filePath);
        if (action == null) {
            throw new RuntimeException("file reader action is null.");
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            return action.accept(reader);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("file not found", e);
        }
        catch (IOException | UncheckedIOException e) {
            throw new RuntimeException("io exception", e);
        }
        finally {
            closeReader(reader);
        }
    }

    private static void checkFilePath(String filePath) {
        if (filePath == null || filePath.equals("")) {
            throw new RuntimeException("filePath is null or empty.");
        }
    }

    private static void closeReader(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            }
            catch (IOException e) {
                throw new RuntimeException("io exception", e);
            }
        }
    }

    /**
     * Perform an operation on each line of the file at the specified path
     * @param filePath The file path
     * @param action The action to perform on each line
     */
    static void iterateFileLines(String filePath, final Consumer<String> action) {
        if (action == null) {
            throw new RuntimeException("action is null.");
        }

        consumeFileReader(filePath, reader -> {
            reader.lines().forEachOrdered(action);
            return null;
        });
    }

    /**
     * Calculates and formats the time it takes to search all the
     * keys which are specified in the file at the given path
     * @param filePath The path to the file containing the keys as lines
     * @param search The search function of the data structure to perform
     * @return The formatted amount of time in milliseconds it took for entire search operation
     * (as a whole)
     */
    static String getSearchTime(String filePath, final Consumer<String> search) {
        if (search == null) {
            throw new RuntimeException("search is null.");
        }

        return consumeFileReader(filePath, reader -> getSearchTimeCore(reader, search));
    }

    /**
     * Formats the nano time difference to milliseconds
     * @param startNanoTime The start time (in nanoseconds)
     * @param endNanoTime The end time (in nanoseconds)
     * @return The formatted time difference in milliseconds
     */
    static String formatMillisecondsDiff(long startNanoTime, long endNanoTime) {
        return formatMillisecondsDiff(endNanoTime - startNanoTime);
    }

    /**
     * Formats nanoseconds to milliseconds
     * @param diff The nanoseconds value
     */
    static String formatMillisecondsDiff(long diff) {
        double milliseconds = (double)diff / NANO_SEC_TO_MS;
        DecimalFormat format = new DecimalFormat("#.####");
        return format.format(milliseconds);
    }

    /**
     * Calculates and formats the time it takes to search all the
     * keys which are specified in the file
     * @param reader The reader of the file containing the keys as lines
     * @param search The search function of the data structure to perform
     * @return The formatted amount of time in milliseconds it took for entire search operation
     * (as a whole)
     */
    private static String getSearchTimeCore(BufferedReader reader, Consumer<String> search) throws IOException {
        String password;
        long startNanoTime = System.nanoTime();
        while ((password = reader.readLine()) != null) {
            search.accept(password);
        }

        long endNanoTime = System.nanoTime();
        return Utils.formatMillisecondsDiff(startNanoTime, endNanoTime);
    }
}
