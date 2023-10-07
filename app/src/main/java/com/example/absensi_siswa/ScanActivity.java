package com.example.absensi_siswa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.absensi_siswa.model.AttendanceResponse;
import com.example.absensi_siswa.retrofit.PortalClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.Result;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScanActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 112;
    private static final int LOCATION_PERMISSION_CODE = 123;
    private CodeScanner mCodeScanner;
    String lat="0";
    String lon="0";
    TextView tv_distance_status, tv_distance;
    ConstraintLayout constraintLayoutDistance;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        int ALL_PERMISSIONS = 101;

        final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};

        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);

//        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
//        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE);

        tv_distance_status = findViewById(R.id.tv_distance_status);
        tv_distance = findViewById(R.id.tv_distance);
        constraintLayoutDistance = findViewById(R.id.constraintLayoutDistance);

        LocationManager nManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!nManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        }else{
            getLocation();
        }

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (checkLocation(lat, lon)) {
                            attendance(result.getText());
                        } else {
                            Toast.makeText(getApplicationContext(), "Diluar Area Sekolah", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==CAMERA_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(ScanActivity.this, "Camera Permission Granted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ScanActivity.this, "Camera Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode==LOCATION_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(ScanActivity.this, "Location Permission Granted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ScanActivity.this, "Location Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hidupkan GPS untuk melakukan Absensi").setCancelable(false).setPositiveButton("OK", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(@NonNull Location location) {
                if(location != null) {
                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();

                    lat = latitude.toString();
                    lon = longitude.toString();
                    String source = latitude + "," + longitude;
                    //Toast.makeText(getApplicationContext(),source.toString(),Toast.LENGTH_SHORT).show();
                    Log.i("TEST",source.toString());

                    getDistance(lat, lon);
                }
            }
        });
    }

    public void getDistance(String lat, String lon){
        Location startPoint=new Location("LocationUser");
        startPoint.setLatitude(Double.parseDouble(lat));
        startPoint.setLongitude(Double.parseDouble(lon));

        Location endPoint=new Location("LocationSekolah");
        endPoint.setLatitude(-0.913198);
        endPoint.setLongitude(100.405547);

        double distance=startPoint.distanceTo(endPoint);
        String sdistance=String.valueOf(distance);
        if(distance<100){
            constraintLayoutDistance.setBackgroundResource(R.color.colorGreen);
            tv_distance_status.setText("DALAM AREA SEKOLAH");
            tv_distance.setText("Jarak : "+String.format("%.1f", distance)+" Meter");
        }else{
            constraintLayoutDistance.setBackgroundResource(R.color.colorRed);
            tv_distance_status.setText("DILUAR AREA SEKOLAH");
            tv_distance.setText("Jarak : "+String.format("%.1f", distance)+" Meter");
        }

    }

    public void attendance(String id) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PortalClient client = retrofit.create(PortalClient.class);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                "com.example.absensi-siswa.PREFS",
                Context.MODE_PRIVATE
        );
        String accesstoken = preferences.getString("ACCESS_TOKEN", "");
//        Toast.makeText(getApplicationContext(), accesstoken, Toast.LENGTH_SHORT).show();

        //Panggil Method
        Call<AttendanceResponse> call = client.getAttendance("Bearer " + accesstoken, id);
        call.enqueue(new Callback<AttendanceResponse>() {
            @Override
            public void onResponse(Call<AttendanceResponse> call, Response<AttendanceResponse> response) {
                try {
                    if (response.body() != null) {
                        new SweetAlertDialog(ScanActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Absensi Berhasil!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent mainIntent = new Intent(ScanActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                })
                                .show();
                        //Toast.makeText(HomeActivity.this,response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    if (response.errorBody() != null) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            new SweetAlertDialog(ScanActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(jObjError.getString("message"))
                                    .show();
                        } catch (Exception e) {
                            Toast.makeText(ScanActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AttendanceResponse> call, Throwable t) {
                new SweetAlertDialog(ScanActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Koneksi Gagal")
                        .setContentText("Periksa Koneksi dan Jaringan")
                        .show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(ScanActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ScanActivity.this, new String[]{permission}, requestCode);
        } else {
//            Toast.makeText(HomeActivity.this, "Permission already Granted", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkLocation(String lat, String lon) {

        Location startPoint=new Location("LocationUser");
        startPoint.setLatitude(Double.parseDouble(lat));
        startPoint.setLongitude(Double.parseDouble(lon));

        Location endPoint=new Location("LocationSekolah");
        endPoint.setLatitude(-0.913198);
        endPoint.setLongitude(100.405547);

        double distance=startPoint.distanceTo(endPoint);
        if(distance<100){
            return true;
        }
        return false;
    }
}