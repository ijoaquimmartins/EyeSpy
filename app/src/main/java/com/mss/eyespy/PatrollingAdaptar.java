package com.mss.eyespy;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PatrollingAdaptar extends RecyclerView.Adapter<PatrollingAdaptar.ViewHolder> {

    private Context context;
    private final List<PatrollingList> patrollingLists;

    public PatrollingAdaptar(Context context, List<PatrollingList> patrollingLists){
        this.context = context;
        this.patrollingLists = patrollingLists;
    }

    @NonNull
    @Override
    public PatrollingAdaptar.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patrolling_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatrollingAdaptar.ViewHolder holder, int position) {
        PatrollingList patrollingList = patrollingLists.get(position);
        holder.tv_QrCode.setText(patrollingList.getQrcode());
        holder.tv_QrName.setText(patrollingList.getQrname());
        holder.tv_QrLocation.setText(patrollingList.getLatitude()+" "+patrollingList.getLongitude());

        holder.tv_TimeScanned.setText(patrollingList.getScandatetime());

        if ("0".equals(patrollingList.getUploaded())) {
            holder.iv_Uploaded.setColorFilter(ContextCompat.getColor(context, R.color.gray), PorterDuff.Mode.SRC_IN);
        } else if ("1".equals(patrollingList.getUploaded())) {
            holder.iv_Uploaded.setColorFilter(ContextCompat.getColor(context, R.color.green), PorterDuff.Mode.SRC_IN);
        } else {
            holder.iv_Uploaded.setColorFilter(ContextCompat.getColor(context, R.color.red), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemCount() {
        return patrollingLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_QrCode, tv_QrName, tv_QrLocation, tv_TimeScanned;
        ImageView iv_Uploaded;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_QrCode = itemView.findViewById(R.id.tv_QrCode);
            tv_QrName = itemView.findViewById(R.id.tv_QrName);
            tv_QrLocation = itemView.findViewById(R.id.tv_QrLocation);
            tv_TimeScanned = itemView.findViewById(R.id.tv_TimeScanned);
            iv_Uploaded = itemView.findViewById(R.id.iv_Uploaded);
        }
    }
}
