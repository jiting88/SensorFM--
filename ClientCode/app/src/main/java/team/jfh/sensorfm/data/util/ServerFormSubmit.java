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
 * Created by rootK on 2016/7/19.
 */
public class ServerFormSubmit {
    private final OkHttpClient client= new OkHttpClient();
    private static final String server="http://192.168.1.83:8080";
    private final Gson gson = new Gson();
    public <T>T access(String api, Object form, Type typeOfResult) throws IOException {
        Type type=new TypeToken<Map<String,String>>(){}.getType();
        Map<String,String> argMap=gson.fromJson(gson.toJson(form),type);
        FormBody.Builder formBuilder=new FormBody.Builder();
        Iterator entries = argMap.entrySet().iterator();
        while (entries.hasNext()){
            Map.Entry entry=(Map.Entry)entries.next();
            formBuilder.add((String)entry.getKey(),(String)entry.getValue());
        }
        Request request=new Request.Builder().url(server+api).post(formBuilder.build()).build();
        Response response=client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return gson.fromJson(response.body().charStream(),typeOfResult);
    }
}