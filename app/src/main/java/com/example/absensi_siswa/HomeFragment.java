package com.example.absensi_siswa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.absensi_siswa.model.Attendance;
import com.example.absensi_siswa.model.Data;
import com.example.absensi_siswa.model.HomeResponse;
import com.example.absensi_siswa.model.LoginResponse;
import com.example.absensi_siswa.model.Rekap;
import com.example.absensi_siswa.retrofit.PortalClient;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView textTime,textDate, textDay, textName, textNis, textClassroom, tv_time, tv_status, tv_time_in, tv_hadir, tv_absen, tv_sakit, tv_izin, tv_bolos, tv_rekap;
    private ImageView profileView;
    private Button btn_add_leave;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        textTime = view.findViewById(R.id.textTime);
        textDate = view.findViewById(R.id.textDate);
        textDay = view.findViewById(R.id.textDay);
        textName = view.findViewById(R.id.textName);
        textNis = view.findViewById(R.id.textNis);
        textClassroom = view.findViewById(R.id.textClassroom);
        profileView = view.findViewById(R.id.profileView);

        tv_time = view.findViewById(R.id.tv_time);
        tv_status = view.findViewById(R.id.tv_status);
        tv_time_in = view.findViewById(R.id.tv_time_in);
        tv_hadir = view.findViewById(R.id.tv_hadir);
        tv_absen = view.findViewById(R.id.tv_absen);
        tv_sakit = view.findViewById(R.id.tv_sakit);
        tv_izin = view.findViewById(R.id.tv_izin);
        tv_bolos = view.findViewById(R.id.tv_bolos);
        tv_rekap = view.findViewById(R.id.tv_rekap);

//        btn_add_leave = view.findViewById(R.id.btn_add_leave);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                DateFormat clockformat = new SimpleDateFormat("HH:mm");
                DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                DateFormat dayFormat = new SimpleDateFormat("EEEE");

                textTime.setText(clockformat.format(new Date()));
                textDate.setText(dateFormat.format(new Date()));
                textDay.setText(dayFormat.format(new Date()));
                handler.postDelayed(this,1000);
            }
        });

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

        textName.setText(name);
        textNis.setText(nis);
        textClassroom.setText(classroom);

        ImageView ivBasicImage = (ImageView) view.findViewById(R.id.profileView);
        Picasso.get().load(image).into(ivBasicImage);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PortalClient client = retrofit.create(PortalClient.class);

        //Panggil Method
        Call<HomeResponse> call =  client.home("Bearer "+accesstoken);
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                HomeResponse homeResponse = response.body();
                if(homeResponse != null){
                    Attendance attendance = homeResponse.getAttendance();
                    Integer id = attendance.getId();
                    String time = attendance.getTime();
                    String time_in = attendance.getTimeIn();
                    Integer status = attendance.getStatus();

                    Rekap rekap = homeResponse.getRekap();
                    String years = rekap.getYears();
                    String semester = rekap.getSemester();
                    Integer hadir = rekap.getHadir();
                    Integer absen = rekap.getAbsen();
                    Integer sakit = rekap.getSakit();
                    Integer izin = rekap.getIzin();
                    Integer bolos = rekap.getBolos();


                    if(time==null){
                        tv_time.setText("-");
                    }else{
                        tv_time.setText(time);
                    }

                    if(time_in==null){
                        tv_time_in.setText("-");
                    }else{
                        tv_time_in.setText(time_in);
                    }

                    if(status==1){
                        tv_status.setText("Hadir");
                    }else if(status==2){
                        tv_status.setText("Sakit");
                    }else if(status==3){
                        tv_status.setText("Izin");
                    }else if(status==4){
                        tv_status.setText("Absen");
                    }else if(status==5){
                        tv_status.setText("Bolos");
                    }else{
                        tv_status.setText("Absen");
                    }

                    tv_hadir.setText(hadir.toString());
                    tv_absen.setText(absen.toString());
                    tv_sakit.setText(sakit.toString());
                    tv_izin.setText(izin.toString());
                    tv_bolos.setText(bolos.toString());
                    tv_rekap.setText("Tahun "+years+" Semester "+semester);
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), "Koneksi ke Server Gagal", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}