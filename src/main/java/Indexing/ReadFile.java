package Indexing;

import Elements.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.exit;

/**
 * Runnable.
 * Reads all the files in #pathToDocumentsFolder (recursively) into the given buffer.
 * When finished, will insert a Document with all fields equalling null, to represent "end of file".
 */
public class ReadFile implements Runnable {
    private String pathToDocumentsFolder;
    static BlockingQueue<Document> documentBuffer;


    /**
     * @param pathToDocumentsFolder
     * @param documentBuffer        - a blocking queue where parsed documents will be outputted for further processing.
     */
    public ReadFile(String pathToDocumentsFolder, BlockingQueue<Document> documentBuffer) {
        this.pathToDocumentsFolder = pathToDocumentsFolder;
        this.documentBuffer = documentBuffer;
    }


    /**
     * Reads all the files in #pathToDocumentsFolder (recursively) into the given buffer.
     * When finished, will insert a Document with all fields equalling null, to represent "end of file".
     */

    private void read() {
        //TODO not implemented
        File f = new File(pathToDocumentsFolder);
        File[] allSubFiles = f.listFiles();
        for (File file : allSubFiles) {
            if (file.isDirectory()) {
                goDeeperInTree(file);
            }

        }

    }


    private void goDeeperInTree(File f) {
        File[] allSubFiles = f.listFiles();
        for (File file : allSubFiles) {
            if (file.isDirectory()) {

                System.out.println(file.getAbsolutePath() + " is directory");
                goDeeperInTree(file);
            } else {
                //System.out.println(file.getAbsolutePath()+" is file");
                try {

                    FileInputStream fileToRead = new FileInputStream(file);
                    String exampleString1 = "<ROOT>";
                    byte[] rootOpenTagAsByts = exampleString1.getBytes(StandardCharsets.UTF_8);
                    InputStream rootOpenTag = new ByteArrayInputStream(rootOpenTagAsByts);

                    String exampleString2 = "</ROOT>";
                    byte [] rootCloseTagAsByts = exampleString2.getBytes(StandardCharsets.UTF_8);
                    InputStream rootCloseTag = new ByteArrayInputStream(rootCloseTagAsByts);


                    SequenceInputStream sq = new SequenceInputStream(rootOpenTag , fileToRead);//adds to the beginning of the file the <ROOT> tag

                    SequenceInputStream finalDocToRead = new SequenceInputStream(sq,rootCloseTag); // adds to the end of the file </ROOT> tag

                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    UserHandler userhandler = new UserHandler();
                    saxParser.parse(finalDocToRead, userhandler);
                } catch (SAXParseException e) {
                    e.printStackTrace();

                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {

                    e.printStackTrace();
                }
            }


            }
        }



    public void run() {
        read();
    }


    //-------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------
    class UserHandler extends DefaultHandler {

        //boolean startDoc = false;
        boolean docId = false;
        boolean header = false;
        boolean text = false;
        Document doc = null;

        @Override
        public void startElement(
                String uri, String localName, String qName, Attributes attributes)
                throws SAXException {

            qName.replace("P=","P=\"");
            qName.replace("0","0\"");

            if (qName.equalsIgnoreCase("DOC")) {
                doc = new Document();
            } else if (qName.equalsIgnoreCase("DOCNO")) {
                docId = true;
            } else if (qName.equalsIgnoreCase("HEADER")) {
                header = true;
            } else if (qName.equalsIgnoreCase("TEXT")) {
                text = true;
            }
            else if (qName.equalsIgnoreCase("F")) {

                return;
            }


        }

        @Override
        public void endElement(String uri,
                               String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("DOC")) {
                ReadFile.documentBuffer.add(doc);
            }
        }


        @Override
        public void characters(char ch[], int start, int length) throws SAXException {

            if (docId) {
                doc.setDocId(new String(ch,start,length));
                docId = false;
            } else if (header) {
                doc.setHeader(new String(ch,start,length));
                header = false;
            } else if (text) {
                doc.setText(new String(ch,start,10));
                text = false;
            }
        }

    }
}
