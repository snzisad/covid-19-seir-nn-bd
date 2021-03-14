package com.zisad.covid19simulationbd.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.zisad.covid19simulationbd.R;
import com.zisad.covid19simulationbd.fragment.InformationFragment;
import com.zisad.covid19simulationbd.fragment.PeakValueGraphFragment;
import com.zisad.covid19simulationbd.fragment.PredictionFragment;
import com.zisad.covid19simulationbd.model.ConfirmedCases;
import com.zisad.covid19simulationbd.model.ModelData;
import com.zisad.covid19simulationbd.utils.AppVariables;
import com.zisad.covid19simulationbd.utils.MySingleton;
import com.zisad.covid19simulationbd.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottom_navigation_home;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;

    Fragment predictionFragment, peakValueFragment, informationFragment, activeFragment;

    private int model_selection = 0;

    // date picker
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    DatePickerDialog datePickerDialog;
    String selected_date, current_date;
    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    // add intervention
    TextView tv_date, tv_date_error;
    EditText edt_population, edt_i0_value, edt_r0_value, edt_incubation_period, edt_infection_period;
    TextView tv_cancel_btn, tv_submit_btn;
    RadioGroup rg_model;
    LinearLayout ll_incubation_period;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initOthers();
        initFragment();
    }

    private void initOthers(){
        progressDialog = new ProgressDialog(this);
        bottom_navigation_home = findViewById(R.id.bottom_navigation_home);

        bottom_navigation_home.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return selectFragment(menuItem);
            }
        });
    }

    private boolean selectFragment(MenuItem item){
        Fragment selectedFragment = null;

        switch (item.getItemId()){
            case R.id.menu_prediction:
                setActionBarTitle("Prediction");
                selectedFragment = predictionFragment;
                break;

            case R.id.menu_peak_value:
                setActionBarTitle("Peak Value");
                selectedFragment = peakValueFragment;
                break;

            case R.id.menu_info:
                setActionBarTitle("Information");
                selectedFragment = informationFragment;
                break;
        }

        fragmentTransaction(selectedFragment);

        return true;
    }

    private void fragmentTransaction(Fragment fragment){
        if(activeFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(fragment).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, predictionFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, peakValueFragment).hide(peakValueFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, informationFragment).hide(informationFragment).commit();

            getSupportFragmentManager().beginTransaction().show(fragment).commit();
        }

        activeFragment = fragment;
    }

    private void initFragment(){
        activeFragment = null;
        getSupportFragmentManager().beginTransaction();
        predictionFragment = new PredictionFragment();
        peakValueFragment = new PeakValueGraphFragment();
        informationFragment = new InformationFragment();
        fragmentTransaction(predictionFragment);
        setActionBarTitle("Prediction");
        bottom_navigation_home.setSelectedItemId(R.id.menu_prediction);
    }

    public static String get_next_date(String start_date, int day){
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(start_date)); // Now use today date.
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, day); // Adding 5 days
        String output = sdf.format(c.getTime());
        return output;
    }

    public static int get_day_between_dates(String date1, String date2){
        try {
            Date date_1 = sdf.parse(date1);
            Date date_2 = sdf.parse(date2);

            long milliseconds = date_2.getTime() - date_1.getTime();
            return (int) Math.abs(milliseconds/86400000);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_intervention:
                builder = new AlertDialog.Builder(this);
                View view = LayoutInflater.from(this).inflate(R.layout.layout_add_intervention, null);
                initInterventionLayout(view);
                builder.setView(view);
                alertDialog = builder.create();
                alertDialog.show();

                break;

            case R.id.menu_custom_data:
                builder = new AlertDialog.Builder(this);
                View view2 = LayoutInflater.from(this).inflate(R.layout.layout_custom_data, null);
                initCustomDataLayout(view2);
                builder.setView(view2);
                alertDialog = builder.create();
                alertDialog.show();

                break;

            case R.id.menu_reset:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Reset data");
                builder.setMessage("Do you want to reset all data?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                        AppVariables.confirmedCases = AppVariables.initialConfirmedCases;
                        AppVariables.modelData = null;
                        initFragment();
                    }
                });
                builder.setNegativeButton("No", null);
                alertDialog = builder.create();
                alertDialog.show();

                break;


            case R.id.menu_about_us:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }

            return super.onOptionsItemSelected(item);
    }


    private void getCustomData(){
        showProgressBar();
        String url = Urls.getConfirmedCasesData;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);

                    Log.e("Response for custom", response);
                    Gson gson = new Gson();
                    AppVariables.confirmedCases = gson.fromJson(object.toString(), ConfirmedCases.class);
                    initFragment();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Errror", ""+e.getLocalizedMessage());
                    showErrorDialog("Can't load data", "Something wrong. Please try again");
                }

                progressDialog.cancel();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Errrr", error.toString());
                progressDialog.cancel();
                if(error instanceof NoConnectionError){
                    showErrorDialog("No internet", "Please check your internet connection and try again");
                }
                else{
                    showErrorDialog("Can't load data", "Something wrong. Please try again");
                }
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                ModelData modelData = AppVariables.modelData;

                if(modelData.getPopulation() != 0.0){
                    params.put("population", ""+modelData.getPopulation());
                    params.put("R0", ""+modelData.getR0value());
                    params.put("I0", ""+modelData.getInitial_infected());
                    params.put("start_date", modelData.getStart_date());
                    params.put("incubation_period", ""+modelData.getIncubation_period());
                    params.put("infection_period", ""+modelData.getInfection_period());
                }

                params.put("intervention_date", ""+modelData.getIntervention_date());
                params.put("R1", ""+modelData.getR1value());
                params.put("model", ""+modelData.getModel());

                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void showErrorDialog(String title, String message){
        builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getCustomData();
                alertDialog.cancel();
            }
        });

        builder.setNegativeButton("Cancel", null);

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showProgressBar(){
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void initInterventionLayout(View view){
        tv_date = view.findViewById(R.id.tv_date);
        tv_date_error = view.findViewById(R.id.tv_date_error);
        edt_r0_value = view.findViewById(R.id.edt_r0_value);
        tv_cancel_btn = view.findViewById(R.id.tv_cancel_btn);
        tv_submit_btn = view.findViewById(R.id.tv_submit_btn);

        tv_date_error.setVisibility(View.GONE);

        setDate(day, month, year);
        current_date =  day+"/"+(month+1)+"/"+year;


        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatePicker();
            }
        });

        tv_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        tv_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(sdf.parse(current_date).compareTo(sdf.parse(selected_date)) > 0){

                        tv_date_error.setVisibility(View.VISIBLE);
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(TextUtils.isEmpty(edt_r0_value.getText().toString())){
                    edt_r0_value.setError("Required");
                }
                else{
                    ModelData modelData = null;

                    if(AppVariables.modelData != null){
                        modelData = AppVariables.modelData;
                    }
                    else{
                        modelData = new ModelData();
                    }
                    modelData.setR1value(Float.parseFloat(edt_r0_value.getText().toString()));
                    modelData.setIntervention_date(tv_date.getText().toString());

                    AppVariables.modelData = modelData;

                    getCustomData();
                    alertDialog.cancel();
                }
            }
        });
    }

    private void initCustomDataLayout(View view){
        tv_date = view.findViewById(R.id.tv_date);
        edt_population = view.findViewById(R.id.edt_population);
        edt_i0_value = view.findViewById(R.id.edt_i0_value);
        edt_r0_value = view.findViewById(R.id.edt_r0_value);
        edt_incubation_period = view.findViewById(R.id.edt_incubation_period);
        edt_infection_period = view.findViewById(R.id.edt_infection_period);
        tv_cancel_btn = view.findViewById(R.id.tv_cancel_btn);
        tv_submit_btn = view.findViewById(R.id.tv_submit_btn);
        rg_model = view.findViewById(R.id.rg_model);
        ll_incubation_period = view.findViewById(R.id.ll_incubation_period);

        setDate(day, month, year);

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatePicker();
            }
        });

        tv_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        tv_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBlank()){
                    ModelData modelData = new ModelData();

                    modelData.setPopulation(Double.parseDouble(edt_population.getText().toString()));
                    modelData.setInitial_infected(Integer.parseInt(edt_i0_value.getText().toString()));
                    modelData.setIncubation_period(Integer.parseInt(edt_incubation_period.getText().toString()));
                    modelData.setInfection_period(Integer.parseInt(edt_infection_period.getText().toString()));
                    modelData.setR0value(Float.parseFloat(edt_r0_value.getText().toString()));
                    modelData.setStart_date(tv_date.getText().toString());
                    modelData.setModel(model_selection);
                    modelData.setR1value(0);
                    modelData.setIntervention_date("");

                    AppVariables.modelData = modelData;

                    getCustomData();
                    alertDialog.cancel();
                }
            }
        });

        rg_model.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_seir_model){
                    model_selection = 0;
                    ll_incubation_period.setVisibility(View.VISIBLE);
                }
                else{
                    model_selection = 1;
                    ll_incubation_period.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setDatePicker(){
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                setDate(d, m, y);
            }
        }, year, month, day);
        datePickerDialog.show();
    }


    private void setDate(int day, int month, int year){
        selected_date =  day+"/"+(month+1)+"/"+year;
        tv_date.setText(selected_date);
    }

    private boolean checkBlank(){
        boolean blank = false;

        if(TextUtils.isEmpty(edt_population.getText().toString())){
            edt_population.setError("Required");
            blank = true;
        }
        if(TextUtils.isEmpty(edt_i0_value.getText().toString())){
            edt_i0_value.setError("Required");
            blank = true;
        }
        if(TextUtils.isEmpty(edt_r0_value.getText().toString())){
            edt_r0_value.setError("Required");
            blank = true;
        }
        if(TextUtils.isEmpty(edt_incubation_period.getText().toString())){
            edt_incubation_period.setError("Required");
            blank = true;
        }
        if(TextUtils.isEmpty(edt_infection_period.getText().toString())){
            edt_infection_period.setError("Required");
            blank = true;
        }

        return blank;
    }


}
