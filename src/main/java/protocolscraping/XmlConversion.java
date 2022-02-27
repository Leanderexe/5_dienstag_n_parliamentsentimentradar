package protocolscraping;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import NLP.Pipeline;
import NLP.redeMongoDB;
import database.DatabaseOperation;
import entity.Speech;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import scala.Int;

/**
 * This class fetches the the protocols, converts them to bson.documents, saves those docuents in the database
 * It also gets the speeches and speakers out of the xml and us initiating the process of getting the image of the speaker
 * @author Manuel Aha
 * @modiefied Leander Hermanns
 */
public class XmlConversion {

    private String url;
    private List<String> searchValue;
    private List Speaker_id;
    private String parentURL = "https://www.bundestag.de";
    public BufferedImage processedImage;
    private final String REDNER_LIST_KEY = "rednerliste";
    private final String REDNER_KEY = "redner";


    //Collection Keys
    private final String REDE_COLL_KEY = "speeches";
    private final String REDE_ID_KEY = "redeID";
    private final String REDE_SPEAKER_KEY = "rednerID";
    private final String REDE_COMMENTS_KEY = "comments";
    private final String REDE_DATE_KEY = "date";
    private final String REDE_CONTENT_KEY = "content";


    /**
     * Identifier for database operation
     * @author Manuel Aha
     */
    private DatabaseOperation databaseOperation;

    /**
     * identifies the location of the xmls based on the Legislaturperiode
     * @param url
     * @param searchValue
     * @author Manuel Aha
     */
    public XmlConversion(String url, List<String> searchValue) {
        this.url = url;
        this.searchValue = searchValue;
    }

    /**
     * initiating the entire fetching xml process with all additional features
     * @author Manuel Aha
     */
    public void init() {

        databaseOperation = DatabaseOperation.build();

        //connecting to the website and converting it to html source
        String pageSource = getPageSource(url);
        //Initialize empty list xml-IDs
        List<String> endPointIds = new ArrayList<String>();

        for (String string : searchValue) {

            //Here we get the xml-ID
            String id = getIdByXpath(string, pageSource);
            endPointIds.add(id);
        }

        //Here we are getting the xml-URLs by xml-ID
        Map<String, Map<String,String>> datas = parseXmlUrl(endPointIds);

        //converting the xml to a document for the database
        xmlToBsonDocument(datas);

        /*
        try {
            //extract the sppeches out of the protocols
            extractSpeech(datas);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        }

         */

    }


    /**
     * We extracting the xmlID
     * @param searchValue
     * @param pageSource
     * @return the id of the xml
     * @author Manuel Aha
     */
    private String getIdByXpath(String searchValue, String pageSource) {
        String id = "";
        XPath xPath = null;

        //path of the xml file we want
        String xpath = "//*[text()='" + searchValue + "']/following::div[contains(@id,\"bt-collapse\")][1]";

        xPath = XPathFactory.newInstance().newXPath();
        org.w3c.dom.Document doc = null;
        try {
            //Is cleaning html for a better parsing process
            TagNode tagNode = new HtmlCleaner().clean(pageSource);
            try {
                doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            //to access data from xml
            NodeList nodeList = (NodeList) xPath.evaluate(xpath, doc, XPathConstants.NODESET);

            //if it exists
            if (nodeList.getLength() > 0) {

                Node node = nodeList.item(0);
                String idValue = node.getAttributes().getNamedItem("id").getNodeValue();

                //String manipulation to receive desired format
                String[] arr = idValue.split("-");

                id = arr[arr.length - 1];
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return id;
    }

    /**
     * Creating the connecting reference to desired website
     * @param url is given
     * @return the page source of html
     * @author Manuel Aha
     */
    private String getPageSource(String url) {
        String pageSource = "";

        try {
            //Jsoup connection
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0").get();

            return doc.html();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return pageSource;
    }

    /**
     * Here we get the desired document based on the given xml
     * @param url is extracted @link{getIdbyXpath}
     * @return the plenar protocol
     * @Author MAnuel Aha
     */
    private Document getDocument(String url) {
        try {
            //Creating Jsoup connection
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0").get();

            return doc;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Map<String, Map<String,String>> parseXmlUrl(List<String> key) {
        int limit = 10;
        int offset = 0;

        Map<String,String> xmlURLs = new LinkedHashMap<>();

        //String->xml-ID, List-> xml-URL
        //Map<String, List<String>> data = new LinkedHashMap<>();
        Map<String, Map<String,String>> data = new LinkedHashMap<>();

        //going through all the ID's
        for (String string : key) {

            //We are getting all the URL's
            recursiveMethodToGetAllXmlURl(xmlURLs, limit, offset, string);
            data.put(string, xmlURLs);

            xmlURLs = new LinkedHashMap<>();
            offset = 0;
        }

        return data;
    }

    /**
     * A recusrive iteration of extracting the desired xmls out of the map structure
     * @param xmlURLs includes the desired xmls
     * @param limit set to 10 to avoid overwhelming data amounts
     * @param offset is 0 because we want all xmls
     * @param key are the endpoints we have identified
     * @author MAnuel Aha and Leander Hermanns
     */
    private void recursiveMethodToGetAllXmlURl(Map<String,String> xmlURLs, int limit, int offset, String key) {

        //scraping through the table
        String url = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/" + key + "-" + key + "?limit="
                + limit + "&noFilterSet=false&offset=" + offset;

        Document pageSource = getDocument(url);

        //identified refernece points in the html
        Elements elements = pageSource.getElementsByClass("bt-link-dokument");
        Elements plenarDes = pageSource.getElementsByClass("bt-documents-description");

        Integer counter = 0;


        //Creating and filling a list of the xmlUrls to extract alter
        for (Element element : elements) {
            String xmlUrl = element.attr("href");
            String plenarName = plenarDes.get(counter).getElementsByTag("strong").text();

            System.out.println(plenarName);

            xmlURLs.put(xmlUrl, plenarName);
            counter++;
        }
        offset += limit;

        //if list is full of data we can call this method again assuming there is more
        if (elements.size() == limit) {
            recursiveMethodToGetAllXmlURl(xmlURLs, limit, offset, key);
        }

    }

    /**
     * converts the xml to the bson.document through json transisiton for the database
     * @param datas
     * @author Manuel Aha
     */
    private void xmlToBsonDocument(Map<String, Map<String,String>> datas) {

        for (Map.Entry<String, Map<String,String>> data : datas.entrySet()) {
            Map<String,String> xmlURL = data.getValue();


            for (Entry<String, String> string : xmlURL.entrySet()) {

                //Creating xml
                String xml = getPageSource(parentURL+string.getKey());
                String name = string.getValue();

                JSONObject json = null;
                try {
                    //Creating Json out of the xml
                    json = XML.toJSONObject(xml);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //and form Json to Bson
                org.bson.Document doc = org.bson.Document.parse(json.toString());
                //You can work with this list of docs now
                createCollectionByDoc(doc);

            }
        }
    }

    /**
     * Process of inserting the document into the collection and extracting/uploading the speaker
     * @param document
     * @Author Manuel Aha
     * @modifiedby Leander Hermanns
     */
    private void createCollectionByDoc(org.bson.Document document) {
        if (!databaseOperation.exists(DatabaseOperation.PROTOKOL_KEY)) {
            databaseOperation.createNewCollection(DatabaseOperation.PROTOKOL_KEY);
        }

        // Insert or update protocol in database
        for (Map.Entry<String, Object> e : document.entrySet()) {
                org.bson.Document document1 = (org.bson.Document) e.getValue();
                /*
                * Only insert in db if its protocol
                * */
                if (!databaseOperation.documentExists(DatabaseOperation.PROTOKOL_KEY, document1)) {
                    databaseOperation.insertOneDocument(DatabaseOperation.PROTOKOL_KEY, document1);
                }
        }

        /*
        * Fetch all the speaker from collection and save them in separate collection
        *
        * Check if collection already exists or not
        * */
        if (!databaseOperation.exists(REDNER_KEY)) {
            databaseOperation.createNewCollection(REDNER_KEY);
        }
        try {

            /*
             * Insert speakers in separate collection
             * */
            Collection<Object> values = document.values();
            values.forEach(o -> {
                org.bson.Document document1 = (org.bson.Document) o;

                List<org.bson.Document> data = (ArrayList<org.bson.Document>)
                        (((org.bson.Document) ((org.bson.Document)
                                document1.get(REDNER_LIST_KEY))).get(REDNER_KEY));


                data.forEach(d -> {
                    try {
//
//                      Alles hier in der try clause kümmert sich um die Sprecher des Protokolls
//
//                      Es extrahiert die Sprecher, deren Information und startet das Bild (in diesem Fall die URL, aber andere Formen wären hier auch leicht wählbar) aud dem Internet basierent auf den Namen herunterzuladen
//
//                      In dieser Struktur eckt dies mit dem übrigen Projekt an (Nach Duplikaten checken)
//                      Jedoch funktioniert dieser Code alleine einwandfrei.
//

                        /*
                        //To avoid to be banned from requesting
                        Thread.sleep(1200);
                        org.bson.Document speakerdoc = (org.bson.Document) d.get("name");
                        Integer id = (Integer) d.get("id");


                        String vorname = speakerdoc.get(DatabaseOperation.VORNAME_COL_KEY).toString();
                        String fraktion = speakerdoc.get(DatabaseOperation.FRAKTION_COL_KEY).toString();
                        String nachname = speakerdoc.get(DatabaseOperation.SURNAME_COL_KEY).toString();


                        PictureScrap picsy = new PictureScrap();
                        String name = vorname + " " + nachname;

                        URL speakerImg = picsy.run(name);


                        String strImg = speakerImg.toString();


                        /*
                        * Creating custom document for avoid duplicates
                        * *//*
                        org.bson.Document doc = new org.bson.Document(DatabaseOperation.ID_COL_KEY, id);
                        doc.append(DatabaseOperation.VORNAME_COL_KEY, vorname );
                        doc.append(DatabaseOperation.FRAKTION_COL_KEY, fraktion);
                        doc.append(DatabaseOperation.SURNAME_COL_KEY, nachname);
                        doc.append(DatabaseOperation.REDNER_IMAGE, strImg);
                        /*
                        int counter = 0;
                        for(org.bson.Document docrede: databaseOperation.findAllDocument("speeches")) {
                            System.out.println("hello2");
                            String rednerid = (String) docrede.get("rednerID");
                            if (id.equals(rednerid)){
                                counter += 1;
                            }
                        }
                        doc.append("AnzahlanReden", counter);

                         */


                        /*
                        * Insert document in database
                        * *//*
                        databaseOperation.insertOneDocument(REDNER_KEY, doc);*/
                    }catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                });


            });
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * extracting the speeches and its additional information from protocol
     * @param datas
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws ResourceInitializationException
     * @author Leander Hermanns
     * @modified Manuel Aha
     */
    private void extractSpeech(Map<String, Map<String,String>> datas) throws ParserConfigurationException, IOException, SAXException, ResourceInitializationException {
        //for loop for all the xml files we fetched
        for (Map.Entry<String, Map<String,String>> data : datas.entrySet()) {
            Map<String, String> xmlURL = data.getValue();


            for (Entry<String, String> string : xmlURL.entrySet()) {

                String xml = getPageSource(parentURL + string.getKey());


                DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
                fac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                DocumentBuilder db = fac.newDocumentBuilder();

                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(xml)) );


                NodeList tagesOP = doc.getElementsByTagName("tagesordnungspunkt");
                NodeList date = doc.getElementsByTagName("datum");
                String Datum = date.item(0).getTextContent();

                //Starting process of extracting the speeches with its information
                for (int j = 0;j < tagesOP.getLength(); j++) {
                    List Kommentare_Liste = new ArrayList(); // Holds every comment made.
                    List Inhalt_Liste = new ArrayList();  // Holds every comment + every speech.
                    StringBuilder Titel = new StringBuilder();
                    List redner_id_list = new ArrayList();
                    List Speaker_list = new ArrayList();


                        Node Node_OP = (tagesOP.item(j));
                        if (Node_OP.getNodeType() == Node.ELEMENT_NODE) {
                            org.w3c.dom.Element top = (org.w3c.dom.Element) Node_OP;
                            String top_id = top.getAttribute("top-id");  // Gibt Tagesordnungspunkt aus.
                            NodeList child_list = top.getChildNodes();
                            for (int t = 0; t < child_list.getLength(); t++) {
                                Node child = child_list.item(t);
                                if (child.getNodeType() == Node.ELEMENT_NODE) {
                                    org.w3c.dom.Element Rede = (org.w3c.dom.Element) child;
                                    if (Rede.getTagName() == "p") {
                                        Inhalt_Liste.add(Rede.getTextContent());

                                        if (Rede.getAttribute("klasse").equals("T_NaS")) {
                                            Titel.append(" " + Rede.getTextContent());
                                        }
                                        if (Rede.getAttribute("klasse").equals("T_fett")) {
                                            Titel.append(" " + Rede.getTextContent());
                                        }
                                    } else if (Rede.getTagName() == "kommentar") {
                                        Inhalt_Liste.add(Rede.getTextContent());
                                    }

                                    if (Rede.getTagName() == "rede") {
                                        String redner_id = "0";
                                        String Vorname = null;
                                        List Kommentare_pro_rede = new ArrayList();
                                        List Speech_Liste = new ArrayList();  // Holds every speech.
                                        String rede_id = Rede.getAttribute("id");
                                        NodeList rede_child_list = Rede.getChildNodes();

                                        for (int z = 0; z < rede_child_list.getLength(); z++) {
                                            Node text_node = rede_child_list.item(z);


                                            if (text_node.getNodeType() == Node.ELEMENT_NODE) {
                                                org.w3c.dom.Element text = (org.w3c.dom.Element) text_node;
                                                if (text.getTagName() == "p") {
                                                    if (text.getAttribute("klasse").equals("redner")) {
                                                        NodeList redner_node = text.getChildNodes();
                                                        for (int k = 0; k < redner_node.getLength(); k++) {
                                                            Node r_node = redner_node.item(k);
                                                            if (r_node.getNodeType() == Node.ELEMENT_NODE) {
                                                                org.w3c.dom.Element redner = (org.w3c.dom.Element) r_node;
                                                                if (redner.getTagName() == "redner") {
                                                                    redner_id = redner.getAttribute("id"); // Redner_id
                                                                    redner_id_list.add(redner_id);
                                                                    Speaker_id.add(redner_id);

                                                                    NodeList child_node = redner.getChildNodes();
                                                                    for (int n = 0; n < child_node.getLength(); n++) {
                                                                        Node ch = child_node.item(n);
                                                                        if (ch.getNodeType() == Node.ELEMENT_NODE) {
                                                                            org.w3c.dom.Element ch_element = (org.w3c.dom.Element) ch;

                                                                            NodeList ch_node = ch_element.getChildNodes();
                                                                            for (int u = 0; u < ch_node.getLength(); u++) {
                                                                                Node redner_prop = ch_node.item(u);
                                                                                if (redner_prop.getNodeType() == Node.ELEMENT_NODE) {
                                                                                    org.w3c.dom.Element redner_prop_elem = (org.w3c.dom.Element) redner_prop;

                                                                                    if (redner_prop_elem.getTagName() == "vorname") {  // get content from
                                                                                        Vorname = redner_prop_elem.getTextContent();
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        Inhalt_Liste.add(text.getTextContent());
                                                        Speech_Liste.add(text.getTextContent());
                                                    }
                                                } else if (text.getTagName() == "kommentar") {
                                                    Kommentare_pro_rede.add(text.getTextContent());
                                                    Kommentare_Liste.add(text.getTextContent());
                                                    Inhalt_Liste.add(text.getTextContent());
                                                }
                                            }
                                        }
                                        //top_id für Tagesordnungspunkt
                                        Speech speech = new Speech(Datum, redner_id, rede_id, Speech_Liste, Kommentare_pro_rede);
                                        speech.printSpeech();
                                        //Speaker_list.add(speech);

                                        //Creating the bson.cdocument from the infromation extracted
                                        org.bson.Document bsonSpeech = new org.bson.Document(REDE_DATE_KEY, Datum);
                                        bsonSpeech.append(REDE_SPEAKER_KEY,redner_id);
                                        bsonSpeech.append(REDE_ID_KEY, rede_id);
                                        bsonSpeech.append(REDE_CONTENT_KEY, Speech_Liste);
                                        bsonSpeech.append(REDE_COMMENTS_KEY, Kommentare_pro_rede);
                                        /*
                                        Rede_MongoDB redenlp = new Rede_MongoDB(bsonSpeech);
                                        Pipeline pip = new Pipeline();
                                        JCas jcas = redenlp.toCAS();
                                        List<JCas> jCasrede = new ArrayList<>();
                                        jCasrede.add(jcas);
                                        //System.out.println("hier bin ich" + jCasrede);
                                        AnalysisEngine pipeline = pip.buildpipeline();
                                        redenlp.build_pipeline(jcas,pipeline);
                                        List mapnelist = redenlp.get_named_entities(jCasrede);
                                        List mapneolist = redenlp.get_named_entities_objects(jCasrede);


                                        bsonSpeech.append("named entities objects", mapneolist); // Added all named entities objects in the order they appear.
                                        bsonSpeech.append("named entities", mapnelist);  // Added all named entities in the order they appear.

                                         */
                                        databaseOperation.insertOneDocument(REDE_COLL_KEY, bsonSpeech);
                                    }

                                }
                            }
                        }

                }


            }


        }


    }


}
