package pl.edu.pb.wi.project;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Date;


@Entity(tableName = "daily_steps")
public class DailySteps {



    @PrimaryKey(autoGenerate = true)
    private int id;
    private int value;
    private LocalDate date;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }



    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
