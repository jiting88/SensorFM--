package team.jfh.sensorfm.domain.interactor;

/**
 * Created by jicl on 16/9/9.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import team.jfh.sensorfm.data.entity.TrackInfo;
import team.jfh.sensorfm.data.repository.TrackInfoRepository;
import team.jfh.sensorfm.data.repository.TrackInfoRepositorySQLite;

public class BPMDectectAndSaver {
    FFmpeg ffmpeg = null;
    Context context;
    List<TrackInfo> analysisList;
    TrackInfoRepository trackInfoRepository;

    public BPMDectectAndSaver(Context context) {
        //Modified 9-11 to adapt interface in BoundService
        ffmpeg = FFmpeg.getInstance(context);
        analysisList=new ArrayList<>();
        this.context = context;
        loadFFMpegBinary();
    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                }
            });
            ffmpeg.setTimeout(10000);
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        trackInfoRepository = new TrackInfoRepositorySQLite(context);
        for (TrackInfo trackInfo:analysisList){
            Log.i("SoundTouch","Trying to analysis "+trackInfo.getLocation());
            detectAndSaveBpm(trackInfo);
        }
    }

    public void addToWaitList(TrackInfo trackInfo){
        analysisList.add(trackInfo);
    }

    public void detectAndSaveBpm(TrackInfo trackInfo) {
        String outputFile = trackInfo.getLocation().substring(0, trackInfo.getLocation().length() - 3) + "wav";
        String[] command = {"-i", trackInfo.getLocation(), outputFile};
        Boolean succ=false;
        while (!succ) {
            try {
                ffmpeg.execute(command, new ffmpegSoundTouch());
                succ=true;
            } catch (FFmpegCommandAlreadyRunningException e) {
                Log.e("ffmpeg","Already running");
                ffmpeg.killRunningProcesses();
                succ=false;
            }
        }
        while (ffmpeg.isFFmpegCommandRunning()){
            try {
                Thread.sleep(500);
            }catch (InterruptedException e){

            }
        }
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){}
        SoundTouch st = new SoundTouch();
        int bpm = (int) st.getMyBpm(outputFile);
        if (bpm>=180) bpm/=2;
        File toDelete = new File(outputFile);
        toDelete.delete();
        trackInfo.setBpm(bpm);
        trackInfoRepository.addTrackInfo(trackInfo);
        Log.i("SoundTouch","Scanned "+outputFile);
    }

    private class ffmpegSoundTouch extends ExecuteBinaryResponseHandler {
        @Override
        public void onFinish() {
            super.onFinish();

        }
    }
}
