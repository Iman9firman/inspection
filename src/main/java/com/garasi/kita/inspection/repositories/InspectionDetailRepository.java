package com.garasi.kita.inspection.repositories;

import com.garasi.kita.inspection.model.InspectionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionDetailRepository extends JpaRepository<InspectionDetail, Integer> {

}