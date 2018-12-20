package Querying;

import de.jungblut.glove.impl.GloveBinaryWriter;
import de.jungblut.glove.impl.GloveTextReader;
import de.jungblut.glove.util.StringVectorPair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SemanticEngineTest {

    private static final String textFilePath = "C:\\Users\\John\\Downloads\\infoRetrieval\\GloVe\\glove.6B.50d.txt";
    private static final String outputFolderPath = "C:\\Users\\John\\Downloads\\infoRetrieval\\GloVe";

    @Test
    void textToBinary() throws IOException {

//        if (args.length != 2) {
//            System.err
//                    .println("first argument needs to be the glove text file, the second needs to be the output folder of the binary files.");
//            System.exit(1);
//        }

        GloveTextReader reader = new GloveTextReader();
        Stream<StringVectorPair> stream = reader.stream(Paths.get(textFilePath));
        GloveBinaryWriter writer = new GloveBinaryWriter();
        writer.writeStream(stream, Paths.get(outputFolderPath));

    }
}