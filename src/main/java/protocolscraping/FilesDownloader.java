package protocolscraping;

import database.DatabaseOperation;
import database.Operation;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is not used
 *
 *
 */
public class FilesDownloader {


    private static FilesDownloader instance;
    private static final String FILE_PATH = "./src/main/resources/blob/information.xml";
    private Operation operation = DatabaseOperation.build();
    final String url = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/866354-866354";

    /**
    * Restrict the creation of files downloader object direclty
     * @author Manuel Aha
    * */
    private FilesDownloader() {
    }

    /**
     * Identifiying the table part and extract the html element
     * @return
     * @throws IOException
     * @author Manuel Aha
     */
    private Elements getTable() throws IOException {
        final org.jsoup.nodes.Document document = (org.jsoup.nodes.Document) Jsoup.connect(url).get();

        return document.getElementsByClass("bt-link-dokument");
    }

    /**
    * Create instance of FilesDownloader and return
     * @author Manuel Aha
    * */
    public static FilesDownloader build() {
        if (instance == null) {
            instance = new FilesDownloader();
        }
        return instance;
    }

    /**
     * String manipulation not used anymore
     * @param str
     * @param index
     * @param replace
     * @return
     * @author Manuel Aha
     */
    public String replace(String str, int index, char replace){
        if(str==null){
            return str;
        }else if(index<0 || index>=str.length()){
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }

    /**
     *
     * @param doc
     * @return
     */
    private Document getBson(String doc) {
        JSONObject json = null;
        try {
            json = XML.toJSONObject(doc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert json != null;
        return org.bson.Document.parse(json.toString());
    }

    static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.US_ASCII);
    }

    //First version and attempts which might hold still important information
    public void downloadAndSaveTheFilesContentInDbInBson() throws IOException {
        for (Element link : getTable()) {
            try {
                String absoluteUrl = link.attr("abs:href");
                /*
                * Download file content and save in temp file
                * */
                URL website = new URL(absoluteUrl);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream(FILE_PATH);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();


                /*
                * Parse the downloaded xml file
                * */
                File input = new File(FILE_PATH);
                String fileContent = readFile(FILE_PATH);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                org.w3c.dom.Document d = db.parse(input.getAbsoluteFile());
                d.getDocumentElement().normalize();

                /*
                * Convert file content to bson and store it in database
                * */
                Document document = getBson(fileContent);
                if (document.containsKey(DatabaseOperation.PROTOKOL_KEY)) {
                    operation.insertOneDocument(DatabaseOperation.PROTOKOL_KEY, document);
                }

                NodeList nodeList = d.getElementsByTagName(DatabaseOperation.REDNER_KEY);
                // nodeList is not iterable, so we are using for loop
                for (int itr = 0; itr < nodeList.getLength(); itr++) {
                    Node node = nodeList.item(itr);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element eElement = (org.w3c.dom.Element) node;

                        try {
                            /*
                             * Creating custom document for avoid duplicates
                             * */
                            org.bson.Document doc = new org.bson.Document(DatabaseOperation.ID_COL_KEY, Integer.valueOf(eElement.getAttribute("id")));

                            doc.append(DatabaseOperation.VORNAME_COL_KEY, eElement.getElementsByTagName(DatabaseOperation.VORNAME_COL_KEY).item(0).getTextContent());
                            doc.append(DatabaseOperation.FRAKTION_COL_KEY, eElement.getElementsByTagName(DatabaseOperation.FRAKTION_COL_KEY).item(0).getTextContent());
                            doc.append(DatabaseOperation.SURNAME_COL_KEY, eElement.getElementsByTagName(DatabaseOperation.SURNAME_COL_KEY).item(0).getTextContent());

                            /*
                             * Insert document in database
                             * */
                            operation.insertOneDocument(DatabaseOperation.REDNER_KEY, doc);

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
