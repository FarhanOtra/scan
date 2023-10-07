package com.example.absensi_siswa.model;

import com.google.gson.annotations.SerializedName;

public class Attendance{

	@SerializedName("time_in")
	private String timeIn;

	@SerializedName("id")
	private int id;

	@SerializedName("time")
	private String time;

	@SerializedName("status")
	private int status;

	public String getTimeIn(){
		return timeIn;
	}

	public int getId(){
		return id;
	}

	public String getTime(){
		return time;
	}

	public int getStatus(){
		return status;
	}
}