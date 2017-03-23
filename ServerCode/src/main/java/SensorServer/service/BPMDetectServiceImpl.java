package SensorServer.service;
import SensorServer.utility.SoundTouch;
import be.tarsos.transcoder.DefaultAttributes;
import be.tarsos.transcoder.Transcoder;
import be.tarsos.transcoder.ffmpeg.EncoderException;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by jicl on 16/9/10.
 */
@Component
public class BPMDetectServiceImpl implements BPMDetectService{
    public int getMyBPM(String inputFile){
        String outputFile=inputFile.substring(0,inputFile.length()-3)+"wav";
        try {
            Transcoder.transcode(inputFile,outputFile, DefaultAttributes.WAV_PCM_S16LE_STEREO_44KHZ);
        } catch (EncoderException e) {
            e.printStackTrace();
        }
        SoundTouch st=new SoundTouch();
        int result=(int)st.getMyBpm(outputFile);
        File mp3=new File(inputFile);
        mp3.delete();
        File wav=new File(outputFile);
        wav.delete();
        if (result>=180)
            result/=2;
        return result;
    }
}
