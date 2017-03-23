package team.jfh.sensorfm.presentation.activity;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dogtwofly on 2016/9/6.
 */
public class DownloadTask extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {

        String response = requestResponse( (String)params[0] );
        String location = getLocation(response);
        String result = downloadFromUrlToPath( location, (String)params[1], (String) params[2]);
        return result;
    }

    private String getLocation(String response) {
        String location = null;
        try{
            JSONObject jsonObject = new JSONObject(response);
            location = jsonObject.get("location").toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            return location;
        }
    }

    @Nullable
    private String downloadFromUrlToPath(String location, String path, String fileName) {
        try{
            URL url = new URL(location);
            File file = new File(path, fileName);
            file.createNewFile();
            FileUtils.copyURLToFile(url, file);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    private String requestResponse(String url) {
        StringBuffer response = new StringBuffer();
        try{
            URL infoUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) infoUrl.openConnection();
            con.connect();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            return response.toString();
        }
    }

}
