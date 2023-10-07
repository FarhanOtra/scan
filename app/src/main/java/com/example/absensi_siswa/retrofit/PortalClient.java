package com.example.absensi_siswa.retrofit;

import com.example.absensi_siswa.model.AttendanceResponse;
import com.example.absensi_siswa.model.ChangePasswordResponse;
import com.example.absensi_siswa.model.HistoryResponse;
import com.example.absensi_siswa.model.HomeResponse;
import com.example.absensi_siswa.model.LoginResponse;
import com.example.absensi_siswa.model.RequestResponse;
import com.example.absensi_siswa.model.ServerResponse;

import java.util.Date;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PortalClient {

    //EndPoint Login
    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginResponse> checkLogin(@Field("nis") String nis, @Field("password") String password);

    //EndPoint Logout
    @Headers({"Accept: application/json", "Content-Type: multipart/form-data"})
    @GET("auth/logout")
    Call<ServerResponse> logout(@Header("Authorization") String token);

    //EndPoint ChangePassword
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("auth/changePassword")
    Call<ChangePasswordResponse> changePassword(@Header("Authorization") String token, @Field("oldPassword") String oldPassword, @Field("newPassword") String newPassword, @Field("confirmPassword") String confirmPassword);

    //EndPoint Home
    @Headers({"Accept: application/json", "Content-Type: multipart/form-data"})
    @GET("home")
    Call<HomeResponse> home(@Header("Authorization") String token);

    //EndPoint getAttendance
    @Headers({"Accept: application/json", "Content-Type: multipart/form-data"})
    @GET("getAttendance/{id}")
    Call<AttendanceResponse> getAttendance(@Header("Authorization") String token, @Path("id") String id);

    //EndPoint getHistory
    @Headers({"Accept: application/json", "Content-Type: multipart/form-data"})
    @GET("getHistory")
    Call<HistoryResponse> getHistory(@Header("Authorization") String token);

    //EndPoint  getRequest
    @Headers({"Accept: application/json", "Content-Type: multipart/form-data"})
    @GET("getRequest")
    Call<RequestResponse> getRequest(@Header("Authorization") String token);

    //EndPoint postLeave
    @Multipart
    @POST("postLeave")
    Call<ServerResponse> uploadFile(
            @Header("Authorization") String token,
            @Part MultipartBody.Part file,
            @Query("title") String title,
            @Query("type") String status,
            @Query("date") String date,
            @Query("desc") String desc
    );
}
