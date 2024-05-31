package com.garasi.kita.inspection.RTP.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransaksiDetail {
    private int transaksiDetailId;
    private String transaksiId;
    private int pengambilanBarang;
    private int barangTerjual;
    private int sisaBarang;
    private int barangReject;
    private double totalHarga;
    private String productId;
    private String namaBarang;

    public int getTransaksiDetailId() {
        return transaksiDetailId;
    }

    public void setTransaksiDetailId(int transaksiDetailId) {
        this.transaksiDetailId = transaksiDetailId;
    }

    public String getTransaksiId() {
        return transaksiId;
    }

    public void setTransaksiId(String transaksiId) {
        this.transaksiId = transaksiId;
    }

    public int getPengambilanBarang() {
        return pengambilanBarang;
    }

    public void setPengambilanBarang(int pengambilanBarang) {
        this.pengambilanBarang = pengambilanBarang;
    }

    public int getBarangTerjual() {
        return barangTerjual;
    }

    public void setBarangTerjual(int barangTerjual) {
        this.barangTerjual = barangTerjual;
    }

    public int getSisaBarang() {
        return sisaBarang;
    }

    public void setSisaBarang(int sisaBarang) {
        this.sisaBarang = sisaBarang;
    }

    public int getBarangReject() {
        return barangReject;
    }

    public void setBarangReject(int barangReject) {
        this.barangReject = barangReject;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public static TransaksiDetail fromResultSet(ResultSet rs) throws SQLException {
        TransaksiDetail transaksiDetail = new TransaksiDetail();
        transaksiDetail.setTransaksiDetailId(rs.getInt("transaksi_detail_id"));
        transaksiDetail.setTransaksiId(rs.getString("transaksi_id"));
        transaksiDetail.setPengambilanBarang(rs.getInt("pengambilan_barang"));
        transaksiDetail.setBarangTerjual(rs.getInt("barang_terjual"));
        transaksiDetail.setSisaBarang(rs.getInt("sisa_barang"));
        transaksiDetail.setBarangReject(rs.getInt("barang_reject"));
        transaksiDetail.setTotalHarga(rs.getDouble("total_harga"));
        transaksiDetail.setProductId(rs.getString("product_id"));
        transaksiDetail.setNamaBarang(rs.getString("namaBarang"));
        return transaksiDetail;

    }
}
