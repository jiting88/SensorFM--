package SensorServer.entity;

/**
 * Created by rootK on 2016/9/10.
 */
public class InputForRecommendation {
    private Integer heartRate;
    private Integer pedPerMin;
    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getPedPerMin() {
        return pedPerMin;
    }

    public void setPedPerMin(Integer pedPerMin) {
        this.pedPerMin = pedPerMin;
    }
}
