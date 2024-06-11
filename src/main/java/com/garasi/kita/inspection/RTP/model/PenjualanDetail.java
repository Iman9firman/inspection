package com.garasi.kita.inspection.RTP.model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PenjualanDetail {

    private String invoiceId;
    private double grandTotal;
    private double modal;
    private double untung;
    private String status;
    private String staf;
    private String branchName;
    private Date createDate;

    public PenjualanDetail() {
    }

    public PenjualanDetail(String invoiceId, double grandTotal, double modal, double untung, String status, String staf, String branchName) {
        this.invoiceId = invoiceId;
        this.grandTotal = grandTotal;
        this.modal = modal;
        this.untung = untung;
        this.status = status;
        this.staf = staf;
        this.branchName = branchName;
    }

    public PenjualanDetail(String invoiceId, double grandTotal, double modal, double untung, String status, String staf, String branchName, Date createDate) {
        this.invoiceId = invoiceId;
        this.grandTotal = grandTotal;
        this.modal = modal;
        this.untung = untung;
        this.status = status;
        this.staf = staf;
        this.branchName = branchName;
        this.createDate = createDate;
    }

    // Getters and Setters
    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public double getModal() {
        return modal;
    }

    public void setModal(double modal) {
        this.modal = modal;
    }

    public double getUntung() {
        return untung;
    }

    public void setUntung(double untung) {
        this.untung = untung;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStaf() {
        return staf;
    }

    public void setStaf(String staf) {
        this.staf = staf;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public static PenjualanDetail fromResultSet(ResultSet rs) throws SQLException {
        String invoiceId = rs.getString("invoiceId");
        double grandTotal = rs.getDouble("grandTotal");
        double modal = rs.getDouble("modal");
        double untung = rs.getDouble("untung");
        String status = rs.getString("status");
        String staf = rs.getString("staf");
        String branchName = rs.getString("branch_name");
        Date createDate = rs.getDate("createDate");

        return new PenjualanDetail(invoiceId, grandTotal, modal, untung, status, staf, branchName, createDate);
    }
}
