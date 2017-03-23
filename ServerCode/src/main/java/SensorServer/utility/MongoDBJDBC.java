package SensorServer.utility;

/**
 * Created by jicl on 16/6/30.
 */
import com.mongodb.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MongoDBJDBC{
    private DB db;
    public MongoDBJDBC(){
        Mongo mongo = new Mongo( "localhost" , 27017 );
        db = mongo.getDB("SensorFM");
    }
    public DB connectUserInfo(){
        if(!db.collectionExists("UserInfo")){
            DBObject optial=new BasicDBObject().append("capped",false);
            db.createCollection("UserInfo",optial);
        }
        return db;
    }
}