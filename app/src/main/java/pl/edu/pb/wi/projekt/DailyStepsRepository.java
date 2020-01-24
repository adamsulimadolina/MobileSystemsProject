package pl.edu.pb.wi.projekt;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class DailyStepsRepository {
    private DailyStepsDao dailyStepsDao;
    private LiveData<List<DailySteps>> dailysteps;

    DailyStepsRepository(Application application) {
        DailyStepsDatabase database = DailyStepsDatabase.getDatabase(application);
        dailyStepsDao = database.dailyStepsDao();
        dailysteps = dailyStepsDao.findAll();
    }

    LiveData<List<DailySteps>> findAllDailySteps() {
        return dailysteps;
    }

    void insert(final DailySteps dailySteps) {
        DailyStepsDatabase.databaseWriteExecutor.execute(() -> {
            dailyStepsDao.insert(dailySteps);
        });
    }

    void update(DailySteps dailySteps) {
        DailyStepsDatabase.databaseWriteExecutor.execute(() -> {
            dailyStepsDao.update(dailySteps);
        });
    }

    void delete(DailySteps dailySteps) {
        DailyStepsDatabase.databaseWriteExecutor.execute(() -> {
            dailyStepsDao.delete(dailySteps);
        });
    }
}
