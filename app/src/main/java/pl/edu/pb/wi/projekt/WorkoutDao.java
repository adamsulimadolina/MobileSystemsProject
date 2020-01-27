package pl.edu.pb.wi.projekt;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Workout workout);

    @Update
    public void update(Workout workout);

    @Delete
    public void delete(Workout workout);

    @Query("DELETE FROM workout")
    public void deleteAll();

    @Query("SELECT * FROM workout ORDER BY date")
    public LiveData<List<Workout>> findAll();

    @Query("SELECT * FROM workout WHERE date LIKE :date")
    public List<Workout> findStepsWithDate(LocalDate date);
}
