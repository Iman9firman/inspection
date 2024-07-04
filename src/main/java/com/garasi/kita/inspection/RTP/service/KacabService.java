package com.garasi.kita.inspection.RTP.service;


import com.garasi.kita.inspection.RTP.model.Kacab;
import com.garasi.kita.inspection.RTP.repositories.KacabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KacabService {

    @Autowired
    private KacabRepository kacabRepository;

    public List<Kacab> getAllKacabs() {
        return kacabRepository.findAll();
    }

    public Optional<Kacab> getKacabById(Long id) {
        return kacabRepository.findById(id);
    }

    public Kacab saveKacab(Kacab kacab) {
        return kacabRepository.save(kacab);
    }

    public Kacab updateKacab(Long id, Kacab kacabDetails) {
        Kacab kacab = kacabRepository.findById(id).orElseThrow(() -> new RuntimeException("Kacab not found"));
        kacab.setBranch_name(kacabDetails.getBranch_name());
        kacab.setTipe(kacabDetails.getTipe());
        kacab.setAddress(kacabDetails.getAddress());
        return kacabRepository.save(kacab);
    }

    public void deleteKacab(Long id) {
        kacabRepository.deleteById(id);
    }
}