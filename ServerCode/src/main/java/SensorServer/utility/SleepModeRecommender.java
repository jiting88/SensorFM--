package SensorServer.utility;

import java.util.List;

import SensorServer.entity.TrackInfoRemote;
import SensorServer.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by rootK on 2016/9/9.
 */
@Component
public class SleepModeRecommender {
    @Autowired
    private TrackService TrackService;
    private final Integer threshold=60;

    public void setTrackService(SensorServer.service.TrackService trackService) {
        TrackService = trackService;
    }

    // Basic thinking: Slightly lower than current HR
    public TrackInfoRemote heartRateRecommend(Integer heartRate){
        final Integer interval=5;
        if (heartRate<threshold)
            return null;
        Integer low=heartRate-interval,high=low-interval;
        List<TrackInfoRemote> trackInfoList;
        do {
            trackInfoList = TrackService.findTrackByBpm(low,high);
            low -= interval;
            high += interval;
            // Todo First Filter last played tracks
        }while (trackInfoList.isEmpty());
        int loc=(int)(Math.random()*trackInfoList.size());
        return trackInfoList.get(loc);
    }
}
