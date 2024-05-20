package com.example.demoapp.Activities.admin.provinces;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.HttpRequest.ApiService;
import com.example.demoapp.Models.Dto.Requests.ProvinceRequest;
import com.example.demoapp.Models.Dto.Response.BaseResponse;
import com.example.demoapp.Models.Dto.Response.ProvinceResponse;
import com.example.demoapp.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProvinceAdapter extends RecyclerView.Adapter<ProvinceAdapter.ProvinceViewHolder> {
    private List<Province> provinceList;
    private Context context;

    public ProvinceAdapter(List<Province> provinceList) {
        this.provinceList = provinceList;
    }

    @NonNull
    @Override
    public ProvinceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_province, parent, false);
        context = parent.getContext();
        return new ProvinceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ProvinceViewHolder holder, int position
    ) {
        Province province = provinceList.get(position);
        holder.bind(province);
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (provinceList != null) {
            return provinceList.size();
        }
        return 0;
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteTextView(position))
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public void setProvinces(List<Province> provinces) {
        this.provinceList = provinces;
        notifyDataSetChanged();
    }

    public void deleteTextView(int position) {
        int userId = provinceList.get(position)
                .getId();

        ApiService.apiService.deleteProvince(userId)
                .enqueue(new Callback<BaseResponse<Void>>() {
                    @Override
                    public void onResponse(
                            Call<BaseResponse<Void>> call,
                            Response<BaseResponse<Void>> response
                    ) {
                        if (response.isSuccessful()) {
                            provinceList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, provinceList.size());
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                    }
                });
    }

    public class ProvinceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProvinceName, tvId;
        private ImageView imgDelete;

        public ProvinceViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_province_id);
            tvProvinceName = itemView.findViewById(R.id.tv_province);
            imgDelete = itemView.findViewById(R.id.province_delete);
        }

        public void bind(Province province) {
            tvProvinceName.setText("Province name: "+province.getName());
        }
    }

}

