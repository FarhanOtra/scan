package com.example.absensi_siswa.model;

import com.google.gson.annotations.SerializedName;

public class HomeResponse{

	@SerializedName("rekap")
	private Rekap rekap;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	@SerializedName("attendance")
	private Attendance attendance;

	public Rekap getRekap(){
		return rekap;
	}

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}

	public Attendance getAttendance(){
		return attendance;
	}
}