package Indexing.Index.IO;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class IntToIntArrayMapOutputStream {

    long filePointer = 0;
    BufferedOutputStream postingsFile;

    /**
     * if the file doesn't exist, creates it.
     * if the file exists, clears it!
     * @param pathToFile
     * @throws IOException
     */
    public IntToIntArrayMapOutputStream(String pathToFile) throws IOException {
        this.postingsFile = new BufferedOutputStream( new FileOutputStream(pathToFile));
    }

    public long getCursor() {
        return filePointer;
    }

    public long write(Map<Integer, int[]> map) throws NullPointerException, IOException {
        long startIdx = getCursor();

        byte[] outBytes = mapToByteArray(map);
        postingsFile.write(outBytes);

        filePointer += outBytes.length;

        return startIdx;
    }


    public void flush() throws IOException {
        postingsFile.flush();
    }

    private byte[] mapToByteArray(Map<Integer, int[]> map){
        if(map == null || map.size() == 0) return new byte[]{};
        
        int numArrays = map.size();
        int numDataInts = 0;
        for (int[] i: map.values()
             ) {
            numDataInts += i.length;
        }
        int totalNumInts = 1 /*indicate number of arrays*/ + numArrays * 2 /*one "key" int and one "length" int per array*/ + numDataInts;

        byte[] data = new byte[totalNumInts * 4];
        int dataIndex = 0;

        //fill data array
        APostingOutputStream.intToByteArray(
                totalNumInts-1 /*indicates how many ints to read after the int indicating how many ints to read*/, data, dataIndex);
        dataIndex += 4;
        for (Map.Entry<Integer, int[]> entry : map.entrySet()
             ) {
            //insert key
            APostingOutputStream.intToByteArray(entry.getKey(), data, dataIndex);
            dataIndex += 4;
            //insert arr size
            int[] a_ints = entry.getValue();
            APostingOutputStream.intToByteArray(a_ints.length, data, dataIndex);
            dataIndex += 4;
            //insert arr
            for (int i : a_ints
                 ) {
                APostingOutputStream.intToByteArray(i, data, dataIndex);
                dataIndex += 4;
            }
        }
        return data;
    }

    public void close() throws IOException {
        postingsFile.close();
    }
}
