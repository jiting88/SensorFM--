package SensorServer.dao;

import SensorServer.utility.MongoDBJDBC3;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Projections.excludeId;

/**
 * Created by rootK on 2016/9/7.
 */
@Component
public class TrackDaoImpl implements TrackDao {

    @Qualifier("mongoDBJDBC3")
    @Autowired
    private MongoDBJDBC3 mongoDBJDBC3;

    public void setMongoDBJDBC3(MongoDBJDBC3 mongoDBJDBC3) {
        this.mongoDBJDBC3 = mongoDBJDBC3;
    }

    @Override
    public void addTrack(Document track){
        mongoDBJDBC3.getCollection("Track").insertOne(track);
    }

    @Override
    public void removeTrack(Document track){
        mongoDBJDBC3.getCollection("Track").deleteOne(track);
    }

    @Override
    public List<Document> queryTrack(Bson filter){
        return mongoDBJDBC3.getCollection("Track").find(filter).projection(excludeId()).into(new ArrayList<>());
    }
}
