package pl.edu.pb.wi.projekt;


import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static androidx.room.Room.*;

@Database(entities = {DailySteps.class, Workout.class}, version=2, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class DailyStepsDatabase extends RoomDatabase {
    public abstract DailyStepsDao dailyStepsDao();
    public abstract WorkoutDao workoutDao();

    public static volatile DailyStepsDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static DailyStepsDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (DailyStepsDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DailyStepsDatabase.class, "app_db")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                DailyStepsDao dao = INSTANCE.dailyStepsDao();
                WorkoutDao workoutDao = INSTANCE.workoutDao();
                final LocalDate start = LocalDate.parse("2020/01/01", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                final LocalDate end = LocalDate.parse("2020/02/20", DateTimeFormatter.ofPattern("yyyy/MM/dd"));

                final int days = (int) start.until(end, ChronoUnit.DAYS);

                List<LocalDate> list =  Stream.iterate(start, d -> d.plusDays(1))
                        .limit(days)
                        .collect(Collectors.toList());
                List<DailySteps> tmp = new ArrayList<>();
                for(int i=0; i<list.size(); i++) {
                    tmp = dao.findStepsWithDate(list.get(i));
                    if(tmp.size() == 0) {

                        DailySteps dailySteps = new DailySteps();
                        dailySteps.setDate(list.get(i));
                        dailySteps.setValue(0);
                        dao.insert(dailySteps);
                    }
                }
                workoutDao.deleteAll();
                Workout w = new Workout();
                for(int i=0; i<5; i++) {
                    w = new Workout();
                    w.setDate(LocalDate.now());
                    w.setName("TEST");
                    w.setReps(3);
                    w.setSeries(3);
                    workoutDao.insert(w);
                }


            });
        }
    };

}
