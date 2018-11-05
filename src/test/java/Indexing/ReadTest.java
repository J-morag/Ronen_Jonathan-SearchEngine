package Indexing;


import java.io.*;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;



public class ReadTest {

    private String path = "C:\\Users\\ronen\\Desktop\\FB396001";
    @Test
    public void readTest(){

    read();

    }


    private void read() {
        //TODO not implemented
        File f = new File(path);
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
                goDeeperInTree(file);
            } else {

                try {
                    FileInputStream fi = null;
                    try {
                        fi = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    BufferedReader br =new BufferedReader(new InputStreamReader(fi));
                    StringBuilder sb = new StringBuilder();

                        String line = br.readLine();
                        while (line!=null) {
                            sb.append(line);
                            line = br.readLine();
                        }

                    Document doc = Jsoup.parse(sb.toString());
                    Elements elm = doc.select("DOC");
                    for(Element el : elm){
                        System.out.println(el + "\n-----------------------------------");
                    }





                } catch (Exception e) {
                    continue;
                }
            }
        }
    }


}


