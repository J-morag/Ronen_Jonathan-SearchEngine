package Indexing;


import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import Elements.Document;

/**
 * Runnable.
 * Reads all the files in #pathToDocumentsFolder (recursively) into the given buffer.
 * When finished, will insert a Document with all fields equalling null, to represent "end of file".
 */
public class ReadFile implements Runnable {
    private String pathToDocumentsFolder;
    static BlockingQueue<Document> documentBuffer; //@TODO: need to see how to make the queue not static.


    /**
     * @param pathToDocumentsFolder
     * @param documentBuffer        - a blocking queue where parsed documents will be outputted for further processing.
     */
    public ReadFile(String pathToDocumentsFolder, BlockingQueue<Document> documentBuffer) {
        this.pathToDocumentsFolder = pathToDocumentsFolder;
        this.documentBuffer = documentBuffer;
    }


    /**
     * Reads all the files in #pathToDocumentsFolder (recursively) and separate each file to Documents
     * then generate each Document to a Documant object that goes into the Buffer
     *
     */

    private void read() {
        File f = new File(pathToDocumentsFolder);
        Elements docs;
        File[] allSubFiles = f.listFiles();
        for (File file : allSubFiles) {
            if (file.isDirectory()) {
                File [] documentsFiles = file.listFiles();
                for (File fileToGenerate: documentsFiles) {
                    docs = separateDocs(fileToGenerate);
                    if(docs != null){
                        generateDocs(docs);
                    }
                }

            }

        }

    }


    /**
     * separate documents from a file
     * @param fileToGenerate - a file that contains a bunch of Documents that needs to be separated
     * @return Elements object ( a list of Elements that each element is a document)
     */
    private Elements separateDocs(File fileToGenerate)
    {
        FileInputStream fi = null;
        Elements toReturn=null;

        try {
            fi = new FileInputStream(fileToGenerate);
            BufferedReader br =new BufferedReader(new InputStreamReader(fi));
            StringBuilder sb = new StringBuilder();
            String line = null;
            line = br.readLine();
            while (line!=null) {
                sb.append(line);
                line = br.readLine();
            }
            org.jsoup.nodes.Document doc = Jsoup.parse(sb.toString());
            toReturn = doc.select("DOC");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    /**
     * parses each element into a SaxParser that creates a Documents objects and insert them to the buffer.
     * @param elems - a list of separated documents that needs to be generated
     */
    private void generateDocs(Elements elems)
    {
        for (Element elm: elems)
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = null;
            try {

                saxParser = factory.newSAXParser();
                UserHandler userhandler = new UserHandler();

                String st =elm.toString();
                InputStream is =new ByteArrayInputStream(st.getBytes());
                saxParser.parse( is, userhandler);
            } catch (SAXException e) {
                e.printStackTrace();

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }




    public void run() {
        read();
    }


    //-------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------
    /**
     * handler class that determines what to do with each tag in the document
     */
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

            if (qName.equalsIgnoreCase("DOCNO")) {
                doc =new Document();
                docId = true;
            } else if (qName.equalsIgnoreCase("HEADER")) {
                header = true;
            } else if (qName.equalsIgnoreCase("TEXT")) {
                text = true;
            }
            else return;


        }

        @Override
        public void endElement(String uri,
                               String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("DOC")) {
                try {
                    ReadFile.documentBuffer.put(doc);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        public void characters(char ch[], int start, int length) throws SAXException {

            if (docId) {
                doc.setDocId(new String(ch, start, length));
                //System.out.println(doc.getDocId()+"\n------------------------");
                docId = false;
            } else if (header) {
                doc.setHeader(new String(ch, start, length));
                header = false;
            } else if (text) {
                System.out.println(new String(ch, start, length)); //@TODO fix it!! it is copying only until the first sub Tag
                doc.setText(new String(ch, start, length));
                text = false;
            }
        }

    }
}
