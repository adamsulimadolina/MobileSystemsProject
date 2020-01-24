package pl.edu.pb.wi.projekt;

import android.database.DatabaseUtils;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Dao
public interface DailyStepsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailySteps dailySteps);

    @Update
    public void update(DailySteps dailySteps);

    @Delete
    public void delete(DailySteps dailySteps);

    @Query("DELETE FROM daily_steps")
    public void deleteAll();

    @Query("SELECT * FROM daily_steps ORDER BY date")
    public LiveData<List<DailySteps>> findAll();

    @Query("SELECT * FROM daily_steps WHERE date LIKE :date")
    public List<DailySteps> findStepsWithDate(LocalDate date);
}
