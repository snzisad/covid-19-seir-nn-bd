package com.zisad.covid19simulationbd.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zisad.covid19simulationbd.R;
import com.zisad.covid19simulationbd.activity.MainActivity;
import com.zisad.covid19simulationbd.adapter.ConfirmedCaseAdapter;
import com.zisad.covid19simulationbd.model.ConfirmedCases;
import com.zisad.covid19simulationbd.utils.AppVariables;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PredictionFragment extends Fragment {

    RecyclerView rv_prediction;
    ArrayList date, cases;
    ConfirmedCaseAdapter confirmedCaseAdapter;
    TextView tv_date, tv_seekbar_position;
    SeekBar seekBar_day;


    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    DatePickerDialog datePickerDialog;
    String prediction_date;


    ConfirmedCases confirmedCases = AppVariables.confirmedCases;
    int[] confirmed_cases = confirmedCases.getConfirmed_data();
    String start_date;
    int prediction_days = 7, start_day;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prediction, container, false);

        initOthers(view);
        setPredictionStartDate();

        return view;
    }

    private void initOthers(View view){
        start_date = confirmedCases.getBetaList()[0].getDate();
        rv_prediction = view.findViewById(R.id.rv_prediction);
        tv_date = view.findViewById(R.id.tv_date);
        tv_seekbar_position = view.findViewById(R.id.tv_seekbar_position);
        seekBar_day = view.findViewById(R.id.seekBar_day);
        date = new ArrayList();
        cases = new ArrayList();

        confirmedCaseAdapter = new ConfirmedCaseAdapter(getContext(), date, cases);
        rv_prediction.setAdapter(confirmedCaseAdapter);
        rv_prediction.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_prediction.setNestedScrollingEnabled(false);

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatePicker();
            }
        });
        seekBar_day.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_seekbar_position.setText("For: "+progress+" days");
                prediction_days = progress;
                setData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setData(){
        date.clear();
        cases.clear();

        for(int i = start_day; i < start_day+prediction_days; i++){
            date.add(MainActivity.get_next_date(start_date, i));
            cases.add(confirmed_cases[i]);
        }

        confirmedCaseAdapter.notifyDataSetChanged();
    }


    private void setDatePicker(){

        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                year = y;
                month = m;
                day = d;
                setPredictionStartDate();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void setPredictionStartDate(){
        prediction_date =  day+"/"+(month+1)+"/"+year;
        tv_date.setText(prediction_date);

        start_day = MainActivity.get_day_between_dates(prediction_date, start_date);
        setData();
    }

}
