package com.mss.eyespy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    private Context context;
    private List<AttendanceList> attendanceListList;

    public AttendanceAdapter(Context context, List<AttendanceList> attendanceListList) {
        this.context = context;
        this.attendanceListList = attendanceListList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceList attendanceList = attendanceListList.get(position);

        holder.tvName.setText(attendanceList.getName());
        holder.tvPhone.setText(attendanceList.getPhone());
        holder.tvInTime.setText("In-Time: " + attendanceList.getInTime());

        // Load Profile Image using Glide (Add Glide dependency in build.gradle)
        Glide.with(context).load(attendanceList.getProfileUrl()).placeholder(R.drawable.default_profile).into(holder.ivProfile);

        holder.itemView.setOnClickListener(v -> {
            String phoneNo = holder.tvPhone.getText().toString();

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNo));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendanceListList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvInTime;
        ImageView ivProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvInTime = itemView.findViewById(R.id.tv_in_time);
            ivProfile = itemView.findViewById(R.id.iv_profile);
        }
    }
}

