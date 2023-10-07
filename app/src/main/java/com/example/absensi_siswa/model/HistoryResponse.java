package com.example.absensi_siswa.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class HistoryResponse{

	@SerializedName("success")
	private boolean success;

	@SerializedName("history")
	private List<HistoryItem> history;

	@SerializedName("message")
	private String message;

	@SerializedName("period")
	private String period;

	public String getPeriod() { return period; }

	public boolean isSuccess(){
		return success;
	}

	public List<HistoryItem> getHistory(){
		return history;
	}

	public String getMessage(){
		return message;
	}
}