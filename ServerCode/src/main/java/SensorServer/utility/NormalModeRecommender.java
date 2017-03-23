package SensorServer.utility;

import SensorServer.entity.InputForRecommendation;
import SensorServer.entity.TrackInfoRemote;
import SensorServer.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jicl on 16/9/13.
 */
@Component
public class NormalModeRecommender {
    @Autowired
    private TrackService trackService;
    public void setTrackService(TrackService trackService) {
        this.trackService = trackService;
    }
    public TrackInfoRemote recommend(InputForRecommendation input){
        List<TrackInfoRemote> trackInfoList=trackService.findTrackByBpm(0,300);
        int loc=(int)(Math.random()*trackInfoList.size());
        return trackInfoList.get(loc);
    }
}
