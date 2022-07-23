package com.foigen.socrate.services;

import com.foigen.socrate.entities.RateCondition;
import com.foigen.socrate.repositories.RateConditionRepository;
import com.foigen.socrate.repositories.RateModRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConditionService {
    private final RateConditionRepository conditionRepository;
    private final RateModRepository modRepository;

    public ConditionService(
            RateConditionRepository conditionRepository,
            RateModRepository modRepository) {
        this.conditionRepository = conditionRepository;
        this.modRepository = modRepository;
    }

    public Long getConditionsCount() {
        return conditionRepository.count();
    }

    public List<RateCondition> findAllConditions(String stringFilter) {
        if(stringFilter==null||stringFilter.isEmpty())
            return conditionRepository.findAll();
        return conditionRepository.search(stringFilter);
    }

    public boolean save(RateCondition condition){
        if(conditionRepository.findByTitle(condition.getTitle()).isPresent()) return false;
        System.out.println(condition.getMods());
        conditionRepository.save(condition);
        return true;
    }
    public void delete(RateCondition condition){
        conditionRepository.delete(condition);
    }
}
