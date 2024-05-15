package com.garasi.kita.inspection.RTP.model;

public class Stock {
    private int stock_id;
    private Product product;
    private Kacab kacab; // Objek Gudang untuk mengetahui lokasi barang
    private int bad;
    private int good;

    public int getBad() {
        return bad;
    }

    public void setBad(int bad) {
        this.bad = bad;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public Stock(int stock_id, Product product, int quantity, Kacab kacab) {
        this.stock_id = stock_id;
        this.product = product;
        this.kacab = kacab;
    }

    public Stock() {

    }

    // Getters and Setters
    public int getStock_id() {
        return stock_id;
    }

    public void setStock_id(int stock_id) {
        this.stock_id = stock_id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Kacab getKacab() {
        return kacab;
    }

    public void setKacab(Kacab kacab) {
        this.kacab = kacab;
    }

    // toString method for debugging or logging

}
