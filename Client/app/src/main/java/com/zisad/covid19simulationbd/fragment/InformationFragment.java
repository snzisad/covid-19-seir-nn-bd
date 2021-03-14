package com.zisad.covid19simulationbd.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.zisad.covid19simulationbd.R;
import com.zisad.covid19simulationbd.activity.MainActivity;
import com.zisad.covid19simulationbd.utils.AppVariables;

public class InformationFragment extends Fragment {
    WebView wv_information;
    String information;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_information, container, false);

        initOthers(view);

        return view;
    }

    private void initOthers(View view){
        information = AppVariables.confirmedCases.getInformation();

        wv_information = view.findViewById(R.id.wv_information);

        wv_information.setFocusableInTouchMode(false);
        wv_information.setBackgroundColor(Color.WHITE);
        wv_information.setFocusable(false);
        wv_information.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = wv_information.getSettings();
        webSettings.setDefaultFontSize(14);
        webSettings.setJavaScriptEnabled(false);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String description_html = "<html><head>"
                + "<style type=\"text/css\">body{color: #000000;}"
                + "</style></head>"
                + "<body><p>"
                + information
                + "</p></body></html>";

        wv_information.loadDataWithBaseURL(null, description_html, mimeType, encoding, null);

    }
}
