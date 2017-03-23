package team.jfh.sensorfm.data.entity;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by jicl on 16/9/8.
 */
public class MusicRecord {
    int number;
    Date time;
    int pulse;
    int magnitude;
    int latitude;
    int bpm;
    String song;

    public MusicRecord(){}

    public MusicRecord(int number,Date time, int pulse, int magnitude, int latitude, int bpm, String song){
        this.number=number;
        this.time=time;
        this.pulse=pulse;
        this.magnitude=magnitude;
        this.latitude=latitude;
        this.bpm=bpm;
        this.song=song;
    }

    public MusicRecord(int pulse, int magnitude, int latitude, int bpm, String song){
        this.pulse=pulse;
        this.magnitude=magnitude;
        this.latitude=latitude;;
        this.bpm=bpm;
        this.song=song;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(int magnitude) {
        this.magnitude = magnitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }
}
