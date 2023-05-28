package com.garasi.kita.inspection.service;

import com.garasi.kita.inspection.model.Inspection;
import com.garasi.kita.inspection.model.InspectionDetail;
import com.garasi.kita.inspection.repositories.InspectionDetailRepository;
import com.garasi.kita.inspection.repositories.InspectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InpectionDetailService {

    private final InspectionDetailRepository inspectionDetailRepository;

    public InpectionDetailService(InspectionDetailRepository inspectionDetailRepository) {
        this.inspectionDetailRepository = inspectionDetailRepository;
    }

    public List<InspectionDetail> getData() {
        return inspectionDetailRepository.findAll();
    }

    public InspectionDetail saveData(InspectionDetail inspection) {
        return inspectionDetailRepository.save(inspection);
    }

}