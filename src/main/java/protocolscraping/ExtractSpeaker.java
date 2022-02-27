package protocolscraping;

import database.DatabaseOperation;
import org.bson.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static database.DatabaseOperation.REDNER_KEY;


/**
 * This class is a duplicate @link{XmlConversion.createcollectionByDoc()}
 * This snippet summons problems with the rest of the XMLConversion class which is the reason it is commented out
 * You remove the comments in XMLconversion and comment out the process of checking if the doc already exists in database @link{XMLConversion.createCollectionByDoc() (line 338,339)}
 * Because it starts a not so simple process to extract the speakers, downloading the speaker image and storing everything the the database I want to make sure this is not getting overlooked.
 * As a team we were not able to include this code properly in a nice user usage case
 *
 * @author Manuel Aha
 */
public class ExtractSpeaker {

    DatabaseOperation databaseOperation;
    org.bson.Document document;
    private final String REDNER_LIST_KEY = "rednerliste";

    public ExtractSpeaker(org.bson.Document document, DatabaseOperation databaseOperation) {
        this.document = document;
        //Creating a new instance might be a bad idea here
        this.databaseOperation = databaseOperation;
        init();
    }


    public void init() {

        for (Map.Entry<String, Object> e : document.entrySet()) {
            org.bson.Document document1 = (org.bson.Document) e.getValue();
            /*
             * Only insert in db if its protocol
             * */
            if (!databaseOperation.documentExists(DatabaseOperation.PROTOKOL_KEY, document1)) {
                databaseOperation.insertOneDocument(DatabaseOperation.PROTOKOL_KEY, document1);
            }
        }

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

                List<Document> data = (ArrayList<Document>)
                        (((org.bson.Document) ((org.bson.Document)
                                document1.get(REDNER_LIST_KEY))).get(REDNER_KEY));


                data.forEach(d -> {
                    try{
                        //to avoid requesting blocks
                        Thread.sleep(1200);
                        org.bson.Document speakerdoc = (org.bson.Document) d.get("name");
                        Integer id = (Integer) d.get("id");


                        //Extracting name and party of speaker
                        String vorname = speakerdoc.get(DatabaseOperation.VORNAME_COL_KEY).toString();
                        String fraktion = speakerdoc.get(DatabaseOperation.FRAKTION_COL_KEY).toString();
                        String nachname = speakerdoc.get(DatabaseOperation.SURNAME_COL_KEY).toString();


                        //Starting to get the image fetch process
                        PictureScrap picsy = new PictureScrap();
                        String name = vorname + " " + nachname;
                        URL speakerImg = picsy.run(name);


                        String strImg = speakerImg.toString();


                        /*
                         * Creating custom document for avoid duplicates
                         * */
                        org.bson.Document doc = new org.bson.Document(DatabaseOperation.ID_COL_KEY, id);
                        doc.append(DatabaseOperation.VORNAME_COL_KEY, vorname );
                        doc.append(DatabaseOperation.FRAKTION_COL_KEY, fraktion);
                        doc.append(DatabaseOperation.SURNAME_COL_KEY, nachname);
                        doc.append(DatabaseOperation.REDNER_IMAGE, strImg);

                        int counter = 0;
                        for(org.bson.Document docrede: databaseOperation.findAllDocument("speeches")) {
                            System.out.println("hello2");
                            String rednerid = (String) docrede.get("rednerID");
                            if (id.equals(rednerid)){
                                counter += 1;
                            }
                        }
                        doc.append("AnzahlanReden", counter);




                        /*
                         * Insert document in database
                         * */
                        databaseOperation.insertOneDocument(REDNER_KEY, doc);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                });


            });
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

}
