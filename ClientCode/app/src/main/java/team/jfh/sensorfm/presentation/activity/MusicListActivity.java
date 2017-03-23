package team.jfh.sensorfm.presentation.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import team.jfh.sensorfm.R;
import team.jfh.sensorfm.data.entity.TrackInfo;

public class MusicListActivity extends AppCompatActivity{

    Navigator navigator;
    BoundService mBoundService;
    boolean mServiceBound = false;

    public static Intent getCallingIntent(Context context){
        return new Intent(context,MusicListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);
        ButterKnife.bind(this);
        navigator=new Navigator();
    }

    private void showSongs() throws IOException {
        ArrayList<String> songs = mBoundService.getSongs();
        ArrayList<Map<String,String>> names = new ArrayList<Map<String,String>>();
        for (String song : songs){
            File f = new File(song);
            MusicMetadataSet info = new MyID3().read(f);
            if (info != null){
                try{
                    IMusicMetadata metadata = info.getSimplified();
                    String title = metadata.getSongTitle();
                    String artist = metadata.getArtist();
                    Map<String,String> m = new HashMap<>(2);
                    m.put("title", title);
                    m.put("artist", artist);
                    names.add(m);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        ListView listView = (ListView) findViewById(R.id.playlist);


        SimpleAdapter adapter = new SimpleAdapter(this, names,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "artist"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        listView.setAdapter(adapter);
    }


    private void afterBind(){
        try {
            showSongs();
            showPlayInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnItemClick(R.id.playlist)
    void play(int pos) {
        try {
            mBoundService.navigatePlaying(pos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        showPlayInfo();
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

        TextView textView1 = (TextView) findViewById(R.id.titleBar);
        TextView textView3 = (TextView) findViewById(R.id.authorBar);
        TrackInfo curr = mBoundService.getPlaying();
        if(curr != null){
            String title = curr.getTitle();
            String artist = curr.getSinger();
            textView1.setText(title);
            textView3.setText(artist);
        }
    }


    @OnClick(R.id.bottomBar)
    public void navigateToMain(){
        navigator.toMain(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BoundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

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
            afterBind();
        }

    };



}
