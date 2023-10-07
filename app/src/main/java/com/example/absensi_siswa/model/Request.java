package com.example.absensi_siswa.model;

public class Request {
    public String id;
    public String type;
    public String title;
    public String desc;
    public String status;
    public String date;
    public String end_date;
    public String start_date;
    public boolean visibility;

    public Request(String id, String type, String title, String desc, String status, String date, String end_date, String start_date) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.desc = desc;
        this.status = status;
        this.date = date;
        this.end_date = end_date;
        this.start_date = start_date;
        this.visibility = false;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
}
