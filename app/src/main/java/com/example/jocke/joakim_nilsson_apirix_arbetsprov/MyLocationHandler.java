package com.example.jocke.joakim_nilsson_apirix_arbetsprov;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Jocke on 2016-12-01.
 */

class MyLocationHandler {
    
    private LocationManager locationManager = null;
    private Vibrator vibrator = null;
    private TextView walkedView = null;
    private Button controlBtn = null;
    private LocationListener locationListener = null;
    private int totalDistanceWalked = 0;
    private MainActivity mainActivity = null;
    private ImageButton imgInfoBtn = null;
    private int i = 0;
    private int distanceToWalk;


    MyLocationHandler(MainActivity mainActivity, LocationListener locationListener) {
        this.locationListener = locationListener;
        this.mainActivity = mainActivity;
        walkedView = (TextView) mainActivity.findViewById(R.id.distanceWalked);
        controlBtn = (Button) mainActivity.findViewById(R.id.controlBtn);
        imgInfoBtn = (ImageButton) mainActivity.findViewById(R.id.imgInfoBtn);
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    private double[] coordinates = new double[2];

    void setupLocationListener() {
        try {
            // Define a listener that responds to location updates
            locationListener = new LocationListener() {

                public void onLocationChanged(Location location) {

                    coordinates[0] = location.getLatitude();
                    coordinates[1] = location.getLongitude();
                    mainActivity.startCoordinates(coordinates);

                    if(i == 1) {
                        // Called when a new location is found by the network location provider.
                        vibrator = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(500);

                        totalDistanceWalked += distanceToWalk;
                        //Push notify user
                        alertUser();
                        walkedView.setText(Integer.toString(totalDistanceWalked));
                        mainActivity.endCoordinates(coordinates);
                        mainActivity.sendCoordinatesToDb();
                        mainActivity.stopMeasuring();
                        i = 0;
                        return;
                    }
                    i++;
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }

            };

        } catch (Exception e) {
            Log.d("SetupLocationListener", "ERROR", e);
        }
    }


    private void alertUser() {
        //Push notification
        Intent intent = new Intent(mainActivity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mainActivity, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mainActivity)
                        .setSmallIcon(R.drawable.apirix_logo)
                        .setColor(Color.GREEN)
                        .setLights(Color.BLUE, 3000, 3000)
                        .setContentTitle(distanceToWalk + "m Walked")
                        .setContentIntent(pendingIntent)
                        .setContentText("Total Distance Walked " + totalDistanceWalked + "m");
        NotificationManager notificationManager = (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }

    void requestListener(int scanInterval, int distanceToWalk) {
        if(ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.distanceToWalk = distanceToWalk;

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, scanInterval, distanceToWalk, locationListener);
    }

    void removeListener() {
        if(ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        locationManager.removeUpdates(locationListener);
    }
}
