package com.garasi.kita.inspection.DAO;

import com.garasi.kita.inspection.model.Inspection;
import com.garasi.kita.inspection.model.InspectionDetail;
import com.garasi.kita.inspection.model.PhotoItem;

import java.util.ArrayList;
import java.util.List;

public interface RepoDao {
    String loginQuery(String username, String password);

    List<Inspection> listTask(String username);

    Inspection getDataInspection(String kode);

    List<InspectionDetail> getDatainpectionDetailService(String kode);

    List<PhotoItem> getDataInspectionDetailPhoto(String kodeBooking, String idField);

    List<PhotoItem> getDataInspectionDetailPhoto(String kodeBooking);

    void removePhoto(String name);

    int update(InspectionDetail data);

    int updateInspection(String data, int status);

    List<String> getGenerateReport();

    List<Inspection> listHistoryTask(String username, String startdate, String endDate);
}

