package com.example.absensi_siswa.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RequestResponse{

	@SerializedName("request")
	private List<RequestItem> request;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	@SerializedName("period")
	private String period;

	public String getPeriod() { return period; }

	public List<RequestItem> getRequest(){
		return request;
	}

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}