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

public class Rede_MongoDB {
    Document doc;
    List Cas = new ArrayList();


    /**
     * set the attribute document from mongodb.
     * @param mongodb
     */
    public Rede_MongoDB(Document mongodb){
        doc = mongodb;
    }

    /**
     * Source: @ https://gitlab.texttechnologylab.org/LeanderHermanns/uebung2_2020 copied and reworked from my own Uebung2 project from last year.
     * create a JCas Object of a document from the collection Tagesordnungspunkt for every speech.
     * @return JCas
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
     * builds up thew pipeline between the Analysis Engine and the JCas object.
     * @return true if document was created in MongoDB collection, false if not
     * @param jcas
     * @param pipeline
     */
    public void build_pipeline(JCas jcas, AnalysisEngine pipeline){
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
     * @return
     */
    public Map<String, Integer> print_token(List<JCas> jcas_list){
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
                    System.out.println("Token: '" + key + "'        Anzahl an Einträgen: " + map.get(key));
                    removed_key.add(key);
                }
            }
        }
        return map;
    }

    /**
     * caluates and sorts all used named entities by how much they were used and prints it out.
     * @param jcas_list
     * @return
     */
    public Map<String, Integer> print_named_entities(List<JCas> jcas_list){

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
                    System.out.println("Typ: '" + key + "'        Anzahl an Einträgen: " + map.get(key));
                    removed_key.add(key);
                }
            }
        }
        return map;
    }

    /**
     * caluates and sorts all used Part of Speeches by how much they were used and prints it out.
     * @param jcas_list
     * @return
     */
    public Map<String, Integer> print_pos(List<JCas> jcas_list) {
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
                    System.out.println("Wortart: '" + key + "'        Häufigkeit: " + map.get(key));
                    removed_key.add(key);
                }
            }
        }
        System.out.println(maphelper);
        return maphelper;
    }


    /**
     * Source: @ https://gitlab.texttechnologylab.org/LeanderHermanns/uebung2_2020 copied and reworked from my own Uebung2 project from last year.
     * create a JCas Object of a document from the collection Rede for every Comment.
     * @return JCas
     */
    public JCas toCAS_rede() {
        JCas jCAS = null;
        try {
            Object tag = doc.get("Kommentare");
            jCAS = JCasFactory.createText(tag.toString(), "de");

        } catch (UIMAException e) {
            System.out.println("Oops! Something went terribly wrong: Strange UIMAException ... ");
        }
        return jCAS;
    }


    /**
     * caluates and sorts every speech by the sentiment of all the comments made during that speech and prints it out.
     * @param jcas_list
     *@param rede_ID
     */
    public void print_rede(List<JCas> jcas_list,List rede_ID){
        Map<String, Integer> map = new HashMap<String, Integer>();
        Map<String, Integer> map_good = new HashMap<String, Integer>();
        Map<String, Integer> map_bad = new HashMap<String, Integer>();
        Map<String, Integer> map_neutral = new HashMap<String, Integer>();
        for (int k = 0; k < jcas_list.size(); k++) {
            int good  = 0;
            int bad = 0;
            int neutral = 0;
            for (Sentence sentence : JCasUtil.select(jcas_list.get(k), Sentence.class)) {
                for (Sentiment sentiment : JCasUtil.selectCovered(Sentiment.class, sentence)) {
                    //System.out.println(sentence.getCoveredText() + "  " + sentiment.getSentiment() + " " + k);
                    if (sentiment.getSentiment() < 0.00){
                        bad += 1;
                    }
                    if (sentiment.getSentiment() == 0.00){
                        if (sentence.getCoveredText().equals("[]")){
                        }
                        else if (sentence.getCoveredText().equals("]")){
                        }
                        else {
                            neutral += 1;
                        }
                    }
                    if (sentiment.getSentiment() > 0.00){
                        good += 1;
                    }
                }
            }
            map_good.put((String) rede_ID.get(k), good);
            map_bad.put((String) rede_ID.get(k), bad);
            map_neutral.put((String) rede_ID.get(k), neutral);
            }

        // Calculate Listings of the Speeches with the most positiv sentiments.
        Integer highest_val_good = 0;
        for (Integer value: map_good.values()){
            if (value > highest_val_good) {
                highest_val_good = value;
            }
        }

        List<String>  removed_key_good = new ArrayList();
        System.out.println('\n' + ">>>>>>>>>>>>>>>>> Auflistung der Reden nach den meisten positiven Zurufen absteigend sortiert.  <<<<<<<<<<<<<<<<<");
        for (int i = highest_val_good; i >= 0; i--){
            removed_key_good.clear();
            for (String key: map_good.keySet()){
                if (map_good.get(key).equals(i)){
                    System.out.println("Rede ID: '" + key + "'        Häufigkeit: " + map_good.get(key));
                    removed_key_good.add(key);
                }
            }
            for (String key: removed_key_good){
                map_good.remove(key);
            }
        }

        // Calculate Listings of the Speeches with the most neutral sentiments.
        Integer highest_val_neutral = 0;
        for (Integer value: map_neutral.values()){
            if (value > highest_val_neutral) {
                highest_val_neutral = value;
            }
        }

        List<String>  removed_key_neutral = new ArrayList();
        System.out.println('\n' + ">>>>>>>>>>>>>>>>> Auflistung der Reden nach den meisten neutral Zurufen absteigend sortiert.  <<<<<<<<<<<<<<<<<");
        for (int i = highest_val_neutral; i >= 0; i--){
            removed_key_neutral.clear();
            for (String key: map_neutral.keySet()){
                if (map_neutral.get(key).equals(i)){
                    System.out.println("Rede ID: '" + key + "'        Häufigkeit: " + map_neutral.get(key));
                    removed_key_neutral.add(key);
                }
            }
            for (String key: removed_key_neutral){
                map_neutral.remove(key);
            }
        }

        // Calculate Listings of the Speeches with the most negativ sentiments.
        Integer highest_val_bad = 0;
        for (Integer value: map_bad.values()){
            if (value > highest_val_bad) {
                highest_val_bad = value;
            }
        }
        //System.out.println(map_bad);
        List<String>  removed_key_bad = new ArrayList();
        System.out.println('\n' + ">>>>>>>>>>>>>>>>> Auflistung der Reden nach den meisten negativen Zurufen absteigend sortiert.  <<<<<<<<<<<<<<<<<");
        for (int i = highest_val_bad; i >= 0; i--){
            removed_key_bad.clear();
            for (String key: map_bad.keySet()){
                if (map_bad.get(key).equals(i)){
                    System.out.println("Rede ID: '" + key + "'        Häufigkeit: " + map_bad.get(key));
                    removed_key_bad.add(key);
                }
            }
            for (String key: removed_key_bad){
                map_bad.remove(key);
            }
        }
    }
}
