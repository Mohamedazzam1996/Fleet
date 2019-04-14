package com.fleetmanagment.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fleetmanagment.FMApp;
import com.fleetmanagment.R;
import com.fleetmanagment.model.WorkloadData;
import com.fleetmanagment.ui.activity.MapActivity;
import com.fleetmanagment.ui.adapter.ScheduleAdapter.ScheduleViewHolder;

import java.security.SecureRandom;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class ScheduleAdapter extends Adapter<ScheduleViewHolder> {

    private final WorkloadData[] records;
    private final Context context;

    static class ScheduleViewHolder extends ViewHolder {
        final View color;
        final TextView vehicle;
        final TextView startDate;
        final TextView endDate;
        final TextView view;

        ScheduleViewHolder(@NonNull final View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.color);
            vehicle = itemView.findViewById(R.id.vehicle);
            startDate = itemView.findViewById(R.id.start_date);
            endDate = itemView.findViewById(R.id.end_date);
            view = itemView.findViewById(R.id.view);
        }
    }

    public ScheduleAdapter(final WorkloadData[] records, final Context context) {
        this.records = records;
        this.context = context;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_schedule, parent, false);
        return new ScheduleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ScheduleViewHolder holder, final int position) {
        final Random rnd = new SecureRandom();
        final int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.color.setBackgroundColor(color);

        if( records[position].vehicle != null) {
            holder.vehicle.setText(records[position].vehicle.brand.name);
        }
        holder.startDate.setText(records[position].startDate);
        holder.endDate.setText(records[position].endDate);
        holder.view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {

            }
        });
        holder.view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Intent intent = new Intent(FMApp.appContext, MapActivity.class);
                intent.putExtra(MapActivity.WORKLOAD, records[position]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.length;
    }
}
