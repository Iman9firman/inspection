package com.garasi.kita.inspection.model;

import lombok.Data;
import netscape.javascript.JSObject;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class InspectionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String kodeBooking;
    String idField;
    String label;
    String value;
    String photo;
}
