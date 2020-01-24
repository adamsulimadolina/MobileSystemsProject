package pl.edu.pb.wi.project;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class DailyStepsViewModel extends AndroidViewModel {
    private DailyStepsRepository dailyStepsRepository;
    private LiveData<List<DailySteps>> dailySteps;

    public DailyStepsViewModel(@NonNull Application application) {
        super(application);
        dailyStepsRepository = new DailyStepsRepository(application);
        dailySteps = dailyStepsRepository.findAllDailySteps();
    }

    public LiveData<List<DailySteps>> findAll() {
        return dailySteps;
    }

    public void insert(DailySteps dailySteps) {
        dailyStepsRepository.insert(dailySteps);
    }

    public void update(DailySteps dailySteps) {
        dailyStepsRepository.update(dailySteps);
    }

    public void delete(DailySteps dailySteps) {
        dailyStepsRepository.delete(dailySteps);
    }
}
