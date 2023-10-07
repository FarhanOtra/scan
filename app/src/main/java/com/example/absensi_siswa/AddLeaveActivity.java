package com.example.absensi_siswa;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.absensi_siswa.model.ServerResponse;
import com.example.absensi_siswa.retrofit.PortalClient;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddLeaveActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST = 100;
    private int PICK_IMAGE_FROM_GALLERY_REQUEST =1;
    TextView uri,path;
    EditText et_title, et_status, et_desc, et_date;
    TextInputLayout ti_title, ti_status, ti_date, ti_desc;
    Button btn_attachment, btn_send, btn_back;
    FrameLayout flAttachment;
    String fileUri;
    String type = "0";
    Integer attendance_id;

    final Calendar myCalendar= Calendar.getInstance();

    String [] items = {"Sakit","Izin"};

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_leave);

        Intent getIntent = getIntent();
        attendance_id = getIntent.getIntExtra("ATTENDANCE_ID", 0);
//        Toast.makeText(AddLeaveActivity.this,attendance_id.toString(), Toast.LENGTH_LONG).show();

        //Permission External
        if(ContextCompat.checkSelfPermission(AddLeaveActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddLeaveActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },MY_PERMISSION_REQUEST);
        }

        if (SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);

            if (Environment.isExternalStorageManager()) {

            } else {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }

        //For Input Select
        autoCompleteTextView = findViewById(R.id.et_type);

        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,items);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        type = "2";
                        break;
                    case 1:
                        type = "3";
                        break;
                }
            }
        });


        //For Input Date
        et_date = findViewById(R.id.et_date);

        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now());
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Pilih Tanggal").setCalendarConstraints(constraintsBuilderRange.build());
        final MaterialDatePicker materialDatePicker = builder.build();

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(),"DATE PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>) selection -> {
            Long startDate = selection.first;
            Long endDate = selection.second;
            String startDateString = DateFormat.format("dd/MM/yyyy", new Date(startDate)).toString();
            String endDateString = DateFormat.format("dd/MM/yyyy", new Date(endDate)).toString();
            et_date.setText(startDateString+" - "+endDateString);
        });

        //For Input Attachment
        btn_attachment = (Button) findViewById(R.id.btn_attachment);
        btn_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String [] mimeTypes = {"image/*", "application/pdf"};
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "SELECT FILE"),
                        PICK_IMAGE_FROM_GALLERY_REQUEST);
            }
        });

        btn_send = (Button) findViewById(R.id.btn_change);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    Uri file_uri = Uri.parse(fileUri);
                    new SweetAlertDialog(AddLeaveActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Yakin Ingin Mengirim Permohonan?")
                            .setCancelText("Batal")
                            .setConfirmText("Ya")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    uploadFile(file_uri);
                                    sweetAlertDialog.cancel();
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
            }
        });

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean validate(){
        ti_title = findViewById(R.id.textInputNis);
        ti_status = findViewById(R.id.textInputStatus);
        ti_date = findViewById(R.id.textInputDate);
        ti_desc = findViewById(R.id.textInputDesc);

        et_title = findViewById(R.id.et_nis);
        et_date = findViewById(R.id.et_date);
        et_desc = findViewById(R.id.et_desc);

        flAttachment = findViewById(R.id.container_with_dotted_border);

        String title = et_title.getText().toString();
        String date = et_date.getText().toString();
        String desc = et_desc.getText().toString();
        String attachment = btn_attachment.getText().toString();

        if(title.isEmpty()) {
            ti_title.setErrorEnabled(true);
            ti_title.setError("Tidak Boleh Kosong");
        }else{
            ti_title.setErrorEnabled(false);
            ti_title.setError(null);
        }
        if (type=="0"){
            ti_status.setErrorEnabled(true);
            ti_status.setError("Tidak Boleh Kosong");
        }else{
            ti_status.setErrorEnabled(false);
            ti_status.setError(null);
        }
        if(date.isEmpty()){
            ti_date.setErrorEnabled(true);
            ti_date.setError("Tidak Boleh Kosong");
        }else{
            ti_date.setErrorEnabled(false);
            ti_date.setError(null);
        }

        if(desc.isEmpty()){
            ti_desc.setErrorEnabled(true);
            ti_desc.setError("Tidak Boleh Kosong");
        }else{
            ti_desc.setErrorEnabled(false);
            ti_desc.setError(null);
        }
        if(attachment.isEmpty()) {
            flAttachment.setBackgroundResource(R.drawable.dot_line_red);
            btn_attachment.setHintTextColor(getResources().getColor(R.color.colorRed));
        } else{
            flAttachment.setBackgroundResource(R.drawable.dot_line);
            btn_attachment.setHintTextColor(getResources().getColor(R.color.dot));
        }

        if(title.isEmpty() || type=="0" || date.isEmpty() || desc.isEmpty() || attachment.isEmpty()){
            return false;
        }else{
            return true;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        et_date.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
                Uri uri = data.getData();
                fileUri = uri.toString();
                String path = FileUtils.getRealPath(this,uri);
                File file = new File(path);
                String strFileName = file.getName();
                btn_attachment = findViewById(R.id.btn_attachment);
                btn_attachment.setText(strFileName);
                Toast.makeText(getApplicationContext(),path.toString(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                } else {

                }
                return;
            }
        }
    }

    public void uploadFile(Uri uri){
        final Button name = findViewById(R.id.btn_attachment);

        SharedPreferences preferences = this.getSharedPreferences(
                "com.example.absensi-siswa.PREFS",
                Context.MODE_PRIVATE
        );
        String accesstoken = preferences.getString("ACCESS_TOKEN","");

        RequestBody descriptionPart = RequestBody.create(MultipartBody.FORM, name.getText().toString());

        File originalfile = new File(FileUtils.getRealPath(this, uri));

        RequestBody filePart = RequestBody.create(
                okhttp3.MediaType.parse("application/pdf"),
                originalfile
        );

        MultipartBody.Part file = MultipartBody.Part.createFormData("file", originalfile.getName(),filePart);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PortalClient client = retrofit.create(PortalClient.class);

        et_title = findViewById(R.id.et_nis);
        et_date = findViewById(R.id.et_date);
        et_desc = findViewById(R.id.et_desc);

        String title = et_title.getText().toString();
        String date = et_date.getText().toString();
        String desc = et_desc.getText().toString();

//        Toast.makeText(this,file+title+desc+type+date,Toast.LENGTH_SHORT).show();
        Call<ServerResponse> call = client.uploadFile("Bearer "+accesstoken, file, title, type, date, desc);

        SweetAlertDialog pDialog = new SweetAlertDialog(AddLeaveActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Mengirim..");
        pDialog.setCancelable(false);
        pDialog.show();

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                pDialog.dismiss();
                try {
                    if(response.body()!=null){
                        new SweetAlertDialog(AddLeaveActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Berhasil!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent mainIntent = new Intent(AddLeaveActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                })
                                .show();
//                        Toast.makeText(AddLeaveActivity.this,response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    if(response.errorBody()!=null) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            new SweetAlertDialog(AddLeaveActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(jObjError.getString("message"))
                                    .show();
                        } catch (Exception e) {
                            Toast.makeText(AddLeaveActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                pDialog.dismiss();
                new SweetAlertDialog(AddLeaveActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Koneksi Gagal")
                        .show();
//                Toast.makeText(AddLeaveActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

}