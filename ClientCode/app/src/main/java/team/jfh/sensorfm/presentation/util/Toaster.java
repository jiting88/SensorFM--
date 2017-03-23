package team.jfh.sensorfm.presentation.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by rootK on 2016/9/11.
 */
public class Toaster {
    public static void makeToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
    public static void makeAsyncToast(final Activity activity,final String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
