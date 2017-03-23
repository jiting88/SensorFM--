package SensorServer.utility;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Component;

/**
 * Created by rootK on 2016/9/7.
 */
@Component
public class MongoDBJDBC3 {
    MongoClient client;

    public MongoDBJDBC3(){
        client=new MongoClient("localhost",27017);
    }

    public MongoCollection<Document> getCollection(String collectionName){
        return client.getDatabase("SensorFM").getCollection(collectionName);
    }
}
