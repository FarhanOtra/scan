package com.example.absensi_siswa.model;

import com.google.gson.annotations.SerializedName;

public class HistoryItem{

	@SerializedName("date")
	private String date;

	@SerializedName("time_in")
	private String timeIn;

	@SerializedName("id")
	private String id;

	@SerializedName("status")
	private String status;

	public String getDate(){
		return date;
	}

	public String getTimeIn(){
		return timeIn;
	}

	public String getId(){
		return id;
	}

	public String getStatus(){
		return status;
	}
}