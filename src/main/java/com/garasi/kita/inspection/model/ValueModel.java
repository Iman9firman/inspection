package com.garasi.kita.inspection.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ValueModel {
    private String value;
    private ArrayList<String> option;

    public ValueModel() {
    }
}
