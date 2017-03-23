package team.jfh.sensorfm.data.repository;

/**
 * Created by jicl on 16/9/10.
 * Minor Bug fixed by odinaryk on 16/9/11
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;
import team.jfh.sensorfm.data.entity.TrackInfo;
import team.jfh.sensorfm.data.repository.TrackInfoRepository;

public class TrackInfoRepositorySQLite extends SQLiteOpenHelper implements TrackInfoRepository {
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="SensorDatabase";
    private static final String TABLE_RECORDS="TrackInfo";

    private static final String KEY_LOCATION="location";
    private static final String KEY_TITLE="title";
    private static final String KEY_BPM="bpm";

    public TrackInfoRepositorySQLite(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RECORDS + "("
                + KEY_LOCATION + " TEXT PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_BPM + " INTEGER"+")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    public void addTrackInfo(TrackInfo trackInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION,trackInfo.getLocation());
        values.put(KEY_TITLE,trackInfo.getTitle());
        values.put(KEY_BPM,trackInfo.getBpm());
        db.insert(TABLE_RECORDS, null, values);
        db.close();
    }
    public void removeTrackInfoByLocation(String Location){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDS, KEY_LOCATION + " = ?",
                new String[] { String.valueOf(Location) });
        db.close();
    }
    public TrackInfo findTrackByLocation(String Location){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECORDS, new String[] { KEY_LOCATION, KEY_TITLE, KEY_BPM }, KEY_LOCATION + "=?",
                new String[] { String.valueOf(Location) },null, null, null, null);
        TrackInfo trackInfo=null;
        if (cursor != null)
            cursor.moveToFirst();
        try {
            trackInfo = new TrackInfo(cursor.getString(0), cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)));
            //location title artist bpm
            cursor.close();
        }
        catch (Exception e){
            //Emergency fixed
//            e.printStackTrace();
        }
        db.close();
        return trackInfo;
    }
    public List<TrackInfo> findTrackByBpm(Integer low, Integer high){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectquery="SELECT * FROM "+TABLE_RECORDS+" WHERE "+KEY_BPM+">=? AND "+KEY_BPM+"<=?";
        Cursor cursor=db.rawQuery(selectquery,new String[]{String.valueOf(low),String.valueOf(high)});
        //Cursor cursor=db.query(TABLE_RECORDS,null,"bpm between ? and ?", range, null, null, null);
        List<TrackInfo>trackInfoList=new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                TrackInfo trackInfo=new TrackInfo();
                trackInfo.setLocation(cursor.getString(0));
                trackInfo.setTitle(cursor.getString(1));
                trackInfo.setBpm(Integer.parseInt(cursor.getString(2)));
                trackInfoList.add(trackInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return trackInfoList;
    }

}
