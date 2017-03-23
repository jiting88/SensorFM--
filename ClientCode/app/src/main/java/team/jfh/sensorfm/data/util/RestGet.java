package team.jfh.sensorfm.data.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rootK on 2016/9/9.
 */
public class RestGet {
    private final OkHttpClient client= new OkHttpClient();
    private static final String server="http://10.0.2.2:8080";
    private final Gson gson = new Gson();
    public <T>T getEntity(String api,Type typeOfResult) throws IOException {
        Request request=new Request.Builder().url(server+api).get().build();
        Response response=client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return gson.fromJson(response.body().charStream(),typeOfResult);
    }
}
