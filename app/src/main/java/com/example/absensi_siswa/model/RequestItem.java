package com.example.absensi_siswa.model;

import com.google.gson.annotations.SerializedName;

public class RequestItem{

	@SerializedName("date")
	private String date;

	@SerializedName("start_date")
	private String start_date;

	@SerializedName("end_date")
	private String end_date;

	@SerializedName("id")
	private String id;

	@SerializedName("type")
	private String type;

	@SerializedName("title")
	private String title;

	@SerializedName("desc")
	private String desc;

	@SerializedName("status")
	private String status;

	public String getDate(){
		return date;
	}

	public String getId(){
		return id;
	}

	public String getType(){
		return type;
	}

	public String getTitle(){
		return title;
	}

	public String getDesc(){
		return desc;
	}

	public String getStatus(){
		return status;
	}

	public String getStart_date() { return start_date; }

	public String getEnd_date() { return end_date; }

}