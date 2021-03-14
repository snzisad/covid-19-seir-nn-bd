package com.zisad.covid19simulationbd.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.zisad.covid19simulationbd.R;
import com.zisad.covid19simulationbd.activity.MainActivity;
import com.zisad.covid19simulationbd.model.ConfirmedCases;
import com.zisad.covid19simulationbd.utils.AppVariables;


public class PeakValueGraphFragment extends Fragment {
    TextView tv_peak_value, tv_peak_date, tv_r0_value;
    GraphView gv_peak_value;
    LineGraphSeries<DataPoint> series;
    DataPoint[] dataPoints;
    ConfirmedCases confirmedCases = AppVariables.confirmedCases;

    int[] confirmed_cases = confirmedCases.getConfirmed_data();
    int total_days = confirmed_cases.length;
    int peak_day = confirmedCases.getPeak_day(),
            peak_value = confirmedCases.getPeak();
    String start_Date = confirmedCases.getBetaList()[0].getDate();
    String r0_value =confirmedCases.getR0value();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_peak_value_graph, container, false);

        initOthers(view);
        setDataPoints();
//        setData();

        return view;
    }

    private void initOthers(View view){
        tv_peak_value = view.findViewById(R.id.tv_peak_value);
        tv_peak_date = view.findViewById(R.id.tv_peak_date);
        tv_r0_value = view.findViewById(R.id.tv_r0_value);
        gv_peak_value = view.findViewById(R.id.gv_peak_value);

        tv_r0_value.setText(r0_value);
        tv_peak_value.setText(""+peak_value);
        tv_peak_date.setText(MainActivity.get_next_date(start_Date, peak_day)+" ("+peak_day+" days)");
    }

    private void setDataPoints(){
        series = new LineGraphSeries<>();
//        total_days = 200;
        for (int i = 0; i<total_days; i++){
            series.appendData(new DataPoint(i, confirmed_cases[i]), false, total_days, true);
        }

        series.setTitle("Confirmed cases curve");
        series.setColor(getContext().getResources().getColor(R.color.colorPrimary));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(5);
        series.setThickness(5);

        gv_peak_value.addSeries(series);

        gv_peak_value.getViewport().setXAxisBoundsManual(true);
        gv_peak_value.getViewport().setMinX(peak_day-100);
        gv_peak_value.getViewport().setMaxX(peak_day+100);

//        gv_peak_value.getViewport().setScalable(true);  // activate horizontal zooming and scrolling
        gv_peak_value.getViewport().setScrollable(true);  // activate horizontal scrolling
//        gv_peak_value.getViewport().setScalableY(false);  // activate horizontal and vertical zooming and scrolling
//        gv_peak_value.getViewport().setScrollableY(false);  // activate vertical scrolling


        // Graph properties
        gv_peak_value.getGridLabelRenderer().setHumanRounding(false);
        gv_peak_value.getGridLabelRenderer().setHorizontalAxisTitle("Days");
//        gv_peak_value.getGridLabelRenderer().setVerticalAxisTitle("Total cases");
//        gv_peak_value.getGridLabelRenderer().setNumHorizontalLabels(13);
//        series.setSpacing(20);
//        gv_peak_value.getViewport().setScrollable(true);

        // draw line on the peak value
        double ref = Math.pow(10, (""+peak_value).length()-2);

        dataPoints = new DataPoint[2];
        dataPoints[0] = new DataPoint(peak_day, 0);
        dataPoints[1] = new DataPoint(peak_day, peak_value+ref);

        series = new LineGraphSeries<>(dataPoints);
        series.setColor(Color.MAGENTA);
        gv_peak_value.addSeries(series);

        gv_peak_value.getViewport().setYAxisBoundsManual(true);
        gv_peak_value.getViewport().setMinY(0);
        gv_peak_value.getViewport().setMaxY(peak_value+ref);
    }
}
