package com.mss.eyespy;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.ViewHolder> {

    private Context context;
    private List<VisitorList> visitorListList;

    public VisitorAdapter(Context context, List<VisitorList> visitorListList){
        this.context = context;
        this.visitorListList = visitorListList;
    }

    @NonNull
    @Override
    public VisitorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.visitor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitorAdapter.ViewHolder holder, int position) {
        VisitorList visitorList = visitorListList.get(position);
    }

    @Override
    public int getItemCount() {
        return visitorListList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvvisitorname, tvvisitingname, tvintime, tvouttime;
        ImageView ivprint,ivvisitorphoto;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvvisitorname = itemView.findViewById(R.id.tv_VisitorName);
            tvvisitingname = itemView.findViewById(R.id.tv_VisitingParty);
            ivvisitorphoto = itemView.findViewById(R.id.iv_Photo);
            tvintime = itemView.findViewById(R.id.tv_in_time);
            tvouttime = itemView.findViewById(R.id.tv_out_time);
            ivprint  = itemView.findViewById(R.id.iv_print);
        }
    }
}
