package com.mss.eyespy;

import static com.mss.eyespy.SharedPreferences.ImageURL;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
        holder.tvvisitorname.setText(visitorList.getVisitors_name());
        holder.tvvisitingname.setText(visitorList.getVisiting_to());
        holder.tvintime.setText(visitorList.getIn_datetime());
        holder.tvouttime.setText(visitorList.getOut_datetime());
        holder.tvcode.setText(visitorList.getCode());
        if (!visitorList.getPhoto().isEmpty()) {

            Glide.with(context).load(ImageURL + visitorList.getPhoto()).into(holder.ivvisitorphoto);
        }else{
            Glide.with(context).load(R.drawable.default_profile).into(holder.ivvisitorphoto);
        }

        holder.ivprint.setOnClickListener(view -> {
            String code = visitorList.getCode();
            if (code == null || code.trim().isEmpty()) {
                Toast.makeText(context, "QR Code data is empty!", Toast.LENGTH_LONG).show();
                return;
            }
            Log.d("QR_CODE_INTENT", "Sending code: " + code);
            Intent intent = new Intent(context, VisitorPrintQR.class);
            intent.putExtra("code", code.trim());
            context.startActivity(intent);
        });
        holder.ll_showDetails.setOnClickListener(view -> {
            Intent i = new Intent(context, VisitorOut.class);

            Bundle bundle = new Bundle();
            bundle.putString("id", visitorList.getId());
            bundle.putString("photo", visitorList.getPhoto());
            bundle.putString("visitorname", visitorList.getVisitors_name());
            bundle.putString("contactno", visitorList.getContact_no());
            bundle.putString("visitingto", visitorList.getVisiting_to());
            bundle.putString("location", visitorList.getFlat_no());
            bundle.putString("idatetime", visitorList.getIn_datetime());
            bundle.putString("outdatetime", visitorList.getOut_datetime());
            bundle.putString("confirmed", visitorList.getConfirm_by());
            bundle.putString("purpose", visitorList.getPurpose());
            bundle.putString("vehicleno", visitorList.getVehicleno());
            bundle.putString("vehiclephoto", visitorList.getVehicle_photo());

            i.putExtras(bundle);
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return visitorListList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvvisitorname, tvvisitingname, tvintime, tvouttime, tvcode;
        ImageView ivprint, ivvisitorphoto;
        LinearLayout ll_showDetails;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_showDetails = itemView.findViewById(R.id.ll_showDetails);
            tvvisitorname = itemView.findViewById(R.id.tv_VisitorName);
            tvvisitingname = itemView.findViewById(R.id.tv_VisitingParty);
            ivvisitorphoto = itemView.findViewById(R.id.iv_Photo);
            tvintime = itemView.findViewById(R.id.tv_in_time);
            tvouttime = itemView.findViewById(R.id.tv_out_time);
            ivprint  = itemView.findViewById(R.id.iv_print);
            tvcode =itemView.findViewById(R.id.tv_code);
        }
    }
}
