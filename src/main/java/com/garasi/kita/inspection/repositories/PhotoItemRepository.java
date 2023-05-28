package com.garasi.kita.inspection.repositories;

import com.garasi.kita.inspection.model.InspectionDetail;
import com.garasi.kita.inspection.model.PhotoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoItemRepository extends JpaRepository<PhotoItem, Integer> {

}