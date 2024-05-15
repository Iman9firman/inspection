package com.garasi.kita.inspection.RTP.model;

import java.sql.Timestamp;
import java.util.List;

public class HistoryStock {
    private int id;
    private Timestamp tanggal;
    private String jenisTransaksi;
    private String username;
    private List<DetailHistoryStock> detailList;
    private int branch_id;

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

    public String getJenisTransaksi() {
        return jenisTransaksi;
    }

    public void setJenisTransaksi(String jenisTransaksi) {
        this.jenisTransaksi = jenisTransaksi;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<DetailHistoryStock> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DetailHistoryStock> detailList) {
        this.detailList = detailList;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

}
