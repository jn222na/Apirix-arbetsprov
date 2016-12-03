package com.example.jocke.joakim_nilsson_apirix_arbetsprov;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jocke on 2016-12-01.
 */

class SpinnerDistance {

    private int distanceToWalk = 0;
    private MainActivity mainactivity = null;

    SpinnerDistance(MainActivity mainActivity, int distanceToWalk) {
        this.mainactivity  = mainActivity;
        this.distanceToWalk = distanceToWalk;
        setupSpinner();

    }

    private void setupSpinner() {
        Spinner spinnerSelectDistance = (Spinner) mainactivity.findViewById(R.id.spinnerSelectedDistance);
        final String[] resArray = mainactivity.getResources().getStringArray(R.array.distanceToWalk);
        final ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(resArray));
        try {
            final ArrayAdapter dataAdapter = new ArrayAdapter<String>(mainactivity, android.R.layout.simple_spinner_dropdown_item, resArray) {

                @Override
                public View getDropDownView(int position, View v, ViewGroup parent) {

                    if(v == null) {
                        Context mContext = this.getContext();
                        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        v = vi.inflate(R.layout.spinnerrow, null);
                    }

                    TextView tv = (TextView) v.findViewById(R.id.spinnerTarget);
                    tv.setText(stringList.get(position));

                    return v;
                }
            };
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSelectDistance.setAdapter(dataAdapter);
        } catch (Exception e) {
            Log.e("SetupSpinner->Adapter", "ERROR", e);
        }

        try {
            spinnerSelectDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    switch ((int) id) {
                        case 0:
                            distanceToWalk = 25;
                            break;
                        case 1:
                            distanceToWalk = 50;
                            break;
                        case 2:
                            distanceToWalk = 75;
                            break;
                        case 3:
                            distanceToWalk = 100;
                            break;
                    }
                    mainactivity.setDistanceToWalk(distanceToWalk);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
        } catch (Exception e) {
            Log.e("SetupSpinner->Listener", "ERROR", e);
        }
    }
}
