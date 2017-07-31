package com.example.jamessmith.weatherexample1.fiveday;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jamessmith.weatherexample1.R;

import java.util.List;

/**
 * Created by James on 31/07/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.CustomViewHolder>{

    private List<FiveDayModel> fiveDayModel;
    private Context context;
    public Adapter(List<FiveDayModel> fiveDayModel, Context context){
        this.fiveDayModel = fiveDayModel;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.five_day_list, parent, false);
        return new CustomViewHolder(view, context, fiveDayModel);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        holder._day.setText(fiveDayModel.get(position).getDay());
        double degrees = f2c(Double.parseDouble(fiveDayModel.get(position).getTemp()));

        Log.v(Adapter.class.getName(), fiveDayModel.get(position).getTemp());
        holder._temp.setText(Math.floor(degrees) + " c");

        if(fiveDayModel.get(position).getIcon() != null){
            Glide.with(context)
                    .load(fiveDayModel.get(position).getIcon()+".png")
                    .asBitmap()
                    .fitCenter()
                    .centerCrop()
                    .into(holder._icon);
        }
    }

    private double f2c(double f)
    {
        return (f-32)*5/9;
    }

    @Override
    public int getItemCount() {
        return fiveDayModel == null ? 0 : fiveDayModel.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        TextView _day, _temp;
        View _customView;
        ImageView _icon;
        Intent intent;

        public CustomViewHolder(View view, final Context context, final List<FiveDayModel> model){
            super(view);

            _day = view.findViewById(R.id.tv_day);
            _temp = view.findViewById(R.id.tv_temp);
            _customView = view.findViewById(R.id.rl_segment);
            intent = new Intent();

            _customView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("index", getAdapterPosition());
                    intent.putExtra("selectedDay", model.get(getAdapterPosition()).getDay());
                    intent.setAction("updateAdapter");
                    context.sendBroadcast(intent);
                }
            });
        }
    }
}
