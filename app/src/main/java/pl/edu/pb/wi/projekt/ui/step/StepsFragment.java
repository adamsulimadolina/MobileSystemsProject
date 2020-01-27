package pl.edu.pb.wi.projekt.ui.step;

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


public class StepsFragment extends Fragment implements SensorEventListener {

    private StepsViewModel stepsViewModel;
    private SensorManager sensorManager;
    private TextView textView;
    private boolean running = false;

    private List<DailySteps> dailyStepsList = new ArrayList<DailySteps>();
    private DailyStepsViewModel dailyStepsViewModel;

    private RecyclerView recyclerView;
    final DailyStepsAdapter adapter = new DailyStepsAdapter();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        stepsViewModel =
                ViewModelProviders.of(this).get(StepsViewModel.class);
        View root = inflater.inflate(R.layout.steps_fragment, container, false);
        running = true;
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        dailyStepsViewModel = ViewModelProviders.of(this).get(DailyStepsViewModel.class);
        dailyStepsViewModel.findAll().observe(this, new Observer<List<DailySteps>>() {
            @Override
            public void onChanged(List<DailySteps> dailySteps) {
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
        adapter.notifyDataSetChanged();
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

        boolean newDay = true;
        LocalDate date = LocalDate.now();
        for (int i = 0; i < dailyStepsList.size(); i++) {
            if (dailyStepsList.get(i).getDate().isEqual(date)) {
                newDay = false;
            }
        }

        if (newDay) {
            DailySteps dailySteps = new DailySteps();
            dailySteps.setDate(date);
            dailySteps.setValue(0);
            dailyStepsViewModel.insert(dailySteps);
            dailyStepsList.add(dailySteps);

        }
        adapter.setDailySteps(dailyStepsList);
        if (running) {
            for (int i = 0; i < dailyStepsList.size(); i++) {
                if (dailyStepsList.get(i).getDate().isEqual(date)) {
                    dailyStepsList.get(i).setValue(dailyStepsList.get(i).getValue() + 1);
                    dailyStepsViewModel.update(dailyStepsList.get(i));
                    adapter.setDailySteps(dailyStepsList);
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
        private TextView monthstepsTextView;
        private DailySteps dailySteps;

        public DailyStepsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.daily_steps_item, parent, false));

            dateTextView = itemView.findViewById(R.id.date);
            stepsTextView = itemView.findViewById(R.id.steps);
            monthstepsTextView = itemView.findViewById(R.id.monthly_steps);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(DailySteps dailySteps) {
            this.dailySteps = dailySteps;
            int monthly_steps = 0;
            LocalDate tmp_date = dailySteps.getDate();
            dateTextView.setText(tmp_date.getDayOfMonth() + "." + tmp_date.getMonthValue() + '.' + tmp_date.getYear());
            stepsTextView.setText(Integer.toString(dailySteps.getValue()));
            for(int i=0; i<dailyStepsList.size(); i++) {
                if(tmp_date.getMonthValue() == dailyStepsList.get(i).getDate().getMonthValue()) {
                    monthly_steps+=dailyStepsList.get(i).getValue();
                }
            }
            monthstepsTextView.setText(Integer.toString(monthly_steps));
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

                for(int i = 0; i<dailyStepsList.size(); i++) {
                    if(dailyStepsList.get(i).getDate().isEqual(LocalDate.now())) {
                        holder.bind(dailyStepsList.get(i));
                    }
                }

            }
        }

        @Override
        public int getItemCount() {
            if(dailyStepsList != null) {
                return 1;
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