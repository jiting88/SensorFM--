package SensorServer.dao;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

/**
 * Created by rootK on 2016/9/8.
 */
public interface TrackDao {
    void addTrack(Document track);

    void removeTrack(Document track);

    List<Document> queryTrack(Bson filter);
}
