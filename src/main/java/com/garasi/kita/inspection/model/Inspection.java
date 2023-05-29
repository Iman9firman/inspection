package com.garasi.kita.inspection.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class Inspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String kodeBooking;
    String nomorPolisi;
    String merk;
    String model;
    String paket;
    String price;
    String customer;
    String phoneNumber;
    String lokasi;
    String kota;
    String tanggal;
    int status = 0;
    String inspektor;
    String createAt;

}
