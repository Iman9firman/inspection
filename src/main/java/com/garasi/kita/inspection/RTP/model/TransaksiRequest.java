package com.garasi.kita.inspection.RTP.model;

import java.util.List;

public class TransaksiRequest {
    private Transaksi transaksi;
    private List<TransaksiDetail> transaksiDetails;

    public TransaksiRequest(Transaksi transaksi, List<TransaksiDetail> transaksiDetails) {
        this.transaksi = transaksi;
        this.transaksiDetails = transaksiDetails;
    }

    public Transaksi getTransaksi() {
        return transaksi;
    }

    public void setTransaksi(Transaksi transaksi) {
        this.transaksi = transaksi;
    }

    public List<TransaksiDetail> getTransaksiDetails() {
        return transaksiDetails;
    }

    public void setTransaksiDetails(List<TransaksiDetail> transaksiDetails) {
        this.transaksiDetails = transaksiDetails;
    }
}
