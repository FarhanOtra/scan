package com.example.absensi_siswa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.absensi_siswa.model.ChangePasswordResponse;
import com.example.absensi_siswa.model.ServerResponse;
import com.example.absensi_siswa.retrofit.PortalClient;
import com.google.android.material.textfield.TextInputLayout;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {

    Button btn_back, btn_change;
    EditText et_oldPassword, et_newPassword, et_confirmPassword;
    TextInputLayout ti_oldPassword, ti_newPassword, ti_confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ti_oldPassword = findViewById(R.id.textInputOldPassword);
        ti_newPassword = findViewById(R.id.textInputNewPassword);
        ti_confirmPassword = findViewById(R.id.textInputConfirmPassword);

        et_oldPassword = findViewById(R.id.et_oldPassword);
        et_newPassword = findViewById(R.id.et_newPassword);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_change = findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValidation()){
                    changePassword();
                }
            }
        });
    }

    private Boolean checkValidation(){

        String oldPassword = et_oldPassword.getText().toString();
        String newPassword = et_newPassword.getText().toString();
        String confirmPassword = et_confirmPassword.getText().toString();

        if(oldPassword.isEmpty()) {
            ti_oldPassword.setErrorEnabled(true);
            ti_oldPassword.setError("Tidak Boleh Kosong");
        }else{
            ti_oldPassword.setErrorEnabled(false);
            ti_oldPassword.setError(null);
        }

        if(newPassword.isEmpty()) {
            ti_newPassword.setErrorEnabled(true);
            ti_newPassword.setError("Tidak Boleh Kosong");
        }else{
            if(newPassword.length()<8){
                ti_newPassword.setErrorEnabled(true);
                ti_newPassword.setError("Password Harus Lebih dari 8 Karakter");
            }else{
                ti_newPassword.setErrorEnabled(false);
                ti_newPassword.setError(null);
            }
        }

        if(confirmPassword.isEmpty()) {
            ti_confirmPassword.setErrorEnabled(true);
            ti_confirmPassword.setError("Tidak Boleh Kosong");
        }else {
            if(confirmPassword.equals(newPassword)){
                ti_confirmPassword.setErrorEnabled(false);
                ti_confirmPassword.setError(null);
            }else{
                ti_confirmPassword.setErrorEnabled(true);
                ti_confirmPassword.setError("Konfirmasi Tidak Cocok");
            }
        }

        if(oldPassword.isEmpty() || newPassword.isEmpty() || newPassword.length()<8 || confirmPassword.isEmpty() || !newPassword.equals(confirmPassword)){
            return false;
        }else{
            return true;
        }
    }

    public void changePassword(){
        SharedPreferences preferences = this.getSharedPreferences(
                "com.example.absensi-siswa.PREFS",
                Context.MODE_PRIVATE
        );
        String accesstoken = preferences.getString("ACCESS_TOKEN","");

        String oldPassword = et_oldPassword.getText().toString();
        String newPassword = et_newPassword.getText().toString();
        String confirmPassword = et_confirmPassword.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PortalClient client = retrofit.create(PortalClient.class);

        Call<ChangePasswordResponse> call = client.changePassword("Bearer "+accesstoken, oldPassword, newPassword, confirmPassword);

        SweetAlertDialog pDialog = new SweetAlertDialog(ChangePasswordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Mengirim..");
        pDialog.setCancelable(false);
        pDialog.show();

        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                pDialog.dismiss();
                try {
                    if(response.body()!=null){
                        new SweetAlertDialog(ChangePasswordActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Berhasil!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent mainIntent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                })
                                .show();
                    }
                    if(response.errorBody()!=null) {
                        new SweetAlertDialog(ChangePasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Password Lama Tidak Cocok")
                                .show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                pDialog.dismiss();
                new SweetAlertDialog(ChangePasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Koneksi Gagal")
                        .show();
//                Toast.makeText(AddLeaveActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}