package com.example.absensi_siswa.model;

import com.google.gson.annotations.SerializedName;

public class Rekap{

	@SerializedName("hadir")
	private int hadir;

	@SerializedName("izin")
	private int izin;

	@SerializedName("sakit")
	private int sakit;

	@SerializedName("absen")
	private int absen;

	@SerializedName("bolos")
	private int bolos;

	@SerializedName("years")
	private String years;

	@SerializedName("semester")
	private String semester;

	public int getHadir(){
		return hadir;
	}

	public int getIzin(){
		return izin;
	}

	public int getSakit(){
		return sakit;
	}

	public int getAbsen(){
		return absen;
	}

	public int getBolos(){
		return bolos;
	}

	public String getYears(){
		return years;
	}

	public String getSemester(){
		return semester;
	}
}