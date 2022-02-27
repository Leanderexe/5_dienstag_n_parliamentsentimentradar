package database;

import org.bson.Document;

import java.util.List;

/**
* Interface for Mongo database operations
 * I think the standard is to comment methods in interface instead of the actual class. This will be not the case
 * in this project
* @author Manuel Aha
*/
public interface Operation {

    Boolean exists(String collectionName);

    List<Document> findAll();

    void createNewCollection(String collectionName);

    List<Document> findAllDocument(String collectionName);

    void insertAll(String collectionName, List<Document> docList);

    void insertOneDocument(String collection, Document document);

    boolean documentExists(String collection, Document document);

    Document findDocument(String collection, String key, String value);

    Document findDocumentById(String collection, Integer id);

    void printAllCollections();

    void deleteCollection(String collection);

    void deleteDocument(String collection, String key, String value);
}
