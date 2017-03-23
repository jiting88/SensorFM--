package SensorServer.service;

import SensorServer.dao.TrackDao;
import SensorServer.utility.Downloader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by rootK on 2016/9/8.
 */
@Component
public class TrackAnalyzer {
    private HttpClient remote;
    private SSLContext context;
    private Downloader downloader=new Downloader();
    @Autowired
    private TrackDao trackDao;
    @Autowired
    private BPMDetectService bpmDetectService;

    public void setTrackDao(TrackDao trackDao) {
        this.trackDao = trackDao;
    }

    public void setBpmDetectService(BPMDetectService bpmDetectService) {
        this.bpmDetectService = bpmDetectService;
    }

    public Document analysis(Integer id){
        initSSL();
        remote=HttpClientBuilder.create().setSslcontext(context).build();
        String res=null;
        HttpGet httpGet=new HttpGet("http://api.lostg.com/music/xiami/songs/"+id.toString());
        try {
            HttpResponse response=remote.execute(httpGet);
            if (response.getStatusLine().getStatusCode()==200){
                res= EntityUtils.toString(response.getEntity());
            }
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
        finally {
            httpGet.releaseConnection();
        }
        // [TODO] Handle Network Exception
        Document trackInfo=Document.parse(res);

        String downUrl=trackInfo.getString("location");
        downloader.downloadFile(downUrl,id.toString()+".mp3");
        //Decode & Analysis
        Integer bpm=bpmDetectService.getMyBPM(id.toString()+".mp3");
        trackInfo.put("id",id);
        trackInfo.put("bpm",bpm);
        trackDao.addTrack(trackInfo);
//        new Thread(() -> {
//        }).start();
        return trackInfo;
    }

    private void initSSL(){
        // configure the SSLContext with a TrustManager
        try{
        context = SSLContext.getInstance("TLS");
        context.init(new KeyManager[0], new TrustManager[] {new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }}, new SecureRandom());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
