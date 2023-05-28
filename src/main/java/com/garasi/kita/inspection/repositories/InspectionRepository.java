package com.garasi.kita.inspection.repositories;

import com.garasi.kita.inspection.model.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Integer> {

}