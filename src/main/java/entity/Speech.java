package entity;

import java.util.Date;
import java.util.List;

/**
 * Speech class which will be the construct for the speech information of the protocols and
 * enters the database as document
 * @author Manuel Aha
 */
public class Speech {

    // All necessary attributes
    String date;
    String speakerID;
    String fraktion;
    String speechID;
    List content;
    List comments;

    /**
     * construcotr class for initial creation
     * @param date
     * @param speakerID
     * @param speechID
     * @param content
     * @param comments
     * @author Manuel Aha
     */
    public Speech (String date, String speakerID,  String speechID, List content, List comments) {
        this.date = date;
        this.speakerID = speakerID;
        this.speechID = speechID;
        this.content = content;
        this.comments = comments;

    }

    /**
     * showing the class attributes
     * @author Manuel Aha
     */
    public void printSpeech() {
        System.out.println(" Date: " + date );
        System.out.println(" speakerID " + speakerID );
        System.out.println(" speechID " + speechID );
        System.out.println(" content " + content );
        System.out.println(" comments" + comments );
    }

}
