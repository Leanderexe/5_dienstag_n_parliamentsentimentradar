package NLP;

import com.mongodb.client.MongoCursor;
import database.DatabaseOperation;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.hucompute.textimager.uima.gervader.GerVaderSentiment;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * Source: @ https://gitlab.texttechnologylab.org/LeanderHermanns/uebung2 copied and reworked from my own Uebung2.
 * initializes the pipeline and calls the function that creates the JCas objects.
 * @author Leander Hermanns
 */
public class Pipeline {
    static DatabaseOperation db = new DatabaseOperation();
    redeMongoDB dbrede;
    List<JCas> jCasrede = new ArrayList<>();

    /**
     * Source: @ https://gitlab.texttechnologylab.org/LeanderHermanns/uebung2 copied and reworked from my own Uebung2.
     * initializes the pipeline and calls the function that creates the JCas objects.
     * also calls the function that adds all named entities, POS, named entities objects, sentiments and Token to the database.
     * @author Leander Hermanns
     * @modified Manuel Aha
     */
    public void generatejCAStop() throws UIMAException {
        AggregateBuilder aggregateBuilder = new AggregateBuilder();
        aggregateBuilder.add(createEngineDescription(SpaCyMultiTagger3.class, SpaCyMultiTagger3.PARAM_REST_ENDPOINT, "http://spacy.prg2021.texttechnologylab.org"));
        aggregateBuilder.add(createEngineDescription(GerVaderSentiment.class, GerVaderSentiment.PARAM_REST_ENDPOINT, "http://gervader.prg2021.texttechnologylab.org" , GerVaderSentiment.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"));
        AnalysisEngine pipeline = aggregateBuilder.createAggregate();
        List doclist = db.findAllDocument("speeches");
        Scanner Sitz = new Scanner(System.in);
        System.out.println('\n');
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> Geben Sie die Anzahl an Dokumenten, die Sie einlesen wollen, ein (1 - ...). Wenn Sie alle einlesen wollen geben Sie 0 ein.: <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        int limit = Integer.parseInt(Sitz.nextLine());  // Set the Number of documents that will be analysed. If not set program will take hours to calculate.
        int i  = 0;
        for (int k = 0; k < doclist.size(); k++){
            Document doc = (Document) doclist.get(k);
            i += 1;
            System.out.println("Dokument: " + i + " wird zu einem JCas konvertiert.");
            System.out.println(doc);
            dbrede = new redeMongoDB(doc);
            JCas jcas = dbrede.toCAS();
            jCasrede.add(jcas);
            dbrede.buildPipeline(jcas,pipeline);
            if (i == limit){
                break;
            }
        }

        db.deleteCollection("named entities objects");
        Map<String, Integer> mapneo = dbrede.printNamedEntitiesobjects(jCasrede);
        Map<String, String> mapneobyne = dbrede.printNamedEntitiesByObjects(jCasrede);
        for (String key: mapneo.keySet()){
            org.bson.Document document = new org.bson.Document("namedEntitiesObject", key);
            document.append("Häufigkeit", mapneo.get(key));
            document.append("LPO", mapneobyne.get(key));
            System.out.println(document);
            db.insertOneDocument("named entities objects", document);
        }

        db.deleteCollection("named entities");
        Map<String, Integer> mapne = dbrede.printNamedEntities(jCasrede);
        for (String key: mapne.keySet()){
            org.bson.Document document = new org.bson.Document("named entities", key);
            document.append("Häufigkeit",mapne.get(key));
            System.out.println(document);
            db.insertOneDocument("named entities", document);
        }

        db.deleteCollection("token");
        Map<String, Integer> maptoken = dbrede.printToken(jCasrede);
        for (String key: maptoken.keySet()){
            org.bson.Document document = new org.bson.Document("Token", key);
            document.append("Häufigkeit",maptoken.get(key));
            System.out.println(document);
            db.insertOneDocument("token", document);
        }

        db.deleteCollection("POS");
        Map<String, Integer> mappos = dbrede.printPos(jCasrede);
        for (String key: mappos.keySet()){
            org.bson.Document document = new org.bson.Document("POS", key);
            document.append("Häufigkeit",mappos.get(key));
            System.out.println(document);
            db.insertOneDocument("POS", document);
        }

        db.deleteCollection("sentiment");
        Map<Double, Integer> mapsentiment = dbrede.printSentiment(jCasrede);
        for (double key: mapsentiment.keySet()){
            org.bson.Document document = new org.bson.Document("sentiment", key);
            document.append("Häufigkeit",mapsentiment.get(key));
            System.out.println(document);
            db.insertOneDocument("sentiment", document);
        }

    }

    /**
     * Source: @ https://gitlab.texttechnologylab.org/LeanderHermanns/uebung2 copied and reworked from my own Uebung2.
     * initializes a pipeline with the needed Engine Descriptions.
     * @author Leander Hermanns
     * @return pipeline
     */
    public AnalysisEngine buildPipeline() throws ResourceInitializationException {
        AggregateBuilder aggregateBuilder = new AggregateBuilder();
        aggregateBuilder.add(createEngineDescription(SpaCyMultiTagger3.class, SpaCyMultiTagger3.PARAM_REST_ENDPOINT, "http://spacy.prg2021.texttechnologylab.org"));
        aggregateBuilder.add(createEngineDescription(GerVaderSentiment.class, GerVaderSentiment.PARAM_REST_ENDPOINT, "http://gervader.prg2021.texttechnologylab.org" , GerVaderSentiment.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"));
        AnalysisEngine pipeline = aggregateBuilder.createAggregate();
        return pipeline;
    }



}
