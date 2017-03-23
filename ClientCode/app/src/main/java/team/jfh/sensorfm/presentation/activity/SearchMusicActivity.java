package team.jfh.sensorfm.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import team.jfh.sensorfm.R;

public class SearchMusicActivity extends AppCompatActivity {
    Navigator navigator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchmusic);
        ButterKnife.bind(this);
        navigator=new Navigator();
    }

    public static Intent getCallingIntent(Context context){
        return new Intent(context,SearchMusicActivity.class);
    }
}
