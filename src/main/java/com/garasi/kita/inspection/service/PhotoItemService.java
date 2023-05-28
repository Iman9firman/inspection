package com.garasi.kita.inspection.service;

import com.garasi.kita.inspection.model.InspectionDetail;
import com.garasi.kita.inspection.model.PhotoItem;
import com.garasi.kita.inspection.repositories.InspectionRepository;
import com.garasi.kita.inspection.repositories.PhotoItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoItemService {

    private final PhotoItemRepository photoItemRepository;

    public PhotoItemService(PhotoItemRepository photoItemRepository) {
        this.photoItemRepository = photoItemRepository;
    }

    public List<PhotoItem> getData() {
        return photoItemRepository.findAll();
    }

    public PhotoItem saveData(PhotoItem photoItem) {
        return photoItemRepository.save(photoItem);
    }

}