package pl.edu.pb.wi.projekt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.vo.DateData;

public class WorkoutActivity extends AppCompatActivity {

    private static final String TAG = "WorkoutActivity";
    public static final int NEW_WORKOUT_REQUEST_CODE = 1;
    public static final int EDIT_WORKOUT_REQUEST_CODE = 2;
    private TextView textView;
    private FloatingActionButton add_button;
    private Button back_button;
    private Workout edit_workout = null;

    private RecyclerView recyclerView;
    final WorkoutAdapter adapter = new WorkoutAdapter();
    private WorkoutViewModel workoutViewModel;

    private LocalDate date_selected;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        add_button = findViewById(R.id.add_workout_button);
//        back_button = findViewById(R.id.back_button);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_workouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        workoutViewModel = ViewModelProviders.of(this).get(WorkoutViewModel.class);
        workoutViewModel.findAll().observe(this, new Observer<List<Workout>>() {
            @Override
            public void onChanged(List<Workout> workouts) {
                adapter.setWorkouts(workouts);
            }
        });

        Intent intent = getIntent();
        date_selected = (LocalDate) intent.getSerializableExtra("date");
        //textView.setText(date_selected.toString());

        String label = getResources().getString(R.string.w_label);
        setTitle(label + " " + date_selected.getDayOfMonth() + "." + date_selected.getMonthValue() + '.' + date_selected.getYear());

        add_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutActivity.this, EditWorkoutActivity.class);
                startActivityForResult(intent, NEW_WORKOUT_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WORKOUT_REQUEST_CODE && resultCode == RESULT_OK) {
            Workout workout = new Workout();
            workout.setName(data.getStringExtra(EditWorkoutActivity.EXTRA_EDIT_NAME));
            workout.setReps(Integer.parseInt(data.getStringExtra(EditWorkoutActivity.EXTRA_EDIT_REPS)));
            workout.setSeries(Integer.parseInt(data.getStringExtra(EditWorkoutActivity.EXTRA_EDIT_SERIES)));
            workout.setDate(date_selected);
            workoutViewModel.insert(workout);
            //Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.book_added),
                   // Snackbar.LENGTH_LONG).show();
        } else if (requestCode == EDIT_WORKOUT_REQUEST_CODE && resultCode == RESULT_OK) {
            edit_workout.setName(data.getStringExtra(EditWorkoutActivity.EXTRA_EDIT_NAME));
            edit_workout.setReps(Integer.parseInt(data.getStringExtra(EditWorkoutActivity.EXTRA_EDIT_REPS)));
            edit_workout.setSeries(Integer.parseInt(data.getStringExtra(EditWorkoutActivity.EXTRA_EDIT_SERIES)));
            workoutViewModel.update(edit_workout);
        }

    }

    private class WorkoutHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{


        private TextView workout_name;
        private TextView workout_reps;
        private TextView workout_series;
        private Workout workout;

        public WorkoutHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.workout_item, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            workout_name = itemView.findViewById(R.id.workout_name);
            workout_reps = itemView.findViewById(R.id.workout_reps);
            workout_series = itemView.findViewById(R.id.workout_series);
        }

        public void bind(Workout workout) {
            Log.d(TAG, workout.getDate().toString() + workout.getName());
            this.workout = workout;
            workout_name.setText(workout.getName());
            workout_reps.setText(Integer.toString(workout.getReps()));
            workout_series.setText(Integer.toString(workout.getSeries()));
        }

        @Override
        public void onClick(View v) {
            edit_workout = workout;
            Intent intent = new Intent(WorkoutActivity.this, EditWorkoutActivity.class);
            intent.putExtra(EditWorkoutActivity.EXTRA_EDIT_NAME, workout.getName());
            intent.putExtra(EditWorkoutActivity.EXTRA_EDIT_REPS, workout.getReps());
            intent.putExtra(EditWorkoutActivity.EXTRA_EDIT_SERIES, workout.getSeries());
            Log.d("XD","XD");
            startActivityForResult(intent, EDIT_WORKOUT_REQUEST_CODE);
        }

        @Override
        public boolean onLongClick(View v) {

            workoutViewModel.delete(workout);
            return true;
        }
    }

    private class WorkoutAdapter extends RecyclerView.Adapter<WorkoutHolder> {
        List<Workout> workouts;

        @NonNull
        @Override
        public WorkoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WorkoutHolder(getLayoutInflater(), parent);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull WorkoutHolder holder, int position) {
            if(workouts != null) {
                Workout workout = workouts.get(position);
                holder.bind(workout);
            } else {
                Log.d(TAG, "No workouts");
            }
        }

        @Override
        public int getItemCount() {
            if(workouts != null) {
                return workouts.size();
            } else {
                return 0;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void setWorkouts(List<Workout> workouts) {
            List<Workout> today_workouts = new ArrayList<>();
            for(int i=0; i<workouts.size(); i++) {
                if(workouts.get(i).getDate().isEqual(date_selected)) {
                    today_workouts.add(workouts.get(i));
                }
            }
            this.workouts = today_workouts;
            Log.d(TAG, "XD");
            notifyDataSetChanged();
        }
    }
}

