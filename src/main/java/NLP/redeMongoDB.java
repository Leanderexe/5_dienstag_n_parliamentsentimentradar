package NLP;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.hucompute.textimager.uima.type.Sentiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Source: @ https://gitlab.texttechnologylab.org/LeanderHermanns/uebung2 copied and reworked from my own Uebung2.
 * The class holds methods that can analyse the JCas objects for the different NLP methods.
 * @author Leander Hermanns
 */
public class redeMongoDB {
    Document doc;
    List Cas = new ArrayList();


    /**
     * Source: @ https://gitlab.texttechnologylab.org/LeanderHermanns/uebung2 copied and reworked from my own Uebung2.
     * set the attribute document from mongodb.
     * @param mongodb
     * @author Leander Hermanns
     */
    public redeMongoDB(Document mongodb){
        doc = mongodb;
    }

    /**
     * Source: @ https://gitlab.texttechnologylab.org/LeanderHermanns/uebung2_2020 copied and reworked from my own Uebung2.
     * create a JCas Object of a document from the collection Tagesordnungspunkt for every speech.
     * @return JCas
     * @author Leander Hermanns
     */
    public JCas toCAS() {
        JCas jCAS = null;
        try {
            Object tag = doc.get("content");
            System.out.println(tag);
            jCAS = JCasFactory.createText(tag.toString(), "de");
            Cas.add(jCAS);


        } catch (UIMAException e) {
            System.out.println("Oops! Something went terribly wrong: Strange UIMAException ... ");
        }
        return jCAS;
    }

    /**
     * builds up the pipeline between the Analysis Engine and the JCas object.
     * @param jcas
     * @param pipeline
     * @author Leander Hermanns
     */
    public void buildPipeline(JCas jcas, AnalysisEngine pipeline){
        try {
            System.out.println('\n');
            SimplePipeline.runPipeline(jcas, pipeline);
            System.out.println('\n');
        }
        catch (Exception AnalysisEngineProcessException){
            System.out.println("Oops! Something went terribly wrong: AnalysisEngineProcessException, your pipeline failed!");
        }
    }

    /**
     * caluates and sorts all used tokens by how much they were used and prints it out.
     * @param jcas_list
     * @return map with all analysed Tokens and the number of appearances.
     * @author Leander Hermanns
     */
    public Map<String, Integer> printToken(List<JCas> jcas_list){
        Map<String, Integer> sortedmap = new HashMap<String, Integer>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int k = 0; k < jcas_list.size(); k++) {
            for (Token token : JCasUtil.select(jcas_list.get(k), Token.class)) {
                if (map.containsKey(token.getCoveredText())) {
                    map.replace(token.getCoveredText(), map.get(token.getCoveredText()), map.get(token.getCoveredText()) + 1);
                } else {
                    map.put(token.getCoveredText(), 1);
                }
            }
            // Filter out the List brackets.
            map.replace(("["), map.get("["), map.get("[")-1);
            map.replace(("]"), map.get("]"), map.get("]")-1);
        }
        Integer highest_val = 0;

        // find the highest value in the hashmap.
        for (Integer value: map.values()){
            if (value > highest_val) {
                highest_val = value;
            }
        }

        List<String>  removed_key = new ArrayList();
        System.out.println('\n' + ">>>>>>>>>>>>>>>>> Auflistung der Tokens nach der Anzahl an Vorkommnissen absteigend sortiert.  <<<<<<<<<<<<<<<<<");
        for (int i = highest_val; i > 0; i--){
            removed_key.clear();
            for (String key: map.keySet()){
                if (map.get(key).equals(i)){
                    sortedmap.put(key, map.get(key));
                    System.out.println("Token: '" + key + "'        Anzahl an Einträgen: " + map.get(key));
                    removed_key.add(key);
                }
            }
        }
        return sortedmap;
    }

    /**
     * caluates and sorts all used named entities by how much they were used and prints it out.
     * @param jcas_list
     * @return map with all analysed named entities by Location, Organisation, MISC, Person and the number of appearances.
     * @author Leander Hermanns
     */
    public Map<String, Integer> printNamedEntities(List<JCas> jcas_list){
        Map<String, Integer> sortedmap = new HashMap<String, Integer>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int k = 0; k < jcas_list.size(); k++) {
            for (NamedEntity entity : JCasUtil.select(jcas_list.get(k), NamedEntity.class)) {
                if (map.containsKey(entity.getValue())) {
                    map.replace(entity.getValue(), map.get(entity.getValue()), map.get(entity.getValue()) + 1);
                } else {
                    map.put(entity.getValue(), 1);
                }
            }
        }

        Integer highest_val = 0;

        for (Integer value: map.values()){
            if (value > highest_val) {
                highest_val = value;
            }
        }
        List<String>  removed_key = new ArrayList();
        System.out.println('\n' + ">>>>>>>>>>>>>>>>> Auflistung der named entities nach dessen Typ absteigend sortiert.  <<<<<<<<<<<<<<<<<");
        for (int i = highest_val; i > 0; i--){
            removed_key.clear();
            for (String key: map.keySet()){
                if (map.get(key).equals(i)){
                    sortedmap.put(key, map.get(key));
                    System.out.println("Typ: '" + key + "'        Anzahl an Einträgen: " + map.get(key));
                    removed_key.add(key);
                }
            }
        }
        return sortedmap;
    }

    /**
     * caluates and sorts all used named entities objects by how much they were used and prints it out.
     * @param jcas_list
     * @return map with all analysed named entities and the number of appearances.
     * @author Leander Hermanns
     */
    public Map<String, Integer> printNamedEntitiesObjects(List<JCas> jcas_list){
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int k = 0; k < jcas_list.size(); k++) {
            for (NamedEntity entity : JCasUtil.select(jcas_list.get(k), NamedEntity.class)) {
                System.out.println(entity.getCoveredText() + " " + map);
                if (map.containsKey(entity.getCoveredText())) {
                    System.out.println(entity.getValue() + " " + entity.getCoveredText());
                    map.replace(entity.getCoveredText(), map.get(entity.getCoveredText()), map.get(entity.getCoveredText()) + 1);
                } else {
                    map.put(entity.getCoveredText(), 1);
                }
            }
        }
        return map;
    }

    /**
     * caluates and sorts all used named entities objects by their LPO and prints it out.
     * @param jcas_list
     * @return map with all analysed named entities and the by their LPO.
     * @author Leander Hermanns
     */
    public Map<String, String> printNamedEntitiesByObjects(List<JCas> jcas_list){
        Map<String, String> map = new HashMap<String, String>();
        for (int k = 0; k < jcas_list.size(); k++) {
            for (NamedEntity entity : JCasUtil.select(jcas_list.get(k), NamedEntity.class)) {
                System.out.println(entity.getCoveredText() + " " + map);
                if (map.containsKey(entity.getCoveredText())) {
                    System.out.println(entity.getValue() + " " + entity.getCoveredText());
                } else {
                    map.put(entity.getCoveredText(), entity.getValue());
                }
            }
        }
        return map;
    }

    /**
     * caluates and sorts all used Part of Speeches by how much they were used and prints it out.
     * @param jcas_list
     * @return map with all analysed POS and the number of appearances.
     * @author Leander Hermanns
     */
    public Map<String, Integer> printPos(List<JCas> jcas_list) {
        Map<String, Integer> sortedmap = new HashMap<String, Integer>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int k = 0; k < jcas_list.size(); k++) {
            for (Token token : JCasUtil.select(jcas_list.get(k), Token.class)) {
                if (map.containsKey(token.getPosValue())) {
                    map.replace(token.getPosValue(), map.get(token.getPosValue()), map.get(token.getPosValue()) + 1);
                } else {
                    map.put(token.getPosValue(), 1);

                }
            }
            map.replace(("NE"), map.get("NE"), map.get("NE") - 1); // Filter out the List bracket.
        }

        Map<String, Integer> maphelper = map;
        System.out.println(maphelper);
        Integer highest_val = 0;

        for (Integer value: map.values()){
            if (value > highest_val) {
                highest_val = value;
            }
        }

        List<String>  removed_key = new ArrayList();
        System.out.println('\n' + ">>>>>>>>>>>>>>>>> Auflistung der Wortarten nach der Anzahl an Vorkommnissen absteigend sortiert.  <<<<<<<<<<<<<<<<<");
        for (int i = highest_val; i > 0; i--){
            removed_key.clear();
            for (String key: map.keySet()){
                if (map.get(key).equals(i)){
                    sortedmap.put(key, map.get(key));
                    System.out.println("Wortart: '" + key + "'        Häufigkeit: " + map.get(key));
                    removed_key.add(key);
                }
            }
        }
        System.out.println(maphelper);
        return sortedmap;
    }



    /**
     * caluates and sorts every speech by the sentiment of all the comments made during that speech and prints it out.
     * @param jcas_list
     * @return map with all analysed Sentiments and the number of appearances.
     * @author Leander Hermanns
     * @modified Manuel Aha
     */
    public Map<Double, Integer> printSentiment(List<JCas> jcas_list){
        Map<Double, Integer> map = new HashMap<>();
        for (int k = 0; k < jcas_list.size(); k++) {
            for (Sentence sentence : JCasUtil.select(jcas_list.get(k), Sentence.class)) {
                for (Sentiment sentiment : JCasUtil.selectCovered(Sentiment.class, sentence)) {
                    //System.out.println(sentence.getCoveredText() + "  " + sentiment.getSentiment() + " " + k);
                    if (map.containsKey(sentiment.getSentiment())) {
                        map.replace(sentiment.getSentiment(), map.get(sentiment.getSentiment()), map.get(sentiment.getSentiment()) + 1);
                    } else {
                        map.put(sentiment.getSentiment(), 1);

                    }
                }
            }
            }
        return map;
    }


    /**
     * caluates and sorts every speech by the named entities by Location, Organisation, MISC, Person of all the comments made during that speech and prints it out.
     * @param jcas_list
     * @return list with all analysed named entities by Location, Organisation, MISC, Person by their appearance.
     * @author Leander Hermanns
     * @modified Manuel Aha
     */
    public List getNamedEntities(List<JCas> jcas_list) {
        List list = new ArrayList();
        for (int k = 0; k < jcas_list.size(); k++) {
            for (NamedEntity entity : JCasUtil.select(jcas_list.get(k), NamedEntity.class)) {
                list.add(entity.getValue());
            }
        }
        return list;
    }

    /**
     * caluates and sorts every speech by the named entities of all the comments made during that speech and prints it out.
     * @param jcas_list
     * @return list with all analysed named entities by their appearance.
     * @author Leander Hermanns
     * @modified Manuel Aha
     */
    public List getNamedEntitiesObjects(List<JCas> jcas_list) {
        List list = new ArrayList();
        for (int k = 0; k < jcas_list.size(); k++) {
            for (NamedEntity entity : JCasUtil.select(jcas_list.get(k), NamedEntity.class)) {
                list.add(entity.getCoveredText());
            }
        }
        return list;
    }
}
