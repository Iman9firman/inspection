package com.garasi.kita.inspection.RTP.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cabang {
    private int branch_id;
    private String branch_name;
    private String tipe;
    private String address;

    public Cabang() {
    }


    public Cabang(int branch_id, String branch_name, String tipe, String address) {
        this.branch_id = branch_id;
        this.branch_name = branch_name;
        this.tipe = tipe;
        this.address = address;
    }

    public Cabang(String branch_name) {
        this.branch_name = branch_name;

    }

    public Cabang(int branch_id, String branch_name) {
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

    public static Cabang fromResultSet(ResultSet rs) throws SQLException {
        int branch_id = rs.getInt("branch_id");
        String branch_name = rs.getString("branch_name");
        String tipe = rs.getString("tipe");
        String address = rs.getString("address");
        return new Cabang(branch_id, branch_name, tipe, address);
    }

}
