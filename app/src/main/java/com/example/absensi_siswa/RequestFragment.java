package com.example.absensi_siswa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.FileUtils;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.absensi_siswa.adapter.HistoryAdapter;
import com.example.absensi_siswa.adapter.RequestAdapter;
import com.example.absensi_siswa.model.History;
import com.example.absensi_siswa.model.HistoryItem;
import com.example.absensi_siswa.model.HistoryResponse;
import com.example.absensi_siswa.model.LoginResponse;
import com.example.absensi_siswa.model.Request;
import com.example.absensi_siswa.model.RequestItem;
import com.example.absensi_siswa.model.RequestResponse;
import com.example.absensi_siswa.model.ServerResponse;
import com.example.absensi_siswa.retrofit.PortalClient;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFragment#} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment implements RequestAdapter.OnRequestHolderClickListener{

    RecyclerView rvListRequest;
    TextView tvRPeriod;
    FloatingActionButton btn_add;
    MaterialCardView cardViewRequest;
    ImageButton imageButtonExpand;
    LinearLayout layoutExpand;
    EditText et_show;
    private ArrayList<Request> listData;
    View v;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_request, container, false);

        tvRPeriod = v.findViewById(R.id.tvRPeriod);
        RequestAdapter adapter = new RequestAdapter();
        rvListRequest = (RecyclerView) v.findViewById(R.id.rvListRequest);
        rvListRequest.setAdapter(adapter);
        adapter.setListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvListRequest.setLayoutManager(layoutManager);

        SharedPreferences preferences = this.getActivity().getSharedPreferences(
                "com.example.absensi-siswa.PREFS",
                Context.MODE_PRIVATE
        );
        String accesstoken = preferences.getString("ACCESS_TOKEN","");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PortalClient client = retrofit.create(PortalClient.class);

        Call<RequestResponse> call = client.getRequest("Bearer "+accesstoken);
        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                RequestResponse requestResponse = response.body();
                String period = requestResponse.getPeriod();
                ArrayList<Request> listRequest = new ArrayList<Request>();
                if (requestResponse != null) {
                    List<RequestItem> requestItem = requestResponse.getRequest();
                    for (RequestItem item : requestItem) {
                        Request data = new Request(
                                item.getId(),
                                item.getType(),
                                item.getTitle(),
                                item.getDesc(),
                                item.getStatus(),
                                item.getDate(),
                                item.getEnd_date(),
                                item.getStart_date()
                        );
                        listRequest.add(data);
                    }
                }
                adapter.setListData(listRequest);
                tvRPeriod.setText(period);
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {
//                Toast.makeText(getActivity(), "GAGAL", Toast.LENGTH_SHORT).show();
            }
        });

        btn_add = v.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(getActivity().getApplicationContext(), AddLeaveActivity.class);
                startActivity(addIntent);
            }
        });
        return v;
    }


    @Override
    public void onClick(View view, Request request) {

    }
}