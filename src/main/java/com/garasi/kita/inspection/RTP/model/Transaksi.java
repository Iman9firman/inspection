package com.garasi.kita.inspection.RTP.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Transaksi {
    private int id;
    private String transaksiId;
    private int branchId;
    private String createdBy;
    private Timestamp tanggalTransaksi;
    private String keterangan;
    private double totalTransaksi;
    private int status;

    public Transaksi(int branchId, String createdBy, Timestamp tanggalTransaksi, String keterangan, double totalTransaksi) {
        this.transaksiId = generateTransaksiId(branchId, tanggalTransaksi);
        this.branchId = branchId;
        this.createdBy = createdBy;
        this.tanggalTransaksi = tanggalTransaksi;
        this.keterangan = keterangan;
        this.totalTransaksi = totalTransaksi;
    }

    public Transaksi() {

    }

    public String generateTransaksiId(int branchId, Date tanggalTransaksi) {
        String branchCode = String.format("%03d", branchId); // Format branchId to 3 digits
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateCode = dateFormat.format(tanggalTransaksi);
        String randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 6);

        return branchCode + dateCode + randomString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransaksiId() {
        return transaksiId;
    }

    public void setTransaksiId(String transaksiId) {
        this.transaksiId = transaksiId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public void setTanggalTransaksi(Timestamp tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public double getTotalTransaksi() {
        return totalTransaksi;
    }

    public void setTotalTransaksi(double totalTransaksi) {
        this.totalTransaksi = totalTransaksi;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public static Transaksi fromResultSet(ResultSet rs) throws SQLException {
        Transaksi transaksi = new Transaksi();
        transaksi.setId(rs.getInt("id"));
        transaksi.setTransaksiId(rs.getString("transaksi_id"));
        transaksi.setBranchId(rs.getInt("branch_id"));
        transaksi.setCreatedBy(rs.getString("created_by"));
        transaksi.setTanggalTransaksi(rs.getTimestamp("tanggal_transaksi"));
        transaksi.setKeterangan(rs.getString("keterangan"));
        transaksi.setTotalTransaksi(rs.getDouble("total_transaksi"));
        transaksi.setStatus(rs.getInt("status"));
        return transaksi;
    }
}
