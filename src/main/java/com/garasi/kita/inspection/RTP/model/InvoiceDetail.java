package com.garasi.kita.inspection.RTP.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;


public class InvoiceDetail {

    private String transaksiId;
    private String createdBy;
    private String namaBarang;
    private int barangTerjual;
    private BigDecimal hargaModal;
    private BigDecimal hargaJual;
    private Long branchId;
    private int Status;

    // Constructors
    public InvoiceDetail() {
    }

    public InvoiceDetail(String transaksiId, String createdBy, String namaBarang, int barangTerjual, BigDecimal hargaModal, BigDecimal hargaJual, Long branchId) {
        this.transaksiId = transaksiId;
        this.createdBy = createdBy;
        this.namaBarang = namaBarang;
        this.barangTerjual = barangTerjual;
        this.hargaModal = hargaModal;
        this.hargaJual = hargaJual;
        this.branchId = branchId;
    }

    // Getters and Setters
    public String getTransaksiId() {
        return transaksiId;
    }

    public void setTransaksiId(String transaksiId) {
        this.transaksiId = transaksiId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public int getBarangTerjual() {
        return barangTerjual;
    }

    public void setBarangTerjual(int barangTerjual) {
        this.barangTerjual = barangTerjual;
    }

    public BigDecimal getHargaModal() {
        return hargaModal;
    }

    public void setHargaModal(BigDecimal hargaModal) {
        this.hargaModal = hargaModal;
    }

    public BigDecimal getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(BigDecimal hargaJual) {
        this.hargaJual = hargaJual;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
