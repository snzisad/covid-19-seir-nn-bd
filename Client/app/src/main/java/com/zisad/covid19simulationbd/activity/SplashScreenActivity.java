package com.zisad.covid19simulationbd.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.zisad.covid19simulationbd.R;
import com.zisad.covid19simulationbd.model.ConfirmedCases;
import com.zisad.covid19simulationbd.utils.AppVariables;
import com.zisad.covid19simulationbd.utils.MySingleton;
import com.zisad.covid19simulationbd.utils.Urls;

import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity {

    TextView tv_title;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        getConfirmedCaseData();
        initOthers();
        setAnimation();
    }

    private void initOthers(){
        tv_title = findViewById(R.id.tv_title);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);
    }

    private void setAnimation(){

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(2000);

        tv_title.setAnimation(fadeIn);

        tv_title.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    private void getConfirmedCaseData(){
        String url = Urls.getConfirmedCasesData;

        JsonObjectRequest objectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("REsponse", response.toString());
                Gson gson = new Gson();
                AppVariables.confirmedCases = AppVariables.initialConfirmedCases = gson.fromJson(response.toString(), ConfirmedCases.class);

                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Errrr", error.toString());
                if(error instanceof NoConnectionError){
                    showErrorDialog("No internet", "Please check your internet connection and try again");
                }
                else{
                    showErrorDialog("Can't load data", "Something wrong. Please try again");
                }
            }
        });


        MySingleton.getInstance(this).addToRequestQueue(objectRequest);
    }

    private void showErrorDialog(String title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setCancelable(false);
        dialog.setMessage(message);
        dialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getConfirmedCaseData();
                dialog.cancel();
            }
        });

        dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.create().show();

    }
}
