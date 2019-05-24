import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator for lines of text files
 * 
 *
 */
public class FileLinesIterator implements Iterator<String>, Iterable<String>, Closeable {
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private String nextLine;

    public FileLinesIterator(String filePath) {
    	if (filePath == null || filePath.equals("")) {
    		throw new RuntimeException("filePath is null or empty.");
    	}
    	
        try {
            this.fileReader = new FileReader(filePath);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("file not found", e);
        }
        this.bufferedReader = new BufferedReader(this.fileReader);
    }


    /**
     * Returns whether the file has a next line
     */
    @Override
    public boolean hasNext() {
        if (this.nextLine != null) {
            return true;
        }
        else {
            try {
                this.nextLine = this.bufferedReader.readLine();
                return (this.nextLine != null);
            }
            catch (IOException e) {
                throw new RuntimeException("IO exception: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Returns the next line of the file
     */
    @Override
    public String next() {
        if (this.nextLine != null || this.hasNext()) {
            String line = this.nextLine;
            this.nextLine = null;
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
    		throw new RuntimeException("IO exception: " + e.getMessage(), e);
    	}
    }

    @Override
    public Iterator<String> iterator() {
        return this;
    }
}
