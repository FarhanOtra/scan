package com.example.absensi_siswa.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.example.absensi_siswa.R;
import com.example.absensi_siswa.model.Request;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>  {
    ArrayList<Request> listData = new ArrayList<>();

    public class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textRTitle, textRDate, textRType, textRDesc, textRStatus, textRDateRange;
        ImageView imageViewStatus;
        CardView cardViewType;
        TextView tv_desc;
        ConstraintLayout constraintLayout,expandConstraintLayout;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textRTitle = itemView.findViewById(R.id.textRTitle);
            textRDate = itemView.findViewById(R.id.textRDate);
            textRType = itemView.findViewById(R.id.textRType);
            textRDesc = itemView.findViewById(R.id.textRDesc);
            textRStatus = itemView.findViewById(R.id.textRStatus);
            textRDateRange = itemView.findViewById(R.id.textRDateRange);
            cardViewType = itemView.findViewById(R.id.cardViewAttendance);
            imageViewStatus = itemView.findViewById(R.id.imageViewStatus);
            itemView.setOnClickListener(this);
            tv_desc = itemView.findViewById(R.id.textRDesc);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            expandConstraintLayout = itemView.findViewById(R.id.expandConstraintLayout);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Request request = listData.get(getAdapterPosition());
                    request.setVisibility(!request.isVisibility());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
            if(listener != null){
                listener.onClick(view, listData.get(getAdapterPosition()));
            }
        }
    }

    RequestAdapter.OnRequestHolderClickListener listener = null;

    public interface OnRequestHolderClickListener{

        void onClick(View view, Request request);
    }


    public void setListener(RequestAdapter.OnRequestHolderClickListener listener) {
        this.listener = listener;
    }

    public void setListData(ArrayList<Request> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_request, parent, false);
        return new RequestAdapter.RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.RequestViewHolder holder, int position) {
        Request request = listData.get(position);
        String type = "";

        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        DateFormat dateOnlyFormat = new SimpleDateFormat("dd MMMM yyyy");

        Date date = null;
        try {
            date = sdfIn.parse(request.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date start_date = null;
        try {
            start_date = sdfIn.parse(request.start_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date end_date = null;
        try {
            end_date = sdfIn.parse(request.end_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Integer checkStatus = Integer.valueOf(request.status);

        if(checkStatus==1){
            holder.imageViewStatus.setImageResource(R.drawable.ic_wait);
            holder.textRStatus.setText("Menunggu");
        }
        if(checkStatus==2){
            holder.imageViewStatus.setImageResource(R.drawable.ic_acc);
            holder.textRStatus.setText("Disetujui");
        }
        if(checkStatus==3){
            holder.imageViewStatus.setImageResource(R.drawable.ic_cancel);
            holder.textRStatus.setText("Ditolak");
        }

        Integer checkType = Integer.valueOf(request.type);

        if(checkType==2){
            type = "Sakit";
            holder.cardViewType.setCardBackgroundColor(Color.YELLOW);
        }
        if(checkType==3){
            type = "Izin";
            holder.cardViewType.setCardBackgroundColor(Color.CYAN);
        }

        holder.textRTitle.setText(request.title);
        holder.textRType.setText(type);
        holder.textRDate.setText("Dikirim : "+dateFormat.format(date));
        holder.textRDateRange.setText(dateOnlyFormat.format(start_date)+" - "+dateOnlyFormat.format(end_date));
        holder.textRDesc.setText(request.desc);

        boolean isVisible = request.visibility;
        holder.expandConstraintLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

}
