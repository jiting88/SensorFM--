package team.jfh.sensorfm.presentation.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.jfh.sensorfm.R;
import team.jfh.sensorfm.data.entity.TrackInfo;
import team.jfh.sensorfm.data.repository.TrackInfoRepository;
import team.jfh.sensorfm.data.repository.TrackInfoRepositorySQLite;
import team.jfh.sensorfm.domain.interactor.BPMDectectAndSaver;
import team.jfh.sensorfm.domain.interactor.MicrosoftHeartRateManager;
import team.jfh.sensorfm.presentation.entity.ApplicationState;
import team.jfh.sensorfm.presentation.util.Toaster;

public class MainActivity extends AppCompatActivity {
    Navigator navigator;
    BoundService mBoundService;
    boolean mServiceBound = false;

    @BindView(R.id.btnBand)
    AppCompatImageButton btnBand;

    @BindView(R.id.btnRun)
    AppCompatImageButton btnRun;

    @BindView(R.id.btnSleep)
    AppCompatImageButton btnSleep;

    @BindView(R.id.btnUser)
    AppCompatImageButton btnUser;

    @BindView(R.id.beats)
    TextView beats;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BoundService.MyBinder myBinder = (BoundService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
            showPlayInfo();
        }
    };

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public AppCompatImageButton getBtnUser() {
        return btnUser;
    }

    public void setBtnUser(AppCompatImageButton btnUser) {
        this.btnUser = btnUser;
    }

    public AppCompatImageButton getBtnBand() {
        return btnBand;
    }

    public void setBtnBand(AppCompatImageButton btnBand) {
        this.btnBand = btnBand;
    }

    public AppCompatImageButton getBtnRun() {
        return btnRun;
    }

    public void setBtnRun(AppCompatImageButton btnRun) {
        this.btnRun = btnRun;
    }

    public AppCompatImageButton getBtnSleep() {
        return btnSleep;
    }

    public void setBtnSleep(AppCompatImageButton btnSleep) {
        this.btnSleep = btnSleep;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        navigator = new Navigator();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu2_local_music:
                navigator.toMusicList(this);
                break;
            case R.id.menu4_analysis:
                scanAllTrack(mBoundService.getSongs());
                break;
            case R.id.menu6_app_exit:
                onStop();
                android.os.Process.killProcess(Process.myPid());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scanAllTrack(ArrayList<String> songs) {
        TrackInfoRepository trackInfoRepository = new TrackInfoRepositorySQLite(getApplicationContext());
        BPMDectectAndSaver bpmDectectAndSaver = new BPMDectectAndSaver(getApplicationContext());
        for (String song : songs) {
            TrackInfo trackInfo = trackInfoRepository.findTrackByLocation(song);
            if (trackInfo == null) {
                try {
                    int bpm = 0;
                    MusicMetadataSet musicMetadataSet = new MyID3().read(new File(song));
                    IMusicMetadata iMusicMetadata = musicMetadataSet.getSimplified();
                    String title = iMusicMetadata.getSongTitle();
                    String artist = iMusicMetadata.getArtist();
                    String bpmInFile = iMusicMetadata.getCompilation();
                    if (bpmInFile != null) {
                        bpm = Integer.parseInt(bpmInFile);
                        TrackInfo toAdd = new TrackInfo(song, title,artist, bpm);
                        trackInfoRepository.addTrackInfo(toAdd);
                    } else {
                        TrackInfo toAnalysize = new TrackInfo(song, title,artist, bpm);
                        bpmDectectAndSaver.addToWaitList(toAnalysize);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        bpmDectectAndSaver.start();
    }

    @OnClick(R.id.btnPlay)
    void resume() {
        if (!mBoundService.isPlaying) {
            mBoundService.resume();
            showPlayInfo();
        } else {
            mBoundService.pause();
            showPlayInfo();
        }
    }

    @OnClick(R.id.btnNext)
    void next() {
        mBoundService.next();
        new NextTrackTask(mBoundService).execute();
    }

    private class NextTrackTask extends AsyncTask<Void,Void,Void>{
        BoundService boundService;

        public NextTrackTask(BoundService boundService) {
            this.boundService = boundService;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (boundService.getRecommending()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            showPlayInfo();
            super.onPostExecute(aVoid);
        }
    }

    @OnClick(R.id.btnBack)
    void prev() {
        mBoundService.prev();
        showPlayInfo();
    }

    @OnClick(R.id.btnUser)
    void loginOrShow() {
        ApplicationState state = mBoundService.getState();
        if (!state.getOnline()) {
            btnUser.setBackgroundResource(R.drawable.online);
            state.setOnline(true);
            Toaster.makeToast(getApplicationContext(), "Online Recommendation Activated!");
        } else {
            btnUser.setBackgroundResource(R.drawable.offline);
            state.setOnline(false);
            Toaster.makeToast(getApplicationContext(), "Offline Recommendation Activated!");
        }
    }

    @OnClick(R.id.btnBand)
    void connectBand() {
        if (mBoundService.getManager() == null)
            mBoundService.resetManager(new MicrosoftHeartRateManager(this, mBoundService.getState()));
        MicrosoftHeartRateManager manager = mBoundService.getManager();
        if (manager.getStatus()) {
            btnBand.setBackgroundResource(R.drawable.band_click);
            mBoundService.resetManager(null);
            beats.setText("HeartRate Detection Off");
        } else {
            manager.connect();
            //btnBand.setBackgroundResource(R.drawable.band);
        }
    }

    @OnClick(R.id.btnRun)
    void switchRunMode() {
        ApplicationState state = mBoundService.getState();
        if (state.getMode().equals("sport")) {
            state.setMode("normal");
            btnRun.setBackgroundResource(R.drawable.run_click);
            Toaster.makeToast(getApplicationContext(), "Switch to mode: " + state.getMode());
        } else {
            if (state.getMode().equals("sleep"))
                btnSleep.setBackgroundResource(R.drawable.sleep_click);
            state.setMode("sport");
            btnRun.setBackgroundResource(R.drawable.run);
            Toaster.makeToast(getApplicationContext(), "Switch to mode: " + state.getMode());
        }

    }

    @OnClick(R.id.btnSleep)
    void switchSleepMode() {
        ApplicationState state = mBoundService.getState();
        if (state.getMode().equals("sleep")) {
            state.setMode("normal");
            btnSleep.setBackgroundResource(R.drawable.sleep_click);
            Toaster.makeToast(getApplicationContext(), "Switch to mode: " + state.getMode());

        } else {
            if (state.getMode().equals("sport"))
                btnRun.setBackgroundResource(R.drawable.run_click);
            state.setMode("sleep");
            btnSleep.setBackgroundResource(R.drawable.sleep);
            Toaster.makeToast(getApplicationContext(), "Switch to mode: " + state.getMode());
        }
    }

    public void showPlayInfo() {
        if (mBoundService.isPlaying) {
            ImageButton imageButton = (ImageButton) findViewById(R.id.btnPlay);
            imageButton.setImageResource(R.drawable.off);
            //ImageButton imageButton1 = (ImageButton) findViewById(R.id.playBar);
            //imageButton1.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
        } else {
            ImageButton imageButton = (ImageButton) findViewById(R.id.btnPlay);
            imageButton.setImageResource(R.drawable.on);
            //ImageButton imageButton1 = (ImageButton) findViewById(R.id.playBar);
            //imageButton1.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
        }

        TextView titleBar = (TextView) findViewById(R.id.titleBar);
        TextView authorBar = (TextView) findViewById(R.id.authorBar);
        TrackInfo curr = mBoundService.getPlaying();
        if (isLocal(curr)){
            String location = curr.getLocation();
            File f = new File(location);
            try {
                MusicMetadataSet myID3 = new MyID3().read(f);
                IMusicMetadata info = myID3.getSimplified();
                String title = info.getSongTitle();
                String artist = info.getArtist();
                titleBar.setText(title);
                authorBar.setText(artist);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            String title = curr.getTitle();
            String artist = curr.getSinger();
            titleBar.setText(title);
            authorBar.setText(artist);
        }
    }

    private boolean isLocal(TrackInfo curr) {
        String location = curr.getLocation();
        if(location.startsWith("/storage"))
            return true;
        else
            return false;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BoundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

//    ------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

//    ------------------------------------------------------------------------------

}
