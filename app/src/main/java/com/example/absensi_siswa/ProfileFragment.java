package com.example.absensi_siswa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.absensi_siswa.model.History;
import com.example.absensi_siswa.model.HistoryItem;
import com.example.absensi_siswa.model.HistoryResponse;
import com.example.absensi_siswa.model.ServerResponse;
import com.example.absensi_siswa.retrofit.PortalClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {
    View v;
    ImageView iv_profile;
    TextView tv_pNama, tv_pNis, tv_pKelas;
    Button btn_logout, btn_changePassword;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_profile, container, false);

        iv_profile = v.findViewById(R.id.iv_profile);
        tv_pNama = v.findViewById(R.id.tv_pNama);
        tv_pNis = v.findViewById(R.id.tv_pNis);
        tv_pKelas = v.findViewById(R.id.tv_pKelas);

        SharedPreferences preferences = this.getActivity().getSharedPreferences(
                "com.example.absensi-siswa.PREFS",
                Context.MODE_PRIVATE
        );
        String imageUrl = preferences.getString("IMAGE_URL","avatar.png");
        String name = preferences.getString("NAME"," ");
        String nis = preferences.getString("NIS"," ");
        String classroom = preferences.getString("CLASSROOM"," ");
        String image = BuildConfig.STORAGE_URL+imageUrl;
        String accesstoken = preferences.getString("ACCESS_TOKEN","");

        tv_pNama.setText(name);
        tv_pNis.setText(nis);
        tv_pKelas.setText(classroom);
        Picasso.get().load(image).into(iv_profile);

        btn_logout = v.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Yakin Ingin Logout?")
                        .setCancelText("Batal")
                        .setConfirmText("Ya")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(BuildConfig.BASE_URL)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                PortalClient client = retrofit.create(PortalClient.class);

                                Call<ServerResponse> call = client.logout("Bearer "+accesstoken);

                                call.enqueue(new Callback<ServerResponse>() {
                                    @Override
                                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                        try {
                                            if(response.isSuccessful()){
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.clear();
                                                editor.apply();
                                                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                                getActivity().finishAffinity();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Koneksi Gagal")
                                                .show();
                                    }
                                });
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();
            }
        });

        btn_changePassword = v.findViewById(R.id.btn_changePassword);
        btn_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }
}