package com.example.absensi_siswa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.example.absensi_siswa.R;
import com.example.absensi_siswa.model.History;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>  {
    ArrayList<History> listData = new ArrayList<>();

    public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tDay, tDate, tDateFormat, tTime, tStatus;
        ConstraintLayout historyConstraint;
//        ImageView ivStatus;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tDay = itemView.findViewById(R.id.tDay);
            tDate = itemView.findViewById(R.id.tDate);
            tDateFormat = itemView.findViewById(R.id.tDateFormat);
            tTime = itemView.findViewById(R.id.tTime);
            tStatus = itemView.findViewById(R.id.tStatus);
            historyConstraint = itemView.findViewById(R.id.historyConstraint);
//            ivStatus = itemView.findViewById(R.id.ivStatus);
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            if(listener != null){
                listener.onClick(view, listData.get(getAdapterPosition()));
            }
        }
    }

    HistoryAdapter.OnHistoryHolderClickListener listener = null;

    public interface OnHistoryHolderClickListener{
        void onClick(View view);

        void onClick(View view, History history);
    }


    public void setListener(HistoryAdapter.OnHistoryHolderClickListener listener) {
        this.listener = listener;
    }

    public void setListData(ArrayList<History> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_history, parent, false);
        return new HistoryAdapter.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.HistoryViewHolder holder, int position) {
        History history = listData.get(position);

        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timesdfIn = new SimpleDateFormat("HH:mm:ss");

        DateFormat dayFormat = new SimpleDateFormat("EEEE");
        DateFormat dateFormat = new SimpleDateFormat("dd");
        DateFormat dateformatFormat = new SimpleDateFormat("MMMM yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Date date = null;
        try {
            date = sdfIn.parse(history.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date time = null;
        if(history.time_in != null){
            try {
                time = timesdfIn.parse(history.time_in);
                holder.tTime.setText(timeFormat.format(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Integer checkStatus = Integer.valueOf(history.status);

        if(checkStatus==1){
            holder.tStatus.setText("Hadir");
            holder.historyConstraint.setBackgroundResource(R.drawable.gradient_green);
        }else{
            if(checkStatus==2){
                holder.tStatus.setText("Sakit");
                holder.historyConstraint.setBackgroundResource(R.drawable.gradient_orange);
            }else{
                if(checkStatus==3){
                    holder.tStatus.setText("Izin");
                    holder.historyConstraint.setBackgroundResource(R.drawable.gradient_blue);
                }else{
                    if(checkStatus==4){
                        holder.tStatus.setText("Absen");
                        holder.historyConstraint.setBackgroundResource(R.drawable.gradient_red);
                    }else{
                        if(checkStatus==5){
                            holder.tStatus.setText("Bolos");
                            holder.historyConstraint.setBackgroundResource(R.drawable.gradient_grey);
                        }
                    }
                }
            }
        }

        holder.tDay.setText(dayFormat.format(date));
        holder.tDate.setText(dateFormat.format(date));
        holder.tDateFormat.setText(dateformatFormat.format(date));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

}
