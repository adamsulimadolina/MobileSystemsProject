package pl.edu.pb.wi.projekt;

import android.app.Application;
import android.media.tv.TvContract;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WorkoutRepository {
    private WorkoutDao workoutDao;
    private LiveData<List<Workout>> workouts;

    WorkoutRepository(Application application) {
        DailyStepsDatabase database = DailyStepsDatabase.getDatabase(application);
        workoutDao = database.workoutDao();
        workouts = workoutDao.findAll();
    }

    LiveData<List<Workout>> findAllWorkouts() {
        return workouts;
    }

    void insert(final Workout workout) {
        DailyStepsDatabase.databaseWriteExecutor.execute(() -> {
            workoutDao.insert(workout);
        });
    }

    void update(Workout workout) {
        DailyStepsDatabase.databaseWriteExecutor.execute(() -> {
            workoutDao.update(workout);
        });
    }

    void delete(Workout workout) {
        DailyStepsDatabase.databaseWriteExecutor.execute(() -> {
            workoutDao.delete(workout);
        });
    }
}
