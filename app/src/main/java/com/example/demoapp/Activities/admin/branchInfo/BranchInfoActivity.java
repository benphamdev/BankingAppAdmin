package com.example.demoapp.Activities.admin.branchInfo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.demoapp.Activities.admin.AdminsActivity;
import com.example.demoapp.Activities.admin.OnDeleteClickListener;
import com.example.demoapp.HttpRequest.ApiService;
import com.example.demoapp.Models.Dto.Requests.BranchRequest;
import com.example.demoapp.Models.Dto.Response.AccountInfoResponse;
import com.example.demoapp.Models.Dto.Response.BaseResponse;
import com.example.demoapp.Models.Dto.Response.BranchResponse;
import com.example.demoapp.Models.Dto.Response.ProvinceResponse;
import com.example.demoapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchInfoActivity extends AppCompatActivity implements OnDeleteClickListener {
    private RecyclerView rcvBranchInfo;
    private BranchInfoAdapter branchInfoAdapter;
    private List<BranchInfo> branchInfoList = new ArrayList<>();
    private ImageView imgEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_info);
        rcvBranchInfo = findViewById(R.id.rcv_admin_branchinfo);
        imgEdit = findViewById(R.id.edit_branchinfo);
        rcvBranchInfo.setLayoutManager(new LinearLayoutManager(this));
        branchInfoAdapter = new BranchInfoAdapter(branchInfoList);
        rcvBranchInfo.setAdapter(branchInfoAdapter);
        toolbars();
        getBranchInfo();
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateOrUpdateDialog(null);
            }
        });
    }

    private void toolbars() {
        Toolbar toolbar = findViewById(R.id.tool_bar_admin_branchinfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        ImageView backButton = findViewById(R.id.toolbar_back_admin_branchinfo);
        Drawable drawable = backButton.getDrawable();
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(
                    drawable,
                    ContextCompat.getColor(this, R.color.white)
            );
        }
        backButton.setImageDrawable(drawable);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BranchInfoActivity.this, AdminsActivity.class));
            }
        });
    }

    private void getBranchInfo() {
        ApiService.apiService.getAllBranches()
                .enqueue(new Callback<BaseResponse<List<BranchResponse>>>() {
                    @Override
                    public void onResponse(
                            Call<BaseResponse<List<BranchResponse>>> call,
                            Response<BaseResponse<List<BranchResponse>>> response
                    ) {
                        if (response.isSuccessful()) {
                            List<BranchResponse> branchResponses = response.body()
                                    .getData();
                            if (branchResponses != null) {
                                for (BranchResponse branchResponse : branchResponses) {
                                    String provinceName = branchResponse.getProvince()
                                            .getName();
                                    ProvinceResponse provinceResponse =
                                            new ProvinceResponse(
                                                    branchResponse.getId(),
                                                    provinceName
                                            );
                                    Log.e(
                                            "provinceResponse",
                                            provinceResponse.getName()
                                    );

                                    ApiService.apiService.getAccountInfo(branchResponse.getId())
                                            .enqueue(new Callback<BaseResponse<List<AccountInfoResponse>>>() {
                                                @Override
                                                public void onResponse(
                                                        Call<BaseResponse<List<AccountInfoResponse>>> call,
                                                        Response<BaseResponse<List<AccountInfoResponse>>> accountResponse
                                                ) {
                                                    List<AccountInfoResponse>
                                                            accountInfoResponseList =
                                                            new ArrayList<>(); // Initialize account info list
                                                    if (accountResponse.isSuccessful()) {
                                                        List<AccountInfoResponse>
                                                                accountInfoResponses =
                                                                accountResponse.body()
                                                                        .getData();
                                                        if (accountInfoResponses != null) {
                                                            accountInfoResponseList.addAll(
                                                                    accountInfoResponses);
                                                        }
                                                    }

                                                    // Check if there are any accounts
                                                    if (!accountInfoResponseList.isEmpty()) {
                                                        // If there are accounts, create BranchInfo with account info
                                                        BranchInfo
                                                                branchInfo =
                                                                new BranchInfo(
                                                                        String.valueOf(
                                                                                branchResponse.getId()),
                                                                        branchResponse.getBranchName(),
                                                                        branchResponse.getAddress(),
                                                                        provinceResponse,
                                                                        accountInfoResponseList
                                                                );
                                                        branchInfoList.add(
                                                                branchInfo);
                                                    } else {
                                                        // If there are no accounts, create BranchInfo without account info
                                                        BranchInfo
                                                                branchInfo =
                                                                new BranchInfo(
                                                                        String.valueOf(
                                                                                branchResponse.getId()),
                                                                        branchResponse.getBranchName(),
                                                                        branchResponse.getAddress(),
                                                                        provinceResponse,
                                                                        null
                                                                        // or an empty list if preferred
                                                                );
                                                        branchInfoList.add(
                                                                branchInfo);
                                                    }

                                                    // Notify adapter after adding branch info
                                                    branchInfoAdapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onFailure(
                                                        Call<BaseResponse<List<AccountInfoResponse>>> call,
                                                        Throwable t
                                                ) {
                                                    Log.d(
                                                            "E",
                                                            t.getMessage()
                                                    );
                                                }
                                            });
                                }
                                // Initialize the adapter with the branch info list
                                branchInfoAdapter = new BranchInfoAdapter(
                                        branchInfoList);
                                // Set adapter to RecyclerView
                                rcvBranchInfo.setAdapter(branchInfoAdapter);
                                branchInfoAdapter.notifyDataSetChanged();
                            }
                        } else {
                            try {
                                Log.d(
                                        "TAGBranch",
                                        response.errorBody()
                                                .toString()
                                );
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<BaseResponse<List<BranchResponse>>> call, Throwable t
                    ) {
                        Log.d("E", t.getMessage());
                    }
                });
    }
    private void showCreateOrUpdateDialog(@Nullable BranchInfo branchInfo) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_branch_dialog, null);
        dialogBuilder.setView(dialogView);

        EditText editTextBranchName = dialogView.findViewById(R.id.editTextBranchName);
        EditText editTextAddress = dialogView.findViewById(R.id.editTextAddress);
        EditText editTextProvinceName = dialogView.findViewById(R.id.editTextProvinceName);
        dialogBuilder.setTitle(branchInfo != null ? "Edit Branch" : "Create Branch");
        dialogBuilder.setPositiveButton(branchInfo != null ? "Save" : "Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String branchName = editTextBranchName.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();
                String provinceName = editTextProvinceName.getText().toString().trim();
                    createBranch(branchName, address, provinceName);
            }
        });

        dialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        if (branchInfo != null) {
            editTextBranchName.setText(branchInfo.getBranchName());
            editTextAddress.setText(branchInfo.getAddress());
            editTextProvinceName.setText(branchInfo.getProvinceName().getName());
        }
    }
    private void createBranch(String branchName, String address, String provinceName) {
        BranchRequest branchRequest = new BranchRequest(branchName, address, provinceName);
        ApiService.apiService.createBranch(branchRequest).enqueue(new Callback<BaseResponse<BranchResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<BranchResponse>> call, Response<BaseResponse<BranchResponse>> response) {
                if (response.isSuccessful()) {
                    BranchResponse newBranch = response.body().getData();
                    BranchInfo newBranchInfo = new BranchInfo(
                            String.valueOf(newBranch.getId()),
                            newBranch.getBranchName(),
                            newBranch.getAddress(),
                            newBranch.getProvince(),
                            null
                    );
                    branchInfoList.add(newBranchInfo);
                    branchInfoAdapter.notifyDataSetChanged();
                } else {
                    try {
                        Log.d("TAG", "Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<BranchResponse>> call, Throwable t) {
                Log.e("TAG", "Error: " + t.getMessage());
            }
        });
    }
    @Override
    public void onDeleteClick(int position) {
        if (branchInfoAdapter != null) {
            branchInfoAdapter.deleteBranch(position);
        }
    }

}