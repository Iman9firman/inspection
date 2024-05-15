package com.garasi.kita.inspection.RTP.model;

import java.sql.Timestamp;

public class StockInfo {
    private int id;
    private Timestamp tanggal;
    private String kategori;
    private int jumlahMasuk;
    private int jumlahKeluar;
    private double omset;
    private double untung;

    public StockInfo(int id, Timestamp tanggal, String kategori, int jumlahMasuk, int jumlahKeluar, double omset, double untung) {
        this.id = id;
        this.tanggal = tanggal;
        this.kategori = kategori;
        this.jumlahMasuk = jumlahMasuk;
        this.jumlahKeluar = jumlahKeluar;
        this.omset = omset;
        this.untung = untung;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }

    public void setTanggal(Timestamp tanggal) {
        this.tanggal = tanggal;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getJumlahMasuk() {
        return jumlahMasuk;
    }

    public void setJumlahMasuk(int jumlahMasuk) {
        this.jumlahMasuk = jumlahMasuk;
    }

    public int getJumlahKeluar() {
        return jumlahKeluar;
    }

    public void setJumlahKeluar(int jumlahKeluar) {
        this.jumlahKeluar = jumlahKeluar;
    }

    public double getOmset() {
        return omset;
    }

    public void setOmset(double omset) {
        this.omset = omset;
    }

    public double getUntung() {
        return untung;
    }

    public void setUntung(double untung) {
        this.untung = untung;
    }
    
}
