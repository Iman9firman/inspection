package com.garasi.kita.inspection.RTP.model;

import java.math.BigDecimal;
import java.util.Date;

public class Invoice {

    private String invoiceId;
    private BigDecimal grandTotal;
    private Object invoiceList;
    private BigDecimal modal;
    private BigDecimal untung;
    private String status;
    private int branchId;
    private String staf;
    private Date createDate;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Object getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(Object invoiceList) {
        this.invoiceList = invoiceList;
    }

    public BigDecimal getModal() {
        return modal;
    }

    public void setModal(BigDecimal modal) {
        this.modal = modal;
    }

    public BigDecimal getUntung() {
        return untung;
    }

    public void setUntung(BigDecimal untung) {
        this.untung = untung;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getStaf() {
        return staf;
    }

    public void setStaf(String staf) {
        this.staf = staf;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
