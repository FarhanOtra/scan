package com.example.absensi_siswa.model;

public class History {
    public String id;
    public String time_in;
    public String status;
    public String date;

    public History(String id, String time_in, String status, String date) {
        this.id = id;
        this.time_in = time_in;
        this.status = status;
        this.date = date;
    }
}
