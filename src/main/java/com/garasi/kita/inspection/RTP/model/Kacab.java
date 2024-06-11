package com.garasi.kita.inspection.RTP.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Kacab {
    private int branch_id;
    private String branch_name;
    @JsonIgnore
    private String tipe;
    @JsonIgnore
    private String address;

    public Kacab(int branch_id, String branch_name, String tipe, String address) {
        this.branch_id = branch_id;
        this.branch_name = branch_name;
        this.tipe = tipe;
        this.address = address;
    }

    public Kacab(String branch_name) {
        this.branch_name = branch_name;

    }

    public Kacab(int branch_id, String branch_name) {
        this.branch_id = branch_id;
        this.branch_name = branch_name;

    }

    // Getters and Setters
    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
