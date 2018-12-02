package Indexing.Index.IO;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IntToIntArrayMapStreamsTest {

    String path = "C:\\Users\\John\\Downloads\\infoRetrieval\\test results\\testCityIO.txt";
    IntToIntArrayMapOutputStream out = new IntToIntArrayMapOutputStream(path);
    IntToIntArrayMapInputStream in = new IntToIntArrayMapInputStream(path);

    IntToIntArrayMapStreamsTest() throws IOException {
    }

    @Test
    void smallTest() throws IOException {

        Map<Integer, int[]> original = new LinkedHashMap<>();

        original.put(653, new int[]{37, 239847, 50, 1});
        original.put(6, new int[]{3732, 243984, 530, 0, 23478 , 60, -3, 23897, 549, 5984, 90});
        original.put(98327, new int[]{0, -23487});

        int pointer = (int)out.write(original);

        Map<Integer, int[]> input = in.readIntegerArraysMap(pointer);
        assertArrayEquals(original.entrySet().toArray(), input.entrySet().toArray());
    }
}