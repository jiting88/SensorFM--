package team.jfh.sensorfm.domain.interactor;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatImageButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandPedometerEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;

import team.jfh.sensorfm.R;
import team.jfh.sensorfm.presentation.entity.ApplicationState;

/**
 * Created by rootK on 2016/9/2.
 * Life in UI Thread
 */
public class MicrosoftHeartRateManager implements HeartRateManager {
    private Activity activity = null;
    private BandClient client= null;
    private Integer heartRate = null;

    public Double getPedPerMin() {
        return pedPerMin;
    }

    private Double pedPerMin=null;
    private ApplicationState state;

    public MicrosoftHeartRateManager(Activity activity, ApplicationState state) {
        this.activity = activity;
        this.state = state;
    }

    public Boolean getStatus() {
        return state.getBand();
    }

    private BandHeartRateEventListener heartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(BandHeartRateEvent event) {
            if (event != null){
                heartRate=event.getHeartRate();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView view=(TextView) activity.findViewById(R.id.beats);
                        if (view!=null)
                            view.setText(String.format("HR per min:%d",heartRate));
                    }
                });
            }
        }
    };

    private BandPedometerEventListener pedometerEventListener = new BandPedometerEventListener() {
        @Override
        public void onBandPedometerChanged(BandPedometerEvent event) {
            if (event != null){
                pedPerMin=event.getTotalSteps()/60.0;
            }
        }
    };


    @Override
    public void connect() {
        new ConnectTask().execute();
    }

    @Override
    public int getCurrentHeartRate() {
        return heartRate;
    }

    @Override
    public void disconnect() {
        new HeartRateEndTask().execute();
    }

    private class ConnectTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        client.getSensorManager().registerHeartRateEventListener(heartRateEventListener);
                        client.getSensorManager().registerPedometerEventListener(pedometerEventListener);
                        state.setBand(true);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageButton band=(ImageButton)activity.findViewById(R.id.btnBand);
                                band.setBackgroundResource(R.drawable.band);
                            }
                        });
                        appendToUI("Band connected!");

                    } else {
                        getUserConsent();
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageButton band=(ImageButton)activity.findViewById(R.id.btnBand);
                            band.setBackgroundResource(R.drawable.band_click);
                        }
                    });
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageButton band=(ImageButton)activity.findViewById(R.id.btnBand);
                        band.setBackgroundResource(R.drawable.band_click);
                    }
                });
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }

        private boolean getConnectedBandClient() throws InterruptedException, BandException {

            if (client == null) {
                //Find paired bands
                BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
                if (devices.length == 0) {
                    return false;
                }
                client = BandClientManager.getInstance().create(activity, devices[0]);
            } else if(ConnectionState.CONNECTED == client.getConnectionState()) {
                return true;
            }
            return ConnectionState.CONNECTED == client.connect().await();
        }

        private void getUserConsent(){
            try {
                if (getConnectedBandClient()) {
                    if (activity != null) {
                        client.getSensorManager().requestHeartRateConsent(activity, new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                            }
                        });
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageButton band=(ImageButton)activity.findViewById(R.id.btnBand);
                            band.setBackgroundResource(R.drawable.band_click);
                        }
                    });
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageButton band=(ImageButton)activity.findViewById(R.id.btnBand);
                        band.setBackgroundResource(R.drawable.band_click);
                    }
                });
                appendToUI(exceptionMessage);
            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
        }
    }

    private class HeartRateEndTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            if (client != null) {
                try {
                    client.getSensorManager().unregisterHeartRateEventListener(heartRateEventListener);
                    client.getSensorManager().unregisterPedometerEventListener(pedometerEventListener);
                    client.disconnect().await();
                    state.setBand(false);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageButton band=(ImageButton)activity.findViewById(R.id.btnBand);
                            band.setBackgroundResource(R.drawable.band_click);
                        }
                    });
                    appendToUI("Band disconnected!");
                } catch (InterruptedException e) {
                    appendToUI(e.getMessage());
                    // Do nothing as this is happening during destroy
                } catch (BandException e) {
                    appendToUI(e.getMessage());
                    // Do nothing as this is happening during destroy
                }
            }
            client=null;
            return null;
        }
    }

    private void appendToUI(final String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,message,Toast.LENGTH_SHORT).show();

            }
        });
    }
}
