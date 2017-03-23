package team.jfh.sensorfm.data.repository;

import java.util.List;

import team.jfh.sensorfm.data.entity.TrackInfo;

/**
 * Created by rootK on 2016/9/9.
 */
public interface TrackInfoRepository {
    public void addTrackInfo(TrackInfo trackInfo);
    public void removeTrackInfoByLocation(String Location);
    public TrackInfo findTrackByLocation(String Location);
    public List<TrackInfo> findTrackByBpm(Integer low,Integer high);
}