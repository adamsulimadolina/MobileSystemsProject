package pl.edu.pb.wi.projekt;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditWorkoutActivity  extends AppCompatActivity {
    public static final String EXTRA_EDIT_NAME = "pb.edu.pl.EDIT_NAME";
    public static final String EXTRA_EDIT_REPS = "pb.edu.pl.EDIT_REPS";
    public static final String EXTRA_EDIT_SERIES = "pb.edu.pl.EDIT_SERIES";

    private EditText name_edit;
    private EditText reps_edit;
    private EditText series_edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_edit_workout);

        name_edit = findViewById(R.id.edit_name);
        reps_edit = findViewById(R.id.edit_reps);
        series_edit = findViewById(R.id.edit_series);

        if (getIntent().hasExtra(EXTRA_EDIT_NAME)
                && getIntent().hasExtra(EXTRA_EDIT_REPS) && getIntent().hasExtra(EXTRA_EDIT_SERIES)) {
            name_edit.setText(getIntent().getSerializableExtra(EXTRA_EDIT_NAME).toString());
            reps_edit.setText(getIntent().getSerializableExtra(EXTRA_EDIT_REPS).toString());
            series_edit.setText(getIntent().getSerializableExtra(EXTRA_EDIT_SERIES).toString());
        }

        final Button button = findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(name_edit.getText())
                        || TextUtils.isEmpty(reps_edit.getText())
                        || TextUtils.isEmpty(series_edit.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String name = name_edit.getText().toString();
                    replyIntent.putExtra(EXTRA_EDIT_NAME, name);
                    String reps = reps_edit.getText().toString();
                    replyIntent.putExtra(EXTRA_EDIT_REPS, reps);
                    String series = series_edit.getText().toString();
                    replyIntent.putExtra(EXTRA_EDIT_SERIES, series);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}
