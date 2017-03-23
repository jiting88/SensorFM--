package team.jfh.sensorfm.domain.interactor;

import java.io.IOException;

import team.jfh.sensorfm.data.entity.TrackInfoRemote;
import team.jfh.sensorfm.data.util.RestGet;

/**
 * Created by rootK on 2016/9/9.
 */
public class TrackInteractor {
    public TrackInfoRemote getTrackInfo(Integer id){
        RestGet restGet=new RestGet();
        try {
            return restGet.getEntity("/Track/" + id.toString(), TrackInfoRemote.class);
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
