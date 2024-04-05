package com.garasi.kita.inspection.repositories;

import com.garasi.kita.inspection.model.City;
import com.garasi.kita.inspection.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> { }