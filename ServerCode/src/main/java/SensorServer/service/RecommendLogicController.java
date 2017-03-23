package SensorServer.service;

import SensorServer.entity.InputForRecommendation;
import SensorServer.entity.TrackInfoRemote;
import SensorServer.utility.NormalModeRecommender;
import SensorServer.utility.SleepModeRecommender;
import SensorServer.utility.SportModeRecommender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by rootK on 2016/9/10.
 */
@Component
public class RecommendLogicController {
    @Autowired SleepModeRecommender sleep;
    @Autowired SportModeRecommender sport;
    @Autowired NormalModeRecommender recommender;

    public TrackInfoRemote recommend(InputForRecommendation input){
        if(input.getMode().equals("sleep")){
            return sleep.heartRateRecommend(input.getHeartRate());
        }
        if(input.getMode().equals("sport")){
            return sport.heartRateRecommend(input.getHeartRate());
        }
        else {
            return recommender.recommend(input);
        }
    }
}
