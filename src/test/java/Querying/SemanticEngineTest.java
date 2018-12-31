package Querying;

import Querying.Semantics.SemanticEngine;
import de.jungblut.glove.impl.GloveBinaryWriter;
import de.jungblut.glove.impl.GloveTextReader;
import de.jungblut.glove.util.StringVectorPair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

class SemanticEngineTest {

    private static final String pathToGloveFilesFolder = "C:\\Users\\John\\Downloads\\infoRetrieval\\GloVe\\customVectors";


    @Test
    void showSynonyms() throws IOException {
//        SemanticEngine se = new SemanticEngine(pathToGloveFilesFolder, 10);
//        List<String> neighbors;
//        System.out.println("---- dog ----");
//        neighbors = se.getNearestNeighbors("dog");
//        for (String neighbor: neighbors
//             ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- puppy ----");
//        neighbors = se.getNearestNeighbors("puppy");
//        for (String neighbor: neighbors
//             ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- poppy ----");
//        neighbors = se.getNearestNeighbors("poppy");
//        for (String neighbor: neighbors
//             ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- day ----");
//        neighbors = se.getNearestNeighbors("day");
//        for (String neighbor: neighbors
//                ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- DAY ----");
//        neighbors = se.getNearestNeighbors("DAY");
//        for (String neighbor: neighbors
//                ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- england ----");
//        neighbors = se.getNearestNeighbors("england");
//        for (String neighbor: neighbors
//                ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- England ----");
//        neighbors = se.getNearestNeighbors("England");
//        for (String neighbor: neighbors
//                ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- Britain ----");
//        neighbors = se.getNearestNeighbors("Britain");
//        for (String neighbor: neighbors
//                ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- israel ----");
//        neighbors = se.getNearestNeighbors("israel");
//        for (String neighbor: neighbors
//                ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- russia ----");
//        neighbors = se.getNearestNeighbors("russia");
//        for (String neighbor: neighbors
//                ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- china ----");
//        neighbors = se.getNearestNeighbors("china");
//        for (String neighbor: neighbors
//                ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- petroleum ----");
//        neighbors = se.getNearestNeighbors("petroleum");
//        for (String neighbor: neighbors
//                ) {
//            System.out.println(neighbor);
//        }
//        System.out.println("---- winners ----");
//        neighbors = se.getNearestNeighbors("winners");
//        for (String neighbor: neighbors
//                ) {
//            System.out.println(neighbor);
//        }

    }

}