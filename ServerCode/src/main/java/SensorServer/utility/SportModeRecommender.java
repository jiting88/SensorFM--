package SensorServer.utility;

import SensorServer.entity.TrackInfoRemote;
import SensorServer.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rootK on 2016/9/9.
 */
@Component
public class SportModeRecommender {
    @Autowired
    private TrackService trackService;

    public void setTrackService(TrackService trackService) {
        this.trackService = trackService;
    }

    // Basic thinking: Slightly higher than current HR
    public TrackInfoRemote heartRateRecommend(Integer heartRate){
        final Integer interval=5;
        Integer low=heartRate+interval,high=low+interval;
        List<TrackInfoRemote> trackInfoList;
        do {
            trackInfoList = trackService.findTrackByBpm(low,high);
            low -= interval;
            high += interval;
            // Todo First Filter last played tracks
        }while (trackInfoList.isEmpty());
        int loc=(int)(Math.random()*trackInfoList.size());
        return trackInfoList.get(loc);
    }
    // TODO Predict the trend by recorded HR, calculate another range.
}