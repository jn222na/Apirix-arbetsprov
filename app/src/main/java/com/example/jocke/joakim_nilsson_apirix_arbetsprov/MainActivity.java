package com.example.jocke.joakim_nilsson_apirix_arbetsprov;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jocke.joakim_nilsson_apirix_arbetsprov.Firebase.DatabaseStrings;

import database.RegisterDevice;
import database.SendCoordinates;
import database.SendPushMessage;

import static android.os.Build.VERSION_CODES.M;


//TODO: REQUESTPERMISSION OVERRIDE
//TODO: SHAREDPREFERENCES TOTALDISTANCEWALKED
//TODO: START/ENDCOORDINATES

public class MainActivity extends AppCompatActivity {

    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 1;

    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private MyLocationHandler myLocationHandler = null;

    private ImageButton imgInfoBtn = null;
    private Button controlBtn = null;
    private Button sendNotification = null;
    private Button sendPushNotification = null;

    private String token = null;
    private String title = null;
    private String message = null;
    private int distanceToWalk = 0;
    private SpinnerDistance spinnerDistance = null;
    private Spinner spinner = null;
    private int scanInterval = 100;
    private int scanAfterDistanceWalked = 0;

    private EditText editTextTitle;
    private EditText editTextMessage;

    private TextView walkedView = null;
    private double[] startCoordinates = null;
    private double[] endCoordinates = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        controlBtn = (Button) findViewById(R.id.controlBtn);
        imgInfoBtn = (ImageButton) findViewById(R.id.imgInfoBtn);
        spinner = (Spinner) findViewById(R.id.spinnerSelectedDistance);

        sendNotification = (Button) findViewById(R.id.registerPhone);
        sendPushNotification = (Button) findViewById(R.id.pushNotificationSender);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        walkedView = (TextView) findViewById(R.id.distanceWalked);
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        spinnerDistance = new SpinnerDistance(this, scanAfterDistanceWalked);
        myLocationHandler = new MyLocationHandler(this, locationListener);

        myLocationHandler.setupLocationListener();
        askForPermissions();
        distanceWalkedPrefs();
    }


    private void distanceWalkedPrefs() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        walkedView.setText(sharedPreferences.getString("walkedDistance", null));
    }

    //Save the FCM generated token to db
    public void setAndRegisterToken(String token) {
        if(token != null) {
            this.token = token;
        } else {
            initializePreferences();
        }
        RegisterDevice sendInformationToDb = new RegisterDevice(DatabaseStrings.REGISTER.getValue(), token);
        sendInformationToDb.registerDevice();
        sendInformationToDb.execute();
    }

    public void sendCoordinatesToDb() {
        SendCoordinates sendInformationToDb = new SendCoordinates(DatabaseStrings.SENDCOORDINATES.getValue(), token, startCoordinates[0], startCoordinates[1], endCoordinates[0], endCoordinates[1]);
        sendInformationToDb.sendCoordinates();
        sendInformationToDb.execute();
    }

    public void sendNotification(View view) {
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();
        SendPushMessage pushMessage = new SendPushMessage(DatabaseStrings.PUSHMESSAGE.getValue(), token, title, message);
        pushMessage.sendPushMessage();
        pushMessage.execute();
    }

    @TargetApi(M)
    private boolean askForPermissions() {
        try {
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST);
        } catch (Exception e) {
            Log.d("askForPermissions", "ERROR" + e);
            return false;
        }

        return true;
    }

    //Gets the initial coordinates
    public void startCoordinates(double[] coordinates) {
        this.startCoordinates = coordinates;
    }

    public void endCoordinates(double[] coordinates) {
        this.endCoordinates = coordinates;
    }

    //Start monitoring user coordinates
    public void startMeasuring(View view) {
        controlBtn.setText(R.string.btnDeactivate);
        controlBtn.setEnabled(false);
        spinner.setEnabled(false);
        sendPushNotification.setEnabled(false);
        myLocationHandler.requestListener(scanInterval, distanceToWalk);
    }

    //Quit monitoring user and send start and end coordinates to DB
    public void stopMeasuring() {
        controlBtn.setText(R.string.btnActivate);
        controlBtn.setEnabled(true);
        spinner.setEnabled(true);
        sendPushNotification.setEnabled(true);
        myLocationHandler.removeListener();
    }

    //Fetch already generated token
    public void initializePreferences() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "TOKEN ERROR");
        if(token.equals("TOKEN ERROR")) {
            makeToast("Error - Token null");
        }
    }

    //RegisterBtn
//    public void registerPhone(View view) {
//        stringRequest();
//        makeToast("Device registered");
//    }


    //Info window
    public void displayPopupWindow(View view) {
        PopupWindow popup = new PopupWindow(MainActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.popup_content, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(view);
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("walkedDistance", walkedView.getText().toString());
        editor.apply();
        super.onBackPressed();
    }

    private void makeToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    public void setDistanceToWalk(int distanceToWalk) {
        this.distanceToWalk = distanceToWalk;
    }


    //    private void registerToken() {
//        RegisterDevice dbConnection = new RegisterDevice(handleDBURL, "3", token);
//        JSONObject RegisterDevice = null;
//        try {
//            getInformationFromDb = dbConnection.execute().get();
//            if(getInformationFromDb != null) {
//                makeToast("Your device have already been registered");
//                removeRegisterBtn();
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//    }

    //If the device token has already been generated remove that button
    private void removeRegisterBtn() {
//        View bView = sendNotification;
//        bView.setVisibility(View.GONE);
//        bView = imgInfoBtn;
//        bView.setVisibility(View.GONE);
    }
}