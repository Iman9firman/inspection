package com.garasi.kita.inspection.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String content;
    String uuid;
    String participant;
    String kode_booking;
    String url;
    String status = "1";
    Date create_at;
    String create_by;
}
