package com.example.absensi_siswa.model;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("image")
	private String image;

	@SerializedName("name")
	private String name;

	@SerializedName("nis")
	private String nis;

	@SerializedName("classroom")
	private String classroom;

	public String getImage(){
		return image;
	}

	public String getName(){
		return name;
	}

	public String getNis(){
		return nis;
	}

	public String getClassroom(){
		return classroom;
	}
}