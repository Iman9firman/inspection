package com.garasi.kita.inspection.DAO;

import com.garasi.kita.inspection.model.Inspection;
import com.garasi.kita.inspection.model.InspectionDetail;

import java.util.ArrayList;
import java.util.List;

public interface RepoDao {
    String loginQuery(String username, String password);

    List<Inspection> listTask(String username);

    Inspection getDataInspection(String kode);

    List<InspectionDetail> getDatainpectionDetailService(String kode);

    List<String> getDataInspectionDetailPhoto(String kodeBooking, String idField);

    void removePhoto(String name);

    int update(InspectionDetail data);

}
