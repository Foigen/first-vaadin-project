package com.foigen.socrate.services;

import com.foigen.socrate.entities.RateMod;
import com.foigen.socrate.repositories.RateModRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModService {
    private final RateModRepository repository;

    public ModService(RateModRepository repository) {
        this.repository = repository;
    }
    public Long getConditionsCount(){
        return repository.count();
    }
    public List<RateMod> findAllMods(){
        return repository.findAll();
    }
}
