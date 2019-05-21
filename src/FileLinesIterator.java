import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator for lines of text files
 * 
 *
 */
public class FileLinesIterator implements Iterator<String>, Closeable {
    private FileReader fileReader;
    private BufferedReader bufferedReader;

    private String nextLine;

    public FileLinesIterator(String filePath) {
        try {
            this.fileReader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("io exception", e);
        }
        this.bufferedReader = new BufferedReader(this.fileReader);
    }


    /**
     * Returns whether the file has a next line
     */
    @Override
    public boolean hasNext() {
        if (nextLine != null) {
            return true;
        }
        else {
            try {
                nextLine = this.bufferedReader.readLine();
                return (nextLine != null);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Returns the next line of the file
     */
    @Override
    public String next() {
        if (nextLine != null || this.hasNext()) {
            String line = nextLine;
            nextLine = null;
            return line;
        }
        else {
            throw new NoSuchElementException();
        }
    }
    
    @Override
    public void close() {
    	try {
	    	if (this.fileReader != null) {
	    		this.fileReader.close();
	    	}
	    	if (this.bufferedReader != null) {
	    		this.bufferedReader.close();
	    	}
    	}
    	catch (IOException e) {
    		throw new RuntimeException(e);
    	}
    }
}
