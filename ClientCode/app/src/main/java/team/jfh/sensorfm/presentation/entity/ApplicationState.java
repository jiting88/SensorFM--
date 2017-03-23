package team.jfh.sensorfm.presentation.entity;

/**
 * Created by rootK on 2016/9/11.
 */
public class ApplicationState {
    private String mode;
    private Boolean band;
    private Boolean online;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Boolean getBand() {
        return band;
    }

    public void setBand(Boolean band) {
        this.band = band;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }
}
