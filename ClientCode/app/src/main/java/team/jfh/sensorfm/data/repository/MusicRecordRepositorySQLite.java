package team.jfh.sensorfm.data.repository;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;
import team.jfh.sensorfm.data.entity.MusicRecord;
import team.jfh.sensorfm.data.repository.MusicRecordRepository;

/**
 * Created by rootK on 2016/9/8.
 */
public class MusicRecordRepositorySQLite extends SQLiteOpenHelper implements MusicRecordRepository {
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="SensorDatabase";
    private static final String TABLE_RECORDS="MusicRecords";

    private static final String KEY_ID="number";
    private static final String KEY_TIME="time";
    private static final String KEY_PULSE="pulse";
    private static final String KEY_MAGNITUDE="magnitude";
    private static final String KEY_LATITUDE="latitude";
    private static final String KEY_BPM="bpm";
    private static final String KEY_SONG="song";

    public MusicRecordRepositorySQLite(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RECORDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TIME + " DATETIME,"
                + KEY_PULSE + " INTEGER," + KEY_MAGNITUDE + " INTEGER,"
                + KEY_LATITUDE + " INTEGER," + KEY_BPM + " INTEGER," + KEY_SONG + " TEXT"+")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void addRecord(MusicRecord mr){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();;
        values.put(KEY_TIME,getDateTime());
        values.put(KEY_PULSE,mr.getPulse());
        values.put(KEY_MAGNITUDE,mr.getMagnitude());
        values.put(KEY_LATITUDE,mr.getLatitude());
        values.put(KEY_BPM,mr.getBpm());
        values.put(KEY_SONG,mr.getSong());

        db.insert(TABLE_RECORDS, null, values);
        db.close();
    }

    @Override
    public MusicRecord getMusicRecord(int number){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECORDS, new String[] { KEY_ID, KEY_TIME,KEY_PULSE,KEY_MAGNITUDE,
                KEY_LATITUDE,KEY_BPM,KEY_SONG }, KEY_ID + "=?",
                new String[] { String.valueOf(number) },null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String myDate =cursor.getString(cursor.getColumnIndex("time"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        MusicRecord mr=null;
        try {
            Date date = format.parse(myDate);
            mr=new MusicRecord(Integer.parseInt(cursor.getString(0)),date
                ,Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)),cursor.getString(6));
            cursor.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.close();
        return mr;
    }

    @Override
    public List<MusicRecord> getAllRecords() {
        List<MusicRecord> recordList = new ArrayList<MusicRecord>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RECORDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MusicRecord mr=new MusicRecord();

                mr.setNumber(Integer.parseInt(cursor.getString(0)));
                String myDate =cursor.getString(cursor.getColumnIndex("time"));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                try {
                    Date date =(Date)format.parse(myDate);
                    mr.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                mr.setPulse(Integer.parseInt(cursor.getString(2)));
                mr.setMagnitude(Integer.parseInt(cursor.getString(3)));
                mr.setLatitude(Integer.parseInt(cursor.getString(4)));
                mr.setBpm(Integer.parseInt(cursor.getString(5)));
                mr.setSong(cursor.getString(6));

                recordList.add(mr);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return recordList;
    }

    @Override
    public void deleteRecord(MusicRecord mr){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDS, KEY_ID + " = ?",
                new String[] { String.valueOf(mr.getNumber()) });
        db.close();
    }

}
