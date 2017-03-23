package team.jfh.sensorfm.presentation.activity;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team.jfh.sensorfm.data.entity.InputForRecommendation;
import team.jfh.sensorfm.data.entity.MusicRecord;
import team.jfh.sensorfm.data.entity.TrackInfo;
import team.jfh.sensorfm.data.repository.MusicRecordRepository;
import team.jfh.sensorfm.data.repository.MusicRecordRepositorySQLite;
import team.jfh.sensorfm.data.repository.TrackInfoRepository;
import team.jfh.sensorfm.data.repository.TrackInfoRepositorySQLite;
import team.jfh.sensorfm.domain.interactor.MicrosoftHeartRateManager;
import team.jfh.sensorfm.domain.interactor.RecommendationController;
import team.jfh.sensorfm.presentation.entity.ApplicationState;

/**
 * Created by dogtwofly on 2016/8/25.
 * Greatly modified by odinaryk on 2016/9/9
 */


public class BoundService extends Service {

    @Nullable
    private static String LOG_TAG = "BoundService";
    public boolean isPlaying = false; //used for display & control
    private boolean locationRequested = false;
    private IBinder mBinder = new MyBinder();
    private ArrayList<String> songs = new ArrayList<>();
    private TrackInfo playing = null; //used for display
    private int number = 0;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MicrosoftHeartRateManager manager;
    private ApplicationState state;
    private TrackInfo nextTrack;
    private Boolean isRecommending;

    public Boolean getRecommending() {
        return isRecommending;
    }

    public void setRecommending(Boolean recommending) {
        isRecommending = recommending;
    }

    public static ArrayList<String> updateSongList(File root) {
        ArrayList<String> res = new ArrayList<>();
        File[] localFiles = root.listFiles();
        for (File elmt : localFiles) {
            if (elmt.isDirectory() && !elmt.isHidden()) {
                res.addAll(updateSongList(elmt));
            } else {
                if (isMusic(elmt)) {
                    res.add(elmt.toString());
                }
            }
        }
        return res;
    }

    private static boolean isMusic(File elmt) {
        if (elmt.getName().endsWith(".mp3"))
            if (elmt.length() / 1024 / 1024 >= 1)
                return true;
        return false;
    }

    public MicrosoftHeartRateManager getManager() {
        return manager;
    }

    public void resetManager(MicrosoftHeartRateManager amanager) {
        if (manager != null)
            manager.disconnect();
        this.manager = amanager;
    }

    public ApplicationState getState() {
        return state;
    }

    public void setState(ApplicationState state) {
        this.state = state;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
        songs = updateSongList(Environment.getExternalStorageDirectory());
        state = new ApplicationState();
        state.setMode("normal");
        state.setBand(false);
        state.setOnline(false);
        isRecommending=false;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //saveRecord();
                recommendNextTrack();
            }
        });
        try {
            navigatePlaying(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }

    public TrackInfo getPlaying() {
        return playing;
    }

    public void navigatePlaying(int pos) throws IOException {
        String location = songs.get(pos);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(location));
            mediaPlayer.prepare();
            if (isPlaying)
                mediaPlayer.start();
            playing = new TrackInfoRepositorySQLite(getApplicationContext()).findTrackByLocation(location);
            if (playing == null)
                playing = new TrackInfo(location, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            mediaPlayer.stop();
            isPlaying = false;
        }
    }

    public ArrayList<String> getSongs() {
        return songs;
    }

    public void resume() {
        mediaPlayer.start();
        isPlaying = true;
    }

    private void saveRecord() {
        number++;

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        dateFormat.format(date);
        int pulse = -1;
        if (manager != null)
            pulse = manager.getCurrentHeartRate();

        Location location = getLastKnownLocation();
        int longitude = 0;
        int latitude = 0;
        if (location != null) {
            longitude = (int) location.getLongitude();
            latitude = (int) location.getLatitude();
        }

        String songAddress = playing.getLocation();
        TrackInfoRepository trackInfoRepository = new TrackInfoRepositorySQLite(getApplicationContext());
        TrackInfo trackInfo = trackInfoRepository.findTrackByLocation(songAddress);
        int bpm = trackInfo.getBpm();

        MusicRecord musicRecord = new MusicRecord(number, date, pulse, longitude, latitude, bpm, songAddress);
        MusicRecordRepository musicRecordRepository = new MusicRecordRepositorySQLite(getApplicationContext());
        musicRecordRepository.addRecord(musicRecord);

        return;
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (locationRequested == false) {
                    toast("Location request unpermitted, please turn on GPS and permit location access");
                    locationRequested = true;
                }
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public void pause() {
        mediaPlayer.pause();
        isPlaying = false;
    }

    public void next() {
        mediaPlayer.pause();
        recommendNextTrack();
    }

    public void prev() {
        mediaPlayer.seekTo(0);
    }

    private void recommendNextTrack() {
        new RecommendNextTrackTask().execute();
    }

    private void toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class RecommendNextTrackTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(nextTrack.getLocation()));
                mediaPlayer.prepare();
                if (isPlaying)
                    mediaPlayer.start();
                playing = nextTrack;
                isRecommending=false;
            } catch (Exception e) {
                e.printStackTrace();
                mediaPlayer.stop();
                isPlaying = false;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            isRecommending=true;
            InputForRecommendation inputForRecommendation = new InputForRecommendation();
            if (state.getBand()) {
                inputForRecommendation.setPedPerMin((int) manager.getPedPerMin().doubleValue());
                inputForRecommendation.setHeartRate(manager.getCurrentHeartRate());
            }
            inputForRecommendation.setMode(state.getMode());
            nextTrack = RecommendationController.recommend(inputForRecommendation, state.getOnline());
            return null;
        }
    }

    public class MyBinder extends Binder {
        BoundService getService() {
            return BoundService.this;
        }
    }

}

