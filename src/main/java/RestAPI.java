import NLP.Pipeline;
import database.DatabaseOperation;
import org.apache.uima.UIMAException;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import spark.Filter;


import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import org.w3c.dom.Document;
import org.w3c.dom.Node;


import static spark.Spark.*;


public class RestAPI {
    private static JsonUtil util = new JsonUtil();
    static DatabaseOperation db = new DatabaseOperation();
    JSONParser parser = new JSONParser();
    public static void main(String[] args) throws IOException, JSONException, UIMAException {
        Pipeline pip = new Pipeline();
        //pip.generate_jCAS_top(); // generates the JCas objects.
        // String[] redner = {"angela Merkel", "Putin", "Markon", "Lauterbach", "Amthor", "trump", "sleepy joe"};
        List key = new ArrayList();
        List value = new ArrayList();
        for(int i = 0; i < 10; i++){
            key.add("key " + i);
            value.add("value " + i);
        }
        //System.out.println(db.findAllDocument("redner"));
        //List rednerlist = new ArrayList();
        //for(Document doc: db.findAllDocument("redner")){
        //    rednerlist.add(doc.toJson());
        //}
        //String output = getsingleJson("redner", rednerlist.toString());
        //System.out.println(rednerlist);

/*
        String rednerstring = "{";
        for (int i = 0; i < redner.length; i++){
            if (i == redner.length-1){
                rednerstring += redner[i] + "}";
            }
            else {
                rednerstring += redner[i] + ", ";
            }
        }

        System.out.println(rednerstring);
        */
        String json = getJson(key, value);
        // url: http://localhost:4567/rest
        init();
        //initExceptionHandler((e) -> System.out.println("Ups! Der Server konnte leider nicht gestartet werden."));
        /*
        post("/rest", ((request, response) ->
                {
                    response.redirect("/bar");
                    request.headers("okay lets go!");
                    return true;
                }
                ));

        get("/rest", (request, response) -> "Hello");

        get("/rest", ((request, response) ->
        {
            String hello = request.headers("i bims ein header");
            response.header(hello, "2");
            return true;
        }));
        //halt(400);

         */
        //final String finalRednerstring = "hello world";
        /**
         * Prints out JSON with all rendner at http://localhost:4567/redner
         * @return String with the all Redner in JSON.
         */
        get("/redner", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("redner")){
                rednerlist.add(doc);
            }
                return rednerlist;
            }, util.json());

        after((request, response) -> {
            response.type("application/json");
        });

        // grant Access so one can get page content from javascript.
        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
                });



        /**
         * Prints out JSON with redner which contain the search parameter at http://localhost:4567/redner/:searchstr
         * @return String with redner which contain the search parameter in JSON.
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
         * Prints out JSON with redner which contain the search parameter at http://localhost:4567/redner/fraktion/:searchstr
         * @return String with redner which contain the search parameter in JSON.
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
         */
        get("/token", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("token")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with redner which contain the search parameter at http://localhost:4567/token/:searchstr
         * @return String with redner which contain the search parameter in JSON.
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
         */
        get("/pos", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("POS")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with redner which contain the search parameter at http://localhost:4567/pos/:searchstr
         * @return String with redner which contain the search parameter in JSON.
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
         */
        get("/sentiment", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("sentiment")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with redner which contain the search parameter at http://localhost:4567/sentiment/:searchstr
         * @return String with redner which contain the search parameter in JSON.
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
         */
        get("/namedentities", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("named entities")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with redner which contain the search parameter at http://localhost:4567/namedentities/:searchstr
         * @return String with redner which contain the search parameter in JSON.
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
         * Prints out JSON with all named entities at http://localhost:4567/namedentitiesobjects
         * Exercise b4)
         * @return String with the all named entities in JSON.
         */
        get("/namedentitiesobjects", (request, response) -> {
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("named entities objects")){
                rednerlist.add(doc);
            }
            return rednerlist;
        }, util.json());

        /**
         * Prints out JSON with redner which contain the search parameter at http://localhost:4567/namedentitiesobjects/:searchstr
         * @return String with redner which contain the search parameter in JSON.
         */
        get("/namedentitiesobjects/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("named entities objects")){
                String sentiment =  (String) doc.get("named entities object");
                if (sentiment.contains(searchstr)){
                    rednerlist.add(doc);
                }
            }
            return rednerlist;
        }, util.json());

    }

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

    private static String byname(String name, List key, List value) throws JSONException {
        JSONObject jn = new JSONObject("hello");
        if (key.contains(name)){
            for(int i = 0; i < key.size(); i++){
                if (key.get(i).toString().equals(name)){
                    getsingleJson((String)key.get(i), (String)value.get(i));
                    return getsingleJson((String)key.get(i), (String)value.get(i));
                }
            }
            return "false";
        }
        else{
            return "false";
        }
    }

    private static String getsingleJson(String key, String value) {
        String json = "";
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(key, value);
        json = builder.build().toString();
        return json;
    }

    /*
    private static List sortData(List data){

        for (int i = 0; i < data.size(); i++){
            Integer value = data.get(i);
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

     */


}

