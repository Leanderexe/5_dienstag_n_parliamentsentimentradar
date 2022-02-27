package API;

import API.JsonUtil;
import NLP.Pipeline;
import database.DatabaseOperation;
import org.apache.uima.UIMAException;
import org.bson.Document;
import org.json.JSONException;
import org.json.simple.parser.JSONParser;
import spark.Filter;


import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.w3c.dom.Document;


import static spark.Spark.*;

/**
 * Implements the API.RestAPI and defines the routes and parameters.
 * @author Leander Hermanns
 * @modified Manuel Aha
 */
public class RestAPI {
    private static JsonUtil util = new JsonUtil();
    static DatabaseOperation db = new DatabaseOperation();
    JSONParser parser = new JSONParser();
    public static void getAPI() throws IOException, JSONException, UIMAException {
        Pipeline pip = new Pipeline();
        //pip.generatejCAStop(); // generates the JCas objects.

        // url: http://localhost:4567/rest
        init();

        /**
         * Prints out JSON with all rendner at http://localhost:4567/redner
         * @return String with the all Redner in JSON.
         * @author Leander Hermanns
         */
        get("/redner", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("redner")){
                rednerlist.add(doc);
            }
                return rednerlist;
            }, util.json());


        /**
         * Makes the API return the respones in json format.
         * @author Leander Hermanns
         */
        after((request, response) -> {
            response.type("application/json");
        });


        /**
         * grant Access so one can get page content from javascript.
         * @author Leander Hermanns
         */
        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
                });



        /**
         * Prints out JSON with all speaker by their name which contain the search parameter at http://localhost:4567/redner/:searchstr
         * @return String with all speaker which contain the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/redner/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("redner")){
                String redner =  (String) doc.get("vorname") + " " + (String) doc.get("nachname");
                if (redner.contains(searchstr)){
                    rednerlist.add(doc);
                }
            }
            return rednerlist;
        }, util.json());


        /**
         * Prints out JSON with all speaker by their name which contain the search parameter at http://localhost:4567/redner/rede
         * @return String with all speaker which contain the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/redner/rede", (request, response) -> {
            //String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            System.out.println("hello");
            Map<String, Integer> map = new HashMap<String, Integer>();
            for(Document doc: db.findAllDocument("redner")) {
                map.put((String) doc.get("_id"), 0);
            }
            for(Document docrede: db.findAllDocument("speeches")) {
                System.out.println("hello2");
                String rednerid = (String) docrede.get("rednerID");
                if (map.containsKey(rednerid)) {
                    map.replace(rednerid, map.get(rednerid), map.get(rednerid) + 1);
                }
            }
            for(Document doc: db.findAllDocument("redner")) {
                System.out.println(map);
                if (map.containsKey((String) doc.get("_id"))){
                    doc.append("AnzahlanReden", map.get("_id"));
                    rednerlist.add(doc);
                }
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with all speaker whose id matches the search parameter at http://localhost:4567/redner/id/:searchstr
         * @return String with all speaker whose id matches the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/redner/id/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("redner")){
                Integer redner = (Integer) doc.get("_id");
                if (redner.toString().contains(searchstr)){
                    rednerlist.add(doc);
                }
            }
            return rednerlist;
        }, util.json());


        /**
         * Prints out JSON with all speaker whose fraction matches the search parameter at http://localhost:4567/redner/fraktion/:searchstr
         * @return String with all speaker whose fraction matches the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/redner/fraktion/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("redner")){
                String fraktion =  (String) doc.get("fraktion");
                if (fraktion.contains(searchstr)){
                    rednerlist.add(doc);
                }
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with all Tokens at http://localhost:4567/token
         * Exercise b1)
         * @return String with the all Tokens in JSON.
         * @author Leander Hermanns
         */
        get("/token", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("token")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with all tokens which contain the search parameter at http://localhost:4567/token/:searchstr
         * @return String with all tokens which contain the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/token/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("token")){
                String token =  (String) doc.get("Token");
                if (token.contains(searchstr)){
                    rednerlist.add(doc);
                }
            }
            return rednerlist;
        }, util.json());

        get("/hello", (request, response) ->  "Hello World", util.json());

        /**
         * Prints out JSON with all POS at http://localhost:4567/pos
         * Exercise b2)
         * @return String with the all POS in JSON.
         * @author Leander Hermanns
         */
        get("/pos", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("POS")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with all POS which contain the search parameter at http://localhost:4567/pos/:searchstr
         * @return String with POS which contain the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/pos/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("POS")){
                String pos = (String) doc.get("POS");
                if (pos.contains(searchstr)){
                    rednerlist.add(doc);
                }
            }
            return rednerlist;
        }, util.json());


        /**
         * Prints out JSON with all sentiment at http://localhost:4567/sentiment
         * Exercise b3)
         * @return String with the all sentiment in JSON.
         * @author Leander Hermanns
         */
        get("/sentiment", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("sentiment")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with all sentiments which contain the search parameter at http://localhost:4567/sentiment/:searchstr
         * @return String with all sentiments which contain the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/sentiment/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("sentiment")){
                Double sentiment =  (Double) doc.get("sentiment");
                if (sentiment.toString().contains(searchstr)){
                    rednerlist.add(doc);
                }
            }
            return rednerlist;
        }, util.json());


        /**
         * Prints out JSON with all named entities at http://localhost:4567/namedentities
         * Exercise b4)
         * @return String with the all named entities in JSON.
         * @author Leander Hermanns
         */
        get("/namedentities", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("named entities")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with all named entities which contain the search parameter at http://localhost:4567/namedentities/:searchstr
         * @return String with all named entities which contain the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/namedentities/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("named entities")){
                String sentiment =  (String) doc.get("named entities");
                if (sentiment.contains(searchstr)){
                    rednerlist.add(doc);
                }
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with all named entities which contain the search parameter at http://localhost:4567/namedentitiesobjects/LPO/:searchstr
         * @return String with all named entities which contain the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/namedentitiesobjects/LPO/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("named entities objects")){
                String lpo =  (String) doc.get("LPO");
                if (lpo.contains(searchstr)){
                    rednerlist.add(doc);
                }
            }
            return rednerlist;
        }, util.json());


        /**
         * Prints out JSON with all named entities objects at http://localhost:4567/namedentitiesobjects
         * Exercise b4)
         * @return String with the all named entities objects in JSON.
         * @author Leander Hermanns
         */
        get("/namedentitiesobjects", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("named entities objects")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with all named entities objects which contain the search parameter at http://localhost:4567/namedentitiesobjects/:searchstr
         * @return String with all named entities objects which contain the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/namedentitiesobjects/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("named entities objects")){
                String sentiment =  (String) doc.get("namedEntitiesObject");
                if (sentiment.contains(searchstr)){
                    rednerlist.add(doc);
                }
            }
            return rednerlist;
        }, util.json());



        /**
         * Prints out JSON with all speeches at http://localhost:4567/rede
         * Exercise b4)
         * @return String with the all speeches in JSON.
         * @author Leander Hermanns
         */
        get("/rede", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("speeches")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());


        /**
         * Prints out JSON with all speeches whose part of the content matches the search parameter at http://localhost:4567/rede/content/:searchstr
         * @return String with speches which contain the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/rede/content/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("speeches")){
                ArrayList contentlist = (ArrayList) doc.get("content");
                for (int i = 0; i < contentlist.size(); i++) {
                    String content = (String) contentlist.get(i);
                    if (content.contains(searchstr)) {
                        rednerlist.add(doc);
                        break;
                    }
                }
            }
            return rednerlist;
        }, util.json());


        /**
         * Prints out JSON with a speech whose id matches the search parameter at http://localhost:4567/rede/redeid/:searchstr
         * @return String with a speech which contain the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/rede/redeid/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("speeches")){
                String redeID =  (String) doc.get("redeID");
                    if (redeID.contains(searchstr)) {
                        rednerlist.add(doc);
                        break;
                    }
                }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with a speech whose id matches the search parameter at http://localhost:4567/rede/rednerid/:searchstr
         * @return String with a speech which contain the search parameter in JSON.
         * @author Leander Hermanns
         */
        get("/rede/rednerid/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("speeches")){
                String redeID =  (String) doc.get("rednerID");
                if (redeID.contains(searchstr)) {
                    rednerlist.add(doc);
                    break;
                }
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with all rendner at http://localhost:4567/protokoll
         * @return String with the all Redner in JSON.
         * @author Leander Hermanns
         */
        get("/protokoll", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("dbtplenarprotokoll")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());
    }



    /**
     * Merges a list of keys and a list of value to a Json String.
     * @return String with Json format
     * @author Leander Hermanns
     */
    private static String getJson(List key, List value) {
        String json = "";
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        for (int i = 0; i < key.size(); i++) {
            builder.add((String) key.get(i), (String) value.get(i));
        }
        json = builder.build().toString();
        return json;
    }

}

