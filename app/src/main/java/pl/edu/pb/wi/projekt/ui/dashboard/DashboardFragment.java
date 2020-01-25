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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.PrimaryKey;

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

    private RecyclerView recyclerView;
    final DailyStepsAdapter adapter = new DailyStepsAdapter();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        running = true;
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        recyclerView.setAdapter(adapter);

        dailyStepsViewModel = ViewModelProviders.of(this).get(DailyStepsViewModel.class);
        dailyStepsViewModel.findAll().observe(this, new Observer<List<DailySteps>>() {
            @Override
            public void onChanged(List<DailySteps> dailySteps) {
               // dailyStepsList = dailySteps;
                adapter.setDailySteps(dailySteps);
            }
        });

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
        LocalDate date = LocalDate.now();
        //Log.d("MainActivity", "DATA " + date.toString());
        //Log.d("DLUGOSC", Integer.toString(dailyStepsList.size()));
        for (int i = 0; i < dailyStepsList.size(); i++) {
            Log.d("MainActivity", "LISTA " + i + dailyStepsList.get(i).getDate().toString());
            if (dailyStepsList.get(i).getDate().isEqual(date)) {
                //Log.d("NOWY DZIEN", "ZNALAZLO");
                newDay = false;

            }
        }

        if (newDay) {
            //Log.d("NOWY DZIEN", Boolean.toString(newDay));
            DailySteps dailySteps = new DailySteps();
            dailySteps.setDate(date);
            dailySteps.setValue(0);
            dailyStepsViewModel.insert(dailySteps);
            dailyStepsList.add(dailySteps);
            adapter.setDailySteps(dailyStepsList);
            newDay = false;
        }
        if (running) {
            for (int i = 0; i < dailyStepsList.size(); i++) {
                //Log.d("XD", dailyStepsList.dailyStepsList.get(i).getDate().toString());
                if (dailyStepsList.get(i).getDate().isEqual(date)) {
                    dailyStepsList.get(i).setValue(dailyStepsList.get(i).getValue() + 1);
                    dailyStepsViewModel.update(dailyStepsList.get(i));
                    adapter.setDailySteps(dailyStepsList);
                    Log.d("ZNALAZLO", Integer.toString(dailyStepsList.get(i).getId()));
                    Log.d("ZNALAZLO", dailyStepsList.get(i).getDate().toString());
                    Log.d("ZNALAZLO", Integer.toString(dailyStepsList.get(i).getValue()));

                }
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class DailyStepsHolder extends RecyclerView.ViewHolder {


        private TextView dateTextView;
        private TextView stepsTextView;
        private DailySteps dailySteps;

        public DailyStepsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.daily_steps_item, parent, false));

            dateTextView = itemView.findViewById(R.id.date);
            stepsTextView = itemView.findViewById(R.id.steps);
        }

        public void bind(DailySteps dailySteps) {
            this.dailySteps = dailySteps;
            dateTextView.setText(dailySteps.getDate().toString());
            stepsTextView.setText(Integer.toString(dailySteps.getValue()));
        }
    }

    private class DailyStepsAdapter extends RecyclerView.Adapter<DailyStepsHolder> {

        @NonNull
        @Override
        public DailyStepsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DailyStepsHolder(getLayoutInflater(), parent);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull DailyStepsHolder holder, int position) {
            if(dailyStepsList != null) {
                DailySteps dailyStep = dailyStepsList.get(position);
                if(dailyStep.getDate().isEqual(LocalDate.now())) holder.bind(dailyStep);
            } else {

            }
        }

        @Override
        public int getItemCount() {
            if(dailyStepsList != null) {
                return dailyStepsList.size();
            } else {
                return 0;
            }
        }

        public void setDailySteps(List<DailySteps> dailySteps) {
            dailyStepsList = dailySteps;
            notifyDataSetChanged();
        }
    }

}