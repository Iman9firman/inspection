package com.garasi.kita.inspection.RTP.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;

public class Product {
    private int id;
    private String kodeBarang;
    private String namaBarang;
    private int stockAwal;
    private double hargaModal;
    private double hargaJual;
    private String kategori;
    private String create_by;
    private Timestamp create_date;

    public Product(String kodeBarang, String namaBarang, int stockAwal, double hargaModal, double hargaJual, String kategori, String create_by, Timestamp create_date) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.stockAwal = stockAwal;
        this.hargaModal = hargaModal;
        this.hargaJual = hargaJual;
        this.kategori = kategori;
        this.create_by = create_by;
        this.create_date = create_date;
    }

    public Product(int id, String kodeBarang, String namaBarang, int stockAwal, double hargaModal, double hargaJual, String kategori, String create_by, Timestamp create_date) {
        this.id = id;
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.stockAwal = stockAwal;
        this.hargaModal = hargaModal;
        this.hargaJual = hargaJual;
        this.kategori = kategori;
        this.create_by = create_by;
        this.create_date = create_date;
    }

    public Product(int id, String namaBarang) {
        this.id = id;
        this.namaBarang = namaBarang;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public int getStockAwal() {
        return stockAwal;
    }

    public void setStockAwal(int stockAwal) {
        this.stockAwal = stockAwal;
    }

    public double getHargaModal() {
        return hargaModal;
    }

    public void setHargaModal(double hargaModal) {
        this.hargaModal = hargaModal;
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(double hargaJual) {
        this.hargaJual = hargaJual;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public Timestamp getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Timestamp create_date) {
        this.create_date = create_date;
    }

}

