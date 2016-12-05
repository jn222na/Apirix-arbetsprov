package database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jocke on 2016-12-05.
 */

public class SendCoordinates extends DatabaseConnection {

    public SendCoordinates(int action, String token, double startLatitude, double startLongitude, double endLatitude, double endLogitude) {
        super(action, token, startLatitude, startLongitude, endLatitude, endLogitude);
    }

    public JSONObject sendCoordinates() {

        String time = getTime();
        try {
            response.put("action", this.action);
            response.put("time", time);
            response.put("startLatitude", this.startLatitude);
            response.put("startLongitude", this.startLongitude);
            response.put("endLatitude", this.endLatitude);
            response.put("endLongitude", this.endLongitude);
            response.put("token", this.token);
            String message = response.toString();
            baseFetch(message);
            response = getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

}
