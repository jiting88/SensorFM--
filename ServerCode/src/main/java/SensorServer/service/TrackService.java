package SensorServer.service;

import SensorServer.entity.TrackInfoRemote;
import org.bson.Document;

import java.util.List;

/**
 * Created by rootK on 2016/9/8.
 */
public interface TrackService {
    Document getTrackInfoById(Integer id);
    List<TrackInfoRemote> findTrackByBpm(Integer low, Integer high);
}
