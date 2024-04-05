package com.garasi.kita.inspection.service;

import com.garasi.kita.inspection.model.City;
import com.garasi.kita.inspection.model.Inspection;
import com.garasi.kita.inspection.model.Inspector;
import com.garasi.kita.inspection.model.Message;
import com.garasi.kita.inspection.repositories.CityRepository;
import com.garasi.kita.inspection.repositories.InspectionRepository;
import com.garasi.kita.inspection.repositories.InspectorRepository;
import com.garasi.kita.inspection.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InpectionService {

    @Autowired
    InspectionRepository inspectionRepository;

    @Autowired
    InspectorRepository repository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    MessageRepository messageRepository;

    public List<Inspection> getData() {
        return inspectionRepository.findAll();
    }

    public List<Message> getDataMessage() {
        return messageRepository.findAll();
    }

    public Inspection saveData(Inspection inspection) {
        return inspectionRepository.save(inspection);
    }

    public List<Inspector> getInspector() {
        return repository.findAll();
    }

    public List<City> getCity() {
        return cityRepository.findAll();
    }

    public Message saveData(Message message) {
        return messageRepository.save(message);
    }

}