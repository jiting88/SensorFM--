package team.jfh.sensorfm.presentation.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.jfh.sensorfm.R;
import team.jfh.sensorfm.data.entity.UserInfo;
import team.jfh.sensorfm.domain.interactor.LoginInteractor;
import team.jfh.sensorfm.presentation.util.Toaster;

public class LoginActivity extends AppCompatActivity {
    Navigator navigator;
    @BindView(R.id.username) TextView username;
    @BindView(R.id.password) EditText password;

    BoundService mBoundService;
    boolean mServiceBound = false;

    public static Intent getCallingIntent(Context context){
        return new Intent(context,LoginActivity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Transition back = TransitionInflater.from(this).inflateTransition(R.transition.slide);
//        getWindow().setEnterTransition(back);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        navigator=new Navigator();
    }

    @OnClick(R.id.btnLogin)
    public void login(){
        new LoginTask(username.getText().toString(),password.getText().toString()).execute();
    }

    @OnClick(R.id.btnSignUp)
    public void navigateToRegister(){
        navigator.toSignUp(this);
    }

    @OnClick(R.id.btnBack)
    public void navigateToMain(){
        navigator.toMain(this);
    }

    public class LoginTask extends AsyncTask<Void,Void,String>{
        private final String mUsername;
        private final String mPassword;
        private LoginInteractor loginInteractor=new LoginInteractor();

        public LoginTask(String mUsername, String mPassword) {
            this.mUsername = mUsername;
            this.mPassword = mPassword;
        }

        @Override
        protected String doInBackground(Void... params) {
            return loginInteractor.check(mUsername,mPassword);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String error) {
            if (error!=null)
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
            else{
                mBoundService.getState().setOnline(true);
                Toaster.makeToast(getApplicationContext(),"Login Successful");
                // TODO Set Extra Login Status.
            }
        }
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
        }

    };
}
