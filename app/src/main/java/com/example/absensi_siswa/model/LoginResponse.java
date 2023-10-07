package com.example.absensi_siswa.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse{

	@SerializedName("data")
	private Data data;

	@SerializedName("message")
	private String message;

	@SerializedName("token")
	private String token;

	public Data getData(){
		return data;
	}

	public String getMessage(){
		return message;
	}

	public String getToken(){
		return token;
	}
}