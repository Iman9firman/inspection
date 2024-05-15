package com.garasi.kita.inspection.RTP.model;

public class DetailHistoryStock {
    private int good;
    private int bad;
    private int product_id;
    private String product_name;

    // Constructors, getters, and setters
    public DetailHistoryStock() {
    }

    public DetailHistoryStock(int good, int bad, String user, int product_id) {
        this.good = good;
        this.bad = bad;
        this.product_id = product_id;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getBad() {
        return bad;
    }

    public void setBad(int bad) {
        this.bad = bad;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

}
