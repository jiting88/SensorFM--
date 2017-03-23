package team.jfh.sensorfm.presentation.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.jfh.sensorfm.R;
import team.jfh.sensorfm.domain.interactor.RegisterInteractor;
import team.jfh.sensorfm.presentation.util.Toaster;

public class SignUpActivity extends AppCompatActivity {
    Navigator navigator;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.RegPwd)
    EditText regPwd;
    @BindView(R.id.RegPwdAgain)
    EditText regPwdAgain;
    @BindView(R.id.RegMail)
    EditText email;

    BoundService mBoundService;
    boolean mServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        navigator=new Navigator();
    }

    public static Intent getCallingIntent(Context context){
        return new Intent(context,SignUpActivity.class);
    }

    @OnClick(R.id.btnBack)
    public void navigateToLogin(){
        //navigator.toLogin(this);
        if (this!= null) {
            Intent intentToLaunch = LoginActivity.getCallingIntent(this);
            this.startActivity(intentToLaunch);
        }
    }

    @OnClick(R.id.btnRegister)
    public void register(){
        String semail=email.getText().toString();
        String susername=username.getText().toString();
        String spwd=regPwd.getText().toString();
        String spwdag=regPwdAgain.getText().toString();
        if (email.getText().toString().contains("@")){
            if (spwd.equals(spwdag)){
                new RegisterTask(semail,susername,spwd).execute();
            }
            else
                Toaster.makeToast(getApplicationContext(),"Inconsistent password!");
        }
        else{
            Toaster.makeToast(getApplicationContext(),"Invalid email address!");
        }

    }

    private class RegisterTask extends AsyncTask<Void,Void,String>{
        String email;
        String username;
        String password;

        public RegisterTask(String email, String username, String password) {
            this.email = email;
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            RegisterInteractor registerInteractor=new RegisterInteractor();
            return registerInteractor.register(username,password,email);
        }

        @Override
        protected void onPostExecute(String error) {
            if (error!=null)
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
            else{
                mBoundService.getState().setOnline(true);
                Toaster.makeToast(getApplicationContext(),"Register Successful");
                // TODO Set Extra Login Status.
            }
        }
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


    public EditText getUsername() {
        return username;
    }

    public void setUsername(EditText username) {
        this.username = username;
    }

    public EditText getRegPwd() {
        return regPwd;
    }

    public void setRegPwd(EditText regPwd) {
        this.regPwd = regPwd;
    }

    public EditText getRegPwdAgain() {
        return regPwdAgain;
    }

    public void setRegPwdAgain(EditText regPwdAgain) {
        this.regPwdAgain = regPwdAgain;
    }

    public EditText getEmail() {
        return email;
    }

    public void setEmail(EditText email) {
        this.email = email;
    }
}
