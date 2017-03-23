package SensorServer.service;

import SensorServer.dao.TrackDao;
import SensorServer.entity.TrackInfoRemote;
import com.google.gson.Gson;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rootK on 2016/9/8.
 */
@Component
public class TrackServiceImpl implements TrackService {
    @Autowired
    TrackDao trackDao;
    @Autowired
    TrackAnalyzer trackAnalyzer;

    public void setTrackAnalyzer(TrackAnalyzer trackAnalyzer) {
        this.trackAnalyzer = trackAnalyzer;
    }

    public void setTrackDao(TrackDao trackDao) {
        this.trackDao = trackDao;
    }

    @Override
    public Document getTrackInfoById(Integer id){
        List<Document> result=trackDao.queryTrack(Filters.eq("id",id));
        if (result==null || result.isEmpty()){
            //Retrieve+Download+Decode+Analysis
            return trackAnalyzer.analysis(id);
        }
        else
            return result.get(0);
    }

    @Override
    public List<TrackInfoRemote> findTrackByBpm(Integer low, Integer high) {
        List<Document> queryResult=trackDao.queryTrack(Filters.and(Filters.gte("bpm",low),Filters.lte("bpm",high)));
        List<TrackInfoRemote> result=new ArrayList<>();
        Gson gson=new Gson();
        for (Document doc:queryResult){
            result.add(gson.fromJson(gson.toJson(doc),TrackInfoRemote.class));
        }
        return result;
    }
}
