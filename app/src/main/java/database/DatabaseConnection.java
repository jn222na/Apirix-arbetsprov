package database;

import android.os.AsyncTask;
import android.util.Log;

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

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Jocke on 2016-12-05.
 */

public abstract class DatabaseConnection extends AsyncTask<Object, Object, JSONObject> {


    String messageBody;
    String title;
    private String URL = "http://jockepocke.se/Android_Apirix_arbetsprov/HandleDb.php";
    int action;
    String token;
    double startLatitude;
    double startLongitude;
    double endLatitude;
    double endLongitude;
    JSONObject response = new JSONObject();
    private String message;
    private JSONObject jsonMessage;

    DatabaseConnection( int action, String token) {
        this.action = action;
        this.token = token;
    }

    DatabaseConnection( int action, String token, String title, String message) {
        this.action = action;
        this.token = token;
        this.title = title;
        this.messageBody = message;
    }

    DatabaseConnection(int action, String token, double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        this.action = action;
        this.startLongitude = startLatitude;
        this.startLatitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.token = token;
    }

    void baseFetch(String message) {
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected JSONObject doInBackground(Object... params) {

        Log.d(TAG, "onPostExecute: " + message);
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

    @Override
    protected void onPostExecute(JSONObject message) {
        super.onPostExecute(message);

        this.jsonMessage = message;
    }

    JSONObject getMessage() {
        return jsonMessage;
    }


    String getTime() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        long millisec = gregorianCalendar.getTime().getTime();
        long seconds = (millisec / 1000) % 60;
        long minutes = ((millisec / (1000 * 60)) % 60);
        long hours = ((millisec / (1000 * 60 * 60)) % 24 + 1);
        return String.valueOf(hours) + ":" + minutes + ":" + seconds;
    }

}
