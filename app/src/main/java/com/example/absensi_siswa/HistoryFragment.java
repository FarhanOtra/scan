package com.example.absensi_siswa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.absensi_siswa.adapter.HistoryAdapter;
import com.example.absensi_siswa.model.History;
import com.example.absensi_siswa.model.HistoryItem;
import com.example.absensi_siswa.model.HistoryResponse;
import com.example.absensi_siswa.retrofit.PortalClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements HistoryAdapter.OnHistoryHolderClickListener{

    RecyclerView rvListHistory;
    TextView tvHPeriod;
    private ArrayList<History> listData;
    View v;
    public HistoryFragment() {
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
        v= inflater.inflate(R.layout.fragment_history, container, false);
        HistoryAdapter adapter = new HistoryAdapter();
        tvHPeriod = v.findViewById(R.id.tvHPeriode);
        rvListHistory = (RecyclerView) v.findViewById(R.id.rvListHistory);
        rvListHistory.setAdapter(adapter);
        adapter.setListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvListHistory.setLayoutManager(layoutManager);

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

        Call<HistoryResponse> call = client.getHistory("Bearer "+accesstoken);
        call.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                HistoryResponse historyResponse = response.body();
                String period = historyResponse.getPeriod();
                ArrayList<History> listHistory = new ArrayList<History>();
                if (historyResponse != null) {
                    List<HistoryItem> historyItem = historyResponse.getHistory();
                    for (HistoryItem item : historyItem) {
                        History data = new History(
                                item.getId(),
                                item.getTimeIn(),
                                item.getStatus(),
                                item.getDate()
                                );
                        listHistory.add(data);
                    }
                }
                adapter.setListData(listHistory);
                tvHPeriod.setText(period);
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
//                Toast.makeText(getActivity(), "GAGAL", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    };

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onClick(View view, History history) {
//        Toast.makeText(getActivity(),history.id,Toast.LENGTH_SHORT).show();
    }
}