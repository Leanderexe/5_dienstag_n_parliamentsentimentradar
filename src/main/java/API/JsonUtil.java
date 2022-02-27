package API;

import com.google.gson.Gson;
import database.Operation;
import spark.ResponseTransformer;


/**
 * Source: @https://www.mscharhag.com/java/building-rest-api-with-spark
 * @author Leander Hermanns
 */
public class JsonUtil {

    /**
     * Source: @https://www.mscharhag.com/java/building-rest-api-with-spark
     * @param object
     * @return String
     * @author Leander Hermanns
     */
    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    /**
     * Source: @https://www.mscharhag.com/java/building-rest-api-with-spark
     * @return ResponseTransformer
     * @author Leander Hermanns
     */
    public static ResponseTransformer json() {
        return JsonUtil::toJson;
    }
}
