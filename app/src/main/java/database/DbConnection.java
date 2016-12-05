package database;

import android.os.AsyncTask;

import com.example.jocke.joakim_nilsson_apirix_arbetsprov.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.GregorianCalendar;

/**
 * Created by Jocke on 2016-11-28.
 */

public class DbConnection extends AsyncTask<Object, Object, JSONObject> {


    //Ctor parameters
    private  String action;
    private  String URL;

    private String token = null;
    private double latitude = 0;
    private double longitude = 0;
    private double endLatitude= 0;
    private double endLongitude= 0;

    private JSONObject response = new JSONObject();

    public DbConnection(String URL, String action,String token) {
        this.URL = URL;
        this.action = action;
        this.token = token;
    }
    public DbConnection(String URL, String action, String token, double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        this.URL = URL;
        this.action = action;
        this.latitude = startLatitude;
        this.longitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.token = token;
    }


    @Override
    protected JSONObject doInBackground(Object... params) {
        JSONObject retrievedInformation = null;
        switch (action){
            case "1":
                retrievedInformation = sendPushMessage();
                break;
            //In use
            case "2":
                retrievedInformation = sendCoordinates();
                break;
            case "3":
                retrievedInformation = registerDevice();
                break;
            case "4":
                retrievedInformation = sendPushMessage();
                break;
            //In Use ^^^^^
            case "5":
                retrievedInformation = registerDevice();
                break;
        }

        return retrievedInformation;
    }

    @Override
    protected void onPostExecute(JSONObject message) {
        super.onPostExecute(message);
    }

    private JSONObject baseFetch(String message) {

        OutputStream os = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        JSONObject jsonObject = null;

        try {
            //make some HTTP header nicety
            URL url = new URL(this.URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(message.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //open
            conn.connect();

            //setup send
            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(message.getBytes());
            //clean up
            os.flush();

            //do something with response
            is = conn.getInputStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }

            jsonObject = new JSONObject(total.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            //clean up
            try {
                assert os != null && is != null;
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            conn.disconnect();

        }

        return jsonObject;
    }

    private JSONObject registerDevice() {
        try {
            response.put("action", this.action);
            response.put("token", this.token);
            String message = response.toString();
            response = baseFetch(message);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    private String getTime(){
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        long millisec = gregorianCalendar.getTime().getTime();
        long seconds = (millisec / 1000) % 60;
        long minutes = ((millisec / (1000 * 60)) % 60);
        long hours = ((millisec / (1000 * 60 * 60)) % 24 + 1);
        return String.valueOf(hours) + ":" + minutes + ":" + seconds;
    }
    private JSONObject sendCoordinates() {

        String time = getTime();
        try {
            response.put("action", this.action);
            response.put("time", time);
            response.put("startLatitude", this.latitude);
            response.put("startLongitude", this.longitude);
            response.put("endLatitude", this.endLatitude);
            response.put("endLongitude", this.endLongitude);
            response.put("token", this.token);
            String message = response.toString();
            response = baseFetch(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    private JSONObject sendPushMessage() {

        String time = getTime();
        try {
            response.put("action", this.action);
            response.put("token", this.token);
            response.put("time", time);
            response.put("title", MainActivity.getTitleText());
            response.put("message", MainActivity.getMessageText());
            String message = response.toString();
            response = baseFetch(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

}