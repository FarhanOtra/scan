package com.example.absensi_siswa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.absensi_siswa.model.Data;
import com.example.absensi_siswa.model.LoginResponse;
import com.example.absensi_siswa.retrofit.PortalClient;
import com.google.android.material.textfield.TextInputLayout;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    EditText editTextNis, editTextPassword;
    TextInputLayout textInputNis, textInputPassword;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextNis = findViewById(R.id.et_nis);
        editTextPassword = findViewById(R.id.et_password);
        textInputNis = findViewById(R.id.textInputNis);
        textInputPassword = findViewById(R.id.textInputPassword);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValidation()){
                    checkLogin();
                }
            }
        });
    }

    private Boolean checkValidation(){

        String username = editTextNis.getText().toString();
        String password = editTextPassword.getText().toString();

        if(username.isEmpty()) {
            textInputNis.setErrorEnabled(true);
            textInputNis.setError("Tidak Boleh Kosong");
        }else{
            textInputNis.setErrorEnabled(false);
            textInputNis.setError(null);
        }

        if(password.isEmpty()) {
            textInputPassword.setErrorEnabled(true);
            textInputPassword.setError("Tidak Boleh Kosong");
        }else{
            textInputPassword.setErrorEnabled(false);
            textInputPassword.setError(null);
        }

        if(username.isEmpty() || password.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    public void checkLogin() {

        String nis = editTextNis.getText().toString();
        String password = editTextPassword.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PortalClient client = retrofit.create(PortalClient.class);

        //Panggil Method
        Call<LoginResponse> call =  client.checkLogin(nis, password);

        SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Login..");
        pDialog.setCancelable(false);
        pDialog.show();

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                pDialog.dismiss();
                LoginResponse loginResponse = response.body();
                if(loginResponse != null){
                    Data data = loginResponse.getData();
                    String token = loginResponse.getToken();
                    String name = data.getName();
                    String nis = data.getNis();
                    String classroom = data.getClassroom();
                    String imageUrl = data.getImage();

                    SharedPreferences preferences = getSharedPreferences("com.example.absensi-siswa.PREFS", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("ACCESS_TOKEN", token);
                    editor.putString("NAME", name);
                    editor.putString("NIS", nis);
                    editor.putString("CLASSROOM", classroom);
                    editor.putString("IMAGE_URL", imageUrl);
                    editor.putBoolean("IS_LOGGED_IN", true);

                    editor.apply();
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("NIS atau Password Salah!")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                pDialog.dismiss();
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Koneksi Gagal")
                        .show();
            }
        });
    }
}