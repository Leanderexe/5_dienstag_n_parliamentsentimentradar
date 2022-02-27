package database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;


/**
 *
 * Database actions are defined in this class and are determined in interface {@link Operation}
 * @author Manuel Aha and Leander Hermanns
 * @modified Leander Hermanns
 *
 */
public class DatabaseOperation implements Operation {
    private final MongoDBConnectionHandler mongoDBConnectionHandler;
    /*
    * database collection columns and keys
    * */
    public static final String REDNER_COLLECTION_NAME = "redner";
    public static final String SURNAME_COL_KEY = "nachname";
    public static final String FRAKTION_COL_KEY = "fraktion";
    public static final String VORNAME_COL_KEY = "vorname";
    public static final String TITLE_COL_KEY = "title";
    public static final String ID_COL_KEY = "_id";
    public static final String REDNER_IMAGE = "image";

    public static final String REDNER_KEY = "redner";
    public static final String PROTOKOL_KEY = "dbtplenarprotokoll";

    private static DatabaseOperation databaseOperation;


    /**
     * With creating the DatabaseOperation class we connect to the database {@link MongoDBConnectionHandler}
     * @author Manuel Aha
     */
    public DatabaseOperation() {
        mongoDBConnectionHandler = new MongoDBConnectionHandler();
    }

    /**
     * Creating instance
     * @author Manuel Aha
     * @return Generated DatabaseOperation class
     */
    public static DatabaseOperation build() {
        if (databaseOperation == null) {
            databaseOperation = new DatabaseOperation();
        }
        return databaseOperation;
    }

    /**
     * Checks if collection already exists in database
     * @param collectionName name of the collection to be checked
     * @return Result of the checking in Boolean
     * @author Manuel Aha
     */
    @Override
    public Boolean exists(String collectionName) {
        return mongoDBConnectionHandler.getDatabase().listCollectionNames().into(new ArrayList<String>()).contains(collectionName);
    }

    /**
     * Looking for all collections
     * @return List of collections
     * @author Manuel Aha
     */
    @Override
    public List<Document> findAll() {
        List<Document> collectionList = new ArrayList<>();
        ListCollectionsIterable<Document> findIterable = mongoDBConnectionHandler.getDatabase().listCollections();
        for (Document document : findIterable) {
            collectionList.add(document);
        }
        return collectionList;
    }

    /**
     * Creating a new collection
     * @param collectionName is the name of the new collection
     * @author Manuel Aha
     */
    @Override
    public void createNewCollection(String collectionName) {
        mongoDBConnectionHandler.getDatabase().createCollection(collectionName);
        System.out.println("Collection is created successfully");
    }

    /**
     * gets all documents of the collection
     * @param collectionName is the name of the target collection
     * @return collectionList
     * @author Manuel Aha
     */
    @Override
    public List<Document> findAllDocument(String collectionName) {
        List<Document> collectionList = new ArrayList<>();
        FindIterable<Document> findIterable = mongoDBConnectionHandler.getDatabase().getCollection(collectionName).find();
        for (Document document : findIterable) {
            collectionList.add(document);
        }
        return collectionList;
    }

    /**
     * Extracts documents explicitly for speeches collection
     * @return
     * @author Leander Hermanns
     * @modified Manuel Aha
     */
    public FindIterable<Document> getDocumentSpeech() {
        return mongoDBConnectionHandler.getDatabase().getCollection("speeches").find();
    }

    /**
     * Inserts a list of documents in a database collection
     * @param collectionName specifies which collection will be targeted
     * @param docList includes the docs
     * @author Manuel Aha
     */
    @Override
    public void insertAll(String collectionName, List<Document> docList) {
        mongoDBConnectionHandler.getDatabase().getCollection(collectionName).insertMany(docList);
        System.out.println("Documents inserted successfully in database");
    }

    /**
     * Inserts only a single document in database collection
     * @param collection specifies which collection will be targeted
     * @param document
     * @author Manuel Aha
     */
    @Override
    public void insertOneDocument(String collection, Document document) {
        mongoDBConnectionHandler.getDatabase().getCollection(collection).insertOne(document);
        System.out.println("Document inserted successfully in database");
    }

    /**
     * Checks if the document already exists in the collection
     * @param collection specifies which collection will be targeted
     * @param document is either duplicate or does not exist in database
     * @return true if document exists
     * @author Manuel Aha
     */
    @Override
    public boolean documentExists(String collection, Document document) {
        FindIterable<Document> iterable = mongoDBConnectionHandler.getDatabase().getCollection(collection).find();
        MongoCursor<Document> cursor = iterable.cursor();
        while (cursor.hasNext()) {
            Document document1 = cursor.next();

            try {
                if (!document.containsKey("_id")) {
                    document1.remove("_id");
                }
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }

            if (document.equals(document1)) {
                System.out.println("exists");
                return true;
            }
        }

        return false;
    }

    /**
     * Find a document by key  from a collection
     * @param collection
     * @param key
     * @param value
     * @return the desired document
     * @author Manuel Aha
     */
    @Override
    public Document findDocument(String collection, String key, String value) {
        return mongoDBConnectionHandler.getDatabase().getCollection(collection).find(eq(key, value)).first();
    }

    /**
     * Find a document by id from a collection
     * @param collection
     * @param id
     * @return the desired document
     * @author Manuel Aha
     */
    @Override
    public Document findDocumentById(String collection, Integer id) {
        return mongoDBConnectionHandler.getDatabase().getCollection(collection).find(eq(ID_COL_KEY, id)).first();
    }

    /**
     * Is showing all existing collections
     * @author Manuel Aha
     */
    @Override
    public void printAllCollections() {

        MongoIterable<String> list = mongoDBConnectionHandler.getDatabase().listCollectionNames();

        System.out.println("\n=============List of collections===============\n");

        for (String name : list) {
            System.out.println(name);
        }
    }

    /**
     * Deletes an entire collection
     * @param collection will be gone
     * @author Manuel Aha
     */
    @Override
    public void deleteCollection(String collection) {
        mongoDBConnectionHandler.getDatabase().getCollection(collection).drop();
        System.out.println(collection + " deleted successfully!");
    }

    /**
     * Deletes a single document from given collection
     * @param collection target collection
     * @param key to identfy the document to be deleted
     * @param value
     * @author Manuel Aha
     */
    @Override
    public void deleteDocument(String collection, String key, String value) {
        mongoDBConnectionHandler.getDatabase().getCollection(collection).deleteOne(Filters.eq(key, value));
        System.out.println(key + " is deleted successfully!");
    }


}
