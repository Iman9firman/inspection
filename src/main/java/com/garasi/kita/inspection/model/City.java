package com.garasi.kita.inspection.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String name;
    String detail;
    String status;
    Timestamp createAt;
    String createBy;
    String updateAt;
    Timestamp updateBy;
}
