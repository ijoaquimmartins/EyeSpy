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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<AttendanceList> attendanceListList;
    private List<AttendanceList> attendanceListFull;

    public AttendanceAdapter(Context context, List<AttendanceList> attendanceListList) {
        this.context = context;
        this.attendanceListList = attendanceListList;
        this.attendanceListFull = new ArrayList<>(attendanceListList);
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
        String status = attendanceList.getStatus();
        if (status != null && status.equals("Absent")){
            holder.tvInTime.setText(attendanceList.getStatus());
        }else {
            holder.tvInTime.setText("In-Time: " + attendanceList.getInTime());
        }

        holder.tvType.setText(attendanceList.getType());

        if (attendanceList.getProfileUrl() != null && !attendanceList.getProfileUrl().isEmpty()) {
            Glide.with(context).load(attendanceList.getProfileUrl())
                    .placeholder(R.drawable.default_profile)
                    .into(holder.ivProfile);
        }

        holder.itemView.setOnClickListener(v -> {
            String phoneNo = holder.tvPhone.getText().toString();

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
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

    @Override
    public Filter getFilter() {
        return attendanceFilter;
    }

    private final Filter attendanceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AttendanceList> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(attendanceListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (AttendanceList item : attendanceListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)
                            || item.getType().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            attendanceListList.clear();
            attendanceListList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvInTime, tvType;
        ImageView ivProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvType = itemView.findViewById(R.id.tv_type);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvInTime = itemView.findViewById(R.id.tv_in_time);
            ivProfile = itemView.findViewById(R.id.iv_profile);
        }
    }
}

