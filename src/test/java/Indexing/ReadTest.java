package Indexing;


import java.io.*;
import java.util.HashMap;
import java.util.concurrent.*;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import Elements.Document;


public class ReadTest {


    private static final int documentBufferSize = 10;
    private static final int termBufferSize = 10;
    private static final int stemmedTermBufferSize = 10;


    private static final String pathToDocumentsFolder = "C:\\Users\\ronen\\Documents\\לימודים\\שנה ג\\איחזור מידע\\עבודות\\מסמכים מנוע חיפוש\\corpus"; //TODO temporary! should come from UI
    private static final String pathToStopwordsFile = "/stopwords"; //TODO temporary! should come from UI
    private static final String pathToOutputFolder = "/output"; //TODO temporary! should come from UI
    public static BlockingQueue<Document> documentBuffer; //@TODO: need to see how to make the queue not static.

    @Test
    void createIndex() throws InterruptedException {

        /*  Concurrent buffers:
        Thread safe. blocks if empty or full.
        Remember it is imperative that the user manually synchronize on the returned list when iterating over it */
        documentBuffer = new ArrayBlockingQueue<Document>(documentBufferSize);
        //BlockingQueue<TermDocument> termDocumentsBuffer = new ArrayBlockingQueue<>(termBufferSize);
        //BlockingQueue<TermDocument> stemmedTermDocumentsBuffer = new ArrayBlockingQueue<>(stemmedTermBufferSize);


        //  Worker Threads:
        long start = System.currentTimeMillis();
        Thread tReader = new Thread(new ReadFileTest(pathToDocumentsFolder, documentBuffer));
        tReader.start();
        tReader.join();
        System.out.println((System.currentTimeMillis()-start)/1000);

    }


    /**
     * Runnable.
     * Reads all the files in #pathToDocumentsFolder (recursively) into the given buffer.
     * When finished, will insert a Document with all fields equalling null, to represent "end of file".
     */
    public class ReadFileTest implements Runnable {
        private String pathToDocumentsFolder;


        /**
         * @param pathToDocumentsFolder
         * @param documentBuffer        - a blocking queue where parsed documents will be outputted for further processing.
         */
        public ReadFileTest(String pathToDocumentsFolder, BlockingQueue documentBuffer) {
            this.pathToDocumentsFolder = pathToDocumentsFolder;
            documentBuffer = documentBuffer;
        }


        /**
         * Reads all the files in #pathToDocumentsFolder (recursively) and separate each file to Documents
         * then generate each Document to a Documant object that goes into the Buffer
         */

        private void read() {
            File f = new File(pathToDocumentsFolder);
            ExecutorService threadPool = Executors.newCachedThreadPool();
            //Elements docs;
            File[] allSubFiles = f.listFiles();
            for (File file : allSubFiles) {
                if (file.isDirectory()) {
                    File[] documentsFiles = file.listFiles();
                    for (File fileToGenerate : documentsFiles) {
                        Elements docs = separateDocs(fileToGenerate);
                        if (docs != null) {
                           threadPool.execute(()-> generateDocs(docs));

                        }
                    }
                    //System.out.println(Thread.activeCount());

                }

            }
            threadPool.shutdown();
            //documentBuffer.put(new Document(-1,null,null,null));

        }


        /**
         * separate documents from a file
         *
         * @param fileToGenerate - a file that contains a bunch of Documents that needs to be separated
         * @return Elements object ( a list of Elements that each element is a document)
         */
        private Elements separateDocs(File fileToGenerate) {
            FileInputStream fi = null;
            Elements toReturn = null;

            try {
                fi = new FileInputStream(fileToGenerate);
                BufferedReader br = new BufferedReader(new InputStreamReader(fi));
                StringBuilder sb = new StringBuilder();
                String line = null;
                line = br.readLine();
                while (line != null) {
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
         *
         * @param elems - a list of separated documents that needs to be generated
         */
        private void generateDocs(Elements elems) {
            for (Element elm : elems) {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = null;
                try {
                    saxParser = factory.newSAXParser();
                    UserHandler1 userhandler = new UserHandler1();
                    String st = elm.toString();
                    InputStream is = new ByteArrayInputStream(st.getBytes());
                    saxParser.parse(is,userhandler);
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
        class UserHandler1 extends DefaultHandler {
            //boolean startDoc = false;
            boolean docId = false;
            boolean title = false;
            boolean text = false;
            boolean others =false;
            boolean date = false;
            Document doc = null;
            StringBuilder textString;


            @Override
            public void startElement(
                    String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {

                if (qName.equalsIgnoreCase("DOCNO")) {
                    doc = new Document();
                    docId = true;
                }
                else if (qName.equalsIgnoreCase("TI")){
                    title=true;
                }
                else if (qName.equalsIgnoreCase("DATE") || qName.equalsIgnoreCase("DATE1")) {
                    date = true;
                } else if (qName.equalsIgnoreCase("TEXT")) {
                    text = true;
                    textString = new StringBuilder();
                } else if( text) {

                    others =true;
                }


            }

            @Override
            public void endElement(String uri,
                                   String localName, String qName) throws SAXException {
                if (qName.equalsIgnoreCase("DOC")) {
                    try {
                        documentBuffer.put(doc);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (qName.equalsIgnoreCase("TEXT")){
                        text = false;
                        doc.setText(textString.toString());
                        textString=null;
                }
            }


            @Override
            public void characters(char ch[], int start, int length) throws SAXException {

                if (docId) {
                    doc.setDocId(new String(ch, start, length));
                    docId = false;
                } else if (date) {
                    doc.setDate(new String(ch, start, length));
                    date = false;

                }
                else if(title){
                    doc.setTitle(new String(ch, start, length));
                    title=false;
                }
                else if(text && others){
                    textString.append( new String(ch, start, length) );
                    others=false;

                } else if (text) {
                    //  System.out.println(new String(ch, start, length)); //@TODO fix it!! it is copying only until the first sub Tag
                    textString.append(new String(ch, start, length));
                }

            }

        }
    }
//--------------------------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------------------------------

    public class ReadFileTest_2 implements Runnable {
        private String pathToDocumentsFolder;


        public ReadFileTest_2(String pathToDocumentsFolder, HashMap documentBuffer) {
            this.pathToDocumentsFolder = pathToDocumentsFolder;
            documentBuffer = documentBuffer;
        }

        public void run() {
            read();
        }

        private void read() {
            File f = new File(pathToDocumentsFolder);
            Elements docs;
            File[] allSubFiles = f.listFiles();
            for (File file : allSubFiles) {
                if (file.isDirectory()) {
                    File[] documentsFiles = file.listFiles();
                    for (File fileToGenerate : documentsFiles) {
                        docs = separateDocs(fileToGenerate);
                        if (docs != null) {
                            generateDocs(docs);
                        }
                    }

                }

            }

        }


        private Elements separateDocs(File fileToGenerate) {
            FileInputStream fi = null;
            Elements toReturn = null;

            try {
                fi = new FileInputStream(fileToGenerate);
                BufferedReader br = new BufferedReader(new InputStreamReader(fi));
                StringBuilder sb = new StringBuilder();
                String line = null;
                line = br.readLine();
                while (line != null) {
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


        private void generateDocs(Elements elems) {
            Document doc = new Document();
            for (Element elm : elems) {
                XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
                try {
                    String st = elems.toString();
                    XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(st.getBytes()));
                    while (xmlEventReader.hasNext()) {
                        XMLEvent xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent.isStartElement()) {
                            StartElement startElement = xmlEvent.asStartElement();
                            if (startElement.getName().toString().equalsIgnoreCase("DOCNO")) {
                                doc.setDocId(xmlEvent.toString());
                            }
                            //set the other varibles from xml elements
                            else if (startElement.getName().toString().equalsIgnoreCase("TI")) {
                                xmlEvent = xmlEventReader.nextEvent();
                                doc.setTitle(xmlEvent.toString());
                            } else if (startElement.getName().toString().equalsIgnoreCase("DATE") || startElement.getName().getLocalPart().equals("DATE1")) {
                                xmlEvent = xmlEventReader.nextEvent();
                                doc.setDate(xmlEvent.toString());
                            } else if (startElement.getName().toString().equalsIgnoreCase("TEXT")) {
                                xmlEvent = xmlEventReader.nextEvent();
                                doc.setText(xmlEvent.toString());
                            }
                        }
                        //if Employee end element is reached, add employee object to list
                        if (xmlEvent.isEndElement()) {
                            EndElement endElement = xmlEvent.asEndElement();
                            if (endElement.getName().getLocalPart().equalsIgnoreCase("DOC")) {
                                //documentBuffer.put(doc , doc.getDocId());
                            }
                        }
                    }

                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
            }



    }




}



