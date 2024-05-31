package com.garasi.kita.inspection.RTP.model;

import java.util.List;
import java.util.Date;

public class Penjualan {

    private double totalBalance;
    private List<Setoran> setoranList;
    private List<History> historyList;

    // Constructor
    public Penjualan(double totalBalance, List<Setoran> setoranList, List<History> historyList) {
        this.totalBalance = totalBalance;
        this.setoranList = setoranList;
        this.historyList = historyList;
    }

    // Getter dan Setter
    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public List<Setoran> getSetoranList() {
        return setoranList;
    }

    public void setSetoranList(List<Setoran> setoranList) {
        this.setoranList = setoranList;
    }

    public List<History> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }

    // Inner class untuk Setoran
    public static class Setoran {
        private String nama;
        private double nominal;

        // Constructor
        public Setoran(String nama, double nominal) {
            this.nama = nama;
            this.nominal = nominal;
        }

        // Getter dan Setter
        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }

        public double getNominal() {
            return nominal;
        }

        public void setNominal(double nominal) {
            this.nominal = nominal;
        }
    }

    // Inner class untuk History
    public static class History {
        private String nama;
        private String invoice;
        private Date tanggal;
        private double nominal;

        // Constructor
        public History(String nama, String invoice, Date tanggal, double nominal) {
            this.nama = nama;
            this.invoice = invoice;
            this.tanggal = tanggal;
            this.nominal = nominal;
        }

        // Getter dan Setter
        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }

        public Date getTanggal() {
            return tanggal;
        }

        public void setTanggal(Date tanggal) {
            this.tanggal = tanggal;
        }

        public double getNominal() {
            return nominal;
        }

        public void setNominal(double nominal) {
            this.nominal = nominal;
        }
    }
}
