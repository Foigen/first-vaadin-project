package com.foigen.socrate.services;

import com.foigen.socrate.entities.RateAction;
import com.foigen.socrate.entities.RateMod;
import com.foigen.socrate.entities.User;
import com.foigen.socrate.repositories.RateActionRepository;
import com.foigen.socrate.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActionService {
    private final RateActionRepository rateActionRepository;
    private final UserRepository userRepository;

    public ActionService(RateActionRepository rateActionRepository, UserRepository userRepository) {
        this.rateActionRepository = rateActionRepository;
        this.userRepository = userRepository;
    }

    public void executeRateAct(RateAction action){
        var user=action.getUser();
        var finalMod=1.0;
        var accrual= action.getRate() > 0;
        var mods= action.getUser().getConditions().stream()
                .flatMap(condition -> condition.getMods().stream())
                .map(RateMod::getMod)
                .filter(value -> (value >= 0 && accrual) || (value < 0 && !accrual))
                .sorted((val1, val2) -> val1 > val2 ? 1 : 0).collect(Collectors.toList());
        if(mods.size()!=0) finalMod=mods.get(mods.size()/2);
        System.err.println(finalMod);

        action.setRate((int) (action.getRate()*Math.abs(finalMod)));
        action.setIsTemplate(false);
        action.setDate(new Date());
        rateActionRepository.save(action);
        System.out.println("old rate "+user.getRate());
        user.increaseRate(action.getRate());
        userRepository.save(user);
        System.out.println("new rate "+user.getRate());
    }

    public List<RateAction> findAllTemplates(String stringFilter){
        if(stringFilter==null||stringFilter.isEmpty()){
            return rateActionRepository.findAllTemplates();
        }
        return rateActionRepository.searchTemplates(stringFilter);
    }
    public List<RateAction> findAllTemplates() {
        return rateActionRepository.findAllTemplates();
    }

    public void saveTemplate(RateAction template){
        template.setIsTemplate(true);
        rateActionRepository.save(template);
    }
    public List<RateAction> findAllActionsByUser(User user){
        return rateActionRepository.findAllByUser(user);
    }
    public void delete(RateAction action){
        rateActionRepository.delete(action);
    }
}
