package team.jfh.sensorfm.data.entity;

/**
 * Created by rootK on 2016/9/9.
 */
public class TrackInfo {
    private String location;// PK
    private String title;
    private String singer;
    private Integer bpm;

    public TrackInfo() {
    }

    public TrackInfo(String location, String title, String singer, Integer bpm) {
        this.title = title;
        this.location = location;
        this.bpm = bpm;
        this.singer = singer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getBpm() {
        return bpm;
    }

    public void setBpm(Integer bpm) {
        this.bpm = bpm;
    }

    public String getSinger(){
        return singer;
    }

    public void setSinger(String singer){
        this.singer = singer;
    }
}
