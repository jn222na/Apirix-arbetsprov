package database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jocke on 2016-12-05.
 */

public class SendPushMessage extends DatabaseConnection {

    public SendPushMessage(int action, String token, String title, String message) {
        super(action, token, title, message);
    }

    public JSONObject sendPushMessage() {

        try {
            response.put("action", this.action);
            response.put("token", this.token);
            response.put("time", getTime());
            response.put("title", this.title);
            response.put("message", this.messageBody);
            String message = response.toString();
            baseFetch(message);
            response = getMessage();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
