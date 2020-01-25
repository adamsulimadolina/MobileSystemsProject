package pl.edu.pb.wi.projekt;


import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.room.Room.*;

@Database(entities = {DailySteps.class}, version=1, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class DailyStepsDatabase extends RoomDatabase {
    public abstract DailyStepsDao dailyStepsDao();

    public static volatile DailyStepsDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static DailyStepsDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (DailyStepsDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DailyStepsDatabase.class, "dailysteps_db")
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
            });
        }
    };

}
