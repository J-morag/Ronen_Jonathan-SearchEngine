package Indexing.Index.IO;

import java.io.*;
import java.util.Map;

public class BasicIntArrayMapOutputStream {


    long filePointer = 0;
    BufferedWriter postingsFile;

    /**
     * if the file doesn't exist, creates it.
     * if the file exists, clears it!
     * @param pathToFile
     * @throws IOException
     */
    public BasicIntArrayMapOutputStream(String pathToFile) throws IOException {
        this.postingsFile = new BufferedWriter(new PrintWriter(pathToFile));
    }

    public long getCursor() {
        return filePointer;
    }

    public long write(Map<Integer, int[]> map) throws NullPointerException, IOException {
        long startIdx = getCursor();

        for (Map.Entry<Integer, int[]> entry: map.entrySet()
             ) {
            postingsFile.write("Key=" + entry.getKey() + ", ");
            postingsFile.write("values=[");
            int [] arr = entry.getValue();
            for (int i = 0; i < arr.length ; i++) {
                postingsFile.write("" + String.valueOf(arr[i]) + (i != arr.length-1 ? ',' : ""));
            }
            postingsFile.write("] ; ");
        }
        postingsFile.write('\n');

        return startIdx;
    }


    public void flush() throws IOException {
        postingsFile.flush();
    }

    public void close() throws IOException {
        postingsFile.close();
    }

}
