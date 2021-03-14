package com.zisad.covid19simulationbd.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.zisad.covid19simulationbd.R;

import java.util.ArrayList;
import java.util.HashMap;


public class ConfirmedCaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    ArrayList date, cases;
    Context context;
    ViewHolderClass itemView;


    public ConfirmedCaseAdapter(Context context, ArrayList date, ArrayList cases){
        this.context = context;
        this.date = date;
        this.cases = cases;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_prediction_table, parent, false);
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        itemView = (ViewHolderClass) holder;

        if(position == 0){
            itemView.tv_date.setText("Date");
            itemView.tv_case_number.setText("Cases");
            itemView.tv_date.setTextColor(context.getResources().getColor(R.color.black));
            itemView.tv_case_number.setTextColor(context.getResources().getColor(R.color.black));
        }
        else{
            itemView.tv_date.setText(date.get(position-1).toString());
            itemView.tv_case_number.setText(""+cases.get(position-1));
        }

    }

    @Override
    public int getItemCount() {
        return date.size()+1;
    }


    public class ViewHolderClass extends RecyclerView.ViewHolder{

        TextView tv_date, tv_case_number;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            tv_date = itemView.findViewById(R.id.tv_date);
            tv_case_number = itemView.findViewById(R.id.tv_case_number);

        }
    }



}
