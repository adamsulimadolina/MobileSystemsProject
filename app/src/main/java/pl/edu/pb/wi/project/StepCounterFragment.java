package pl.edu.pb.wi.project;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepCounterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepCounterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepCounterFragment extends Fragment implements SensorEventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DailyStepsViewModel dailyStepsViewModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SensorManager sensorManager;
    private TextView text;
    private boolean running = false;
    //private DailyStepsList dailyStepsList;
    private List<DailySteps> dailyStepsList = new ArrayList<DailySteps>();

    public StepCounterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepCounterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StepCounterFragment newInstance(String param1, String param2) {
        StepCounterFragment fragment = new StepCounterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.d("JEDEN", "JEDEN");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("DWA", "JEDEN");
        // Inflate the layout for this fragment
        text = getActivity().findViewById(R.id.text);
        dailyStepsViewModel = ViewModelProviders.of(this).get(DailyStepsViewModel.class);
        dailyStepsViewModel.findAll().observe(this, new Observer<List<DailySteps>>() {
            @Override
            public void onChanged(List<DailySteps> dailySteps) {
                dailyStepsList = dailySteps;
            }

        });
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        return inflater.inflate(R.layout.fragment_step_counter, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(getActivity(),"Sensor not found!",Toast.LENGTH_SHORT).show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("TRZY", "JEDEN");
        boolean newDay = true;
        int idx = -1;
        LocalDate date = LocalDate.now();
        Log.d("MainActivity", "DATA " + date.toString());
        for (int i = 0; i < dailyStepsList.size(); i++) {
            Log.d("MainActivity", "LISTA " + dailyStepsList.get(0).getDate().toString());
            if (dailyStepsList.get(i).getDate().isEqual(date)) {
                newDay = false;

            }
        }
        Log.d("XD", Boolean.toString(newDay));
//        if (newDay) {
//            DailySteps dailySteps = new DailySteps();
//            dailySteps.setDate(date);
//            dailySteps.setValue(0);
//            dailyStepsList.add(dailySteps);
//        }
        if (running) {
            for (int i = 0; i < dailyStepsList.size(); i++) {
                //Log.d("XD", dailyStepsList.dailyStepsList.get(i).getDate().toString());
                if (dailyStepsList.get(i).getDate().isEqual(date)) {
                    dailyStepsList.get(i).setValue(dailyStepsList.get(i).getValue() + 1);
                    dailyStepsViewModel.update(dailyStepsList.get(i));
                    idx = i;


                }
            }
            if (idx != -1) {
                text.setText(String.valueOf(dailyStepsList.get(idx).getValue()));
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
