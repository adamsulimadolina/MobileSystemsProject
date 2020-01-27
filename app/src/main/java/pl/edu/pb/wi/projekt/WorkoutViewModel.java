package pl.edu.pb.wi.projekt;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class WorkoutViewModel extends AndroidViewModel {
    private WorkoutRepository workoutRepository;
    private LiveData<List<Workout>> workouts;

    public WorkoutViewModel(@NonNull Application application) {
        super(application);
        workoutRepository = new WorkoutRepository(application);
        workouts = workoutRepository.findAllWorkouts();
    }

    public LiveData<List<Workout>> findAll() {
        return workouts;
    }

    public void insert(Workout workout) {
        workoutRepository.insert(workout);
    }

    public void update(Workout workout) {
        workoutRepository.update(workout);
    }

    public void delete(Workout workout) {
        workoutRepository.delete(workout);
    }
}
