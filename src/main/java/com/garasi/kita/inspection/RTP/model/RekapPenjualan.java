package com.garasi.kita.inspection.RTP.model;

import java.util.List;

public class RekapPenjualan {

    private String totalBalance;
    private List<DetailBalance> detailBalance;
    private List<Pengawas> pengawas;
    private List<Detail> detail;

    public String getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(String totalBalance) {
        this.totalBalance = totalBalance;
    }

    public List<DetailBalance> getDetailBalance() {
        return detailBalance;
    }

    public void setDetailBalance(List<DetailBalance> detailBalance) {
        this.detailBalance = detailBalance;
    }

    public List<Pengawas> getPengawas() {
        return pengawas;
    }

    public void setPengawas(List<Pengawas> pengawas) {
        this.pengawas = pengawas;
    }

    public List<Detail> getDetail() {
        return detail;
    }

    public void setDetail(List<Detail> detail) {
        this.detail = detail;
    }


    public static class DetailBalance {
        private String month;
        private String balance;

        public DetailBalance(String dayOfMonth, String valueOf) {
            this.month = dayOfMonth;
            this.balance = valueOf;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }
    }

    public static class Pengawas {
        private String name;
        private String balance;

        public Pengawas(String staf, String grandTotal) {
            this.name = staf;
            this.balance = grandTotal;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }
    }

    public static class Detail {
        private String invoiceNumber;
        private String grandTotal;
        private String branch;
        private String staf;
        private String date;

        public Detail(String invoiceId, String grandTotal, String branchName, String staf, String date) {
            this.invoiceNumber = invoiceId;
            this.grandTotal = grandTotal;
            this.branch = branchName;
            this.staf = staf;
            this.date = date;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }

        public String getGrandTotal() {
            return grandTotal;
        }

        public void setGrandTotal(String grandTotal) {
            this.grandTotal = grandTotal;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public String getStaf() {
            return staf;
        }

        public void setStaf(String staf) {
            this.staf = staf;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
