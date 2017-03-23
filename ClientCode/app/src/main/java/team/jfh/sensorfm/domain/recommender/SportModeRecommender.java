package team.jfh.sensorfm.domain.recommender;

import java.util.ArrayList;
import java.util.List;

import team.jfh.sensorfm.data.entity.TrackInfo;
import team.jfh.sensorfm.data.repository.TrackInfoRepository;

/**
 * Created by rootK on 2016/9/9.
 */
public class SportModeRecommender {

    TrackInfoRepository trackInfoRepository;
    // Basic thinking: Slightly higher than current HR
    public TrackInfo heartRateRecommend(Integer heartRate){
        final Integer interval=5;
        Integer low=heartRate+interval,high=low+interval;
        List<TrackInfo> trackInfoList;
        do {
            trackInfoList = trackInfoRepository.findTrackByBpm(low,high);
            low -= interval;
            high += interval;
            // Todo First Filter last played tracks
        }while (trackInfoList.isEmpty());
        int loc=(int)(Math.random()*trackInfoList.size());
        return trackInfoList.get(loc);
    }
    // TODO Predict the trend by recorded HR, calculate another range.
}