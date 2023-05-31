package com.garasi.kita.inspection.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;

@Data
@EqualsAndHashCode(callSuper=false)
public class InspectionDetailPhoto extends InspectionDetail {
    ArrayList<String> photo;
}
