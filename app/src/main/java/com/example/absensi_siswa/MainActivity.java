package com.example.absensi_siswa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private boolean isLoggedIn = false;
    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    RequestFragment requestFragment = new RequestFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                "com.example.absensi-siswa.PREFS",
                Context.MODE_PRIVATE
        );
        isLoggedIn = preferences.getBoolean("IS_LOGGED_IN",false);

        FirebaseMessaging.getInstance().subscribeToTopic("absensi");

        //Firebase Registration
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(
                new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful()){
                            String token = task.getResult();
                            Log.d("fcm-token",token);
                        }
                    }
                }
        );

        if(!isLoggedIn){
            Intent loginIntent = new Intent(this,LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, homeFragment).commit();
                        return true;
                    case R.id.history:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, historyFragment).commit();
                        return true;
                    case R.id.request:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, requestFragment).commit();
                        return true;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, profileFragment).commit();
                        return true;
//                        SharedPreferences preferences = getSharedPreferences("com.example.absensi-siswa.PREFS", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.clear();
//                        editor.apply();
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        startActivity(intent);
//                        finishAffinity();
                }
                return false;
            }
        });

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        bottomNavigationView.setItemIconTintList(null);

    }

    public void onScan(View view){
        Intent homeIntent = new Intent(getApplicationContext(), ScanActivity.class);
        startActivity(homeIntent);
    }

    public void LogOut(View view) {
        SharedPreferences preferences = getSharedPreferences("com.example.absensi-siswa.PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }

}