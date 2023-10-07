package com.example.absensi_siswa.model;

import com.google.gson.annotations.SerializedName;

public class AttendanceResponse{

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}