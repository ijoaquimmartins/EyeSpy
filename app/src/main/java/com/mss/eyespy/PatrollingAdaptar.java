package com.mss.eyespy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
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

        holder.tv_LocationName.setText(patrollingList.getColumnName());
        holder.tv_LocationPoint.setText(patrollingList.getQrLocation());
        holder.tv_TimeToScan.setText(patrollingList.getScanTimefr() + " - " + patrollingList.getScanTimeto());
        holder.tv_QrCodeId.setText(String.valueOf(patrollingList.getQrcodeId()));

        //On click function for each list view
        holder.itemView.setOnClickListener(view -> {
            String qrid = holder.tv_QrCodeId.getText().toString();

            Patrolling patrollingfun = new Patrolling(); //calling qr scan from Patrolling Activity
            patrollingfun.scanQRCode(qrid); // String qrid is only to send id to patrolling Activity to clear the alarm from alarm manager

        });
    }

    @Override
    public int getItemCount() {
        return patrollingLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_LocationName, tv_LocationPoint, tv_TimeToScan, tv_QrCodeId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_LocationName = itemView.findViewById(R.id.tv_LocationName);
            tv_LocationPoint = itemView.findViewById(R.id.tv_LocationPoint);
            tv_TimeToScan = itemView.findViewById(R.id.tv_TimeToScan);
            tv_QrCodeId = itemView.findViewById(R.id.tv_QrCodeId);
        }
    }
}
