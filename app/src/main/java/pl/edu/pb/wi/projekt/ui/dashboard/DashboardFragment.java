package pl.edu.pb.wi.projekt.ui.dashboard;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import pl.edu.pb.wi.projekt.DailySteps;
import pl.edu.pb.wi.projekt.DailyStepsViewModel;
import pl.edu.pb.wi.projekt.R;
import pl.edu.pb.wi.projekt.ui.dashboard.DashboardViewModel;

public class DashboardFragment extends Fragment implements SensorEventListener {

    private DashboardViewModel dashboardViewModel;
    private SensorManager sensorManager;
    private TextView textView;
    private boolean running = false;

    //private DailyStepsList dailyStepsList;
    private List<DailySteps> dailyStepsList = new ArrayList<DailySteps>();
    private DailyStepsViewModel dailyStepsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        running = true;
        dailyStepsViewModel = ViewModelProviders.of(this).get(DailyStepsViewModel.class);
        dailyStepsViewModel.findAll().observe(this, new Observer<List<DailySteps>>() {
            @Override
            public void onChanged(List<DailySteps> dailySteps) {
                dailyStepsList = dailySteps;
            }
        });


        //dailyStepsList = dailyStepsViewModel.findAll().getValue();

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(getActivity(), "Sensor not found!", Toast.LENGTH_SHORT).show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("TRZY", "JEDEN");
        boolean newDay = true;
        int idx = -1;
        LocalDate date = LocalDate.now();
        Log.d("MainActivity", "DATA " + date.toString());
        Log.d("DLUGOSC", Integer.toString(dailyStepsList.size()));
        for (int i = 0; i < dailyStepsList.size(); i++) {
            Log.d("MainActivity", "LISTA " + i + dailyStepsList.get(i).getDate().toString());
            if (dailyStepsList.get(i).getDate().isEqual(date)) {
                Log.d("NOWY DZIEN", "ZNALAZLO");
                newDay = false;

            } else newDay = true;
        }

        if (newDay) {
            Log.d("NOWY DZIEN", Boolean.toString(newDay));
            DailySteps dailySteps = new DailySteps();
            dailySteps.setDate(date);
            dailySteps.setValue(0);
            dailyStepsViewModel.insert(dailySteps);
            dailyStepsList.add(dailySteps);
            newDay = false;
        }
        if (running) {
            for (int i = 0; i < dailyStepsList.size(); i++) {
                //Log.d("XD", dailyStepsList.dailyStepsList.get(i).getDate().toString());
                if (dailyStepsList.get(i).getDate().isEqual(date)) {
                    dailyStepsList.get(i).setValue(dailyStepsList.get(i).getValue() + 1);
                    dailyStepsViewModel.update(dailyStepsList.get(i));
                    idx = i;
                    Log.d("ZNALAZLO", Integer.toString(idx));

                }
            }
            if (idx != -1) {
                Log.d("SETTEXT", String.valueOf(dailyStepsList.get(idx).getValue()));
                textView.setText(String.valueOf(dailyStepsList.get(idx).getValue()));
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}