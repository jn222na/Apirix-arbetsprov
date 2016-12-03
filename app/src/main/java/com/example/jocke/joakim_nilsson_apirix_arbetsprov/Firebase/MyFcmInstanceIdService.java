package com.example.jocke.joakim_nilsson_apirix_arbetsprov.Firebase;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jocke.joakim_nilsson_apirix_arbetsprov.MainActivity;
import com.example.jocke.joakim_nilsson_apirix_arbetsprov.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Jocke on 2016-11-29.
 */

public class MyFcmInstanceIdService extends FirebaseInstanceIdService {

    MainActivity mainActivity = new MainActivity();
    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        mainActivity.setAndRegisterToken(refreshedToken);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.FCM_TOKEN), refreshedToken);
        editor.apply();


    }


}
