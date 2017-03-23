package team.jfh.sensorfm.domain.interactor;

import android.app.Activity;

/**
 * Created by rootK on 2016/9/2.
 */
public interface HeartRateManager {
    public void connect();
    public void disconnect();
    public int getCurrentHeartRate();
    public Boolean getStatus();
}