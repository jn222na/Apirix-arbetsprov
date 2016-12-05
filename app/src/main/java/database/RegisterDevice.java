package database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jocke on 2016-12-05.
 */

public class RegisterDevice extends DatabaseConnection {

    public RegisterDevice(int action, String token) {
        super(action, token);

    }

    public JSONObject registerDevice() {
        try {
            response.put("action", this.action);
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
