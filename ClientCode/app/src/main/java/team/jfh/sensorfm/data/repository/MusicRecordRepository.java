package team.jfh.sensorfm.data.repository;

import java.util.List;

import team.jfh.sensorfm.data.entity.MusicRecord;

/**
 * Created by jicl on 16/9/10.
 */
public interface MusicRecordRepository {
    void addRecord(MusicRecord mr);

    MusicRecord getMusicRecord(int number);

    List<MusicRecord> getAllRecords();

    void deleteRecord(MusicRecord mr);
}
