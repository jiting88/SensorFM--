package team.jfh.sensorfm.domain.interactor;

import java.io.IOException;

import team.jfh.sensorfm.data.entity.InputForRecommendation;
import team.jfh.sensorfm.data.entity.TrackInfo;
import team.jfh.sensorfm.data.entity.TrackInfoRemote;
import team.jfh.sensorfm.data.util.ServerFormSubmit;
import team.jfh.sensorfm.domain.recommender.SportModeRecommender;

/**
 * Created by rootK on 2016/9/11.
 */
public class RecommendationController {
    public static TrackInfo recommend(InputForRecommendation rinput,Boolean online){
        if (online){
            ServerFormSubmit submit=new ServerFormSubmit();
            try {
                TrackInfoRemote result = submit.access("/Recommend", rinput, TrackInfoRemote.class);
                return result;
            }catch (IOException e){
                e.printStackTrace();
                //Continue local recommendation
            }
        }
        if (rinput.getMode().equals("sport"))
            return new SportModeRecommender().heartRateRecommend(rinput.getHeartRate());
        if (rinput.getMode().equals("sleep"))
            return new SportModeRecommender().heartRateRecommend(rinput.getHeartRate());
        if (rinput.getMode().equals("normal"))
            return null;
        return null;
    }
}
