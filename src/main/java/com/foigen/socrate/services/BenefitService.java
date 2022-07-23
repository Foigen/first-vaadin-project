package com.foigen.socrate.services;

import com.foigen.socrate.entities.Benefit;
import com.foigen.socrate.entities.User;
import com.foigen.socrate.repositories.BenefitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BenefitService {

    private final BenefitRepository benefitRepository;

    public BenefitService(BenefitRepository benefitRepository) {
        this.benefitRepository = benefitRepository;
    }

    public List<Benefit> findAllBenefits(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty())
            return benefitRepository.findAll();
        return benefitRepository.search(stringFilter);
    }

    public long benefitsCount() {
        return benefitRepository.count();
    }

    public void deleteBenefit(Benefit benefit) {
        benefitRepository.delete(benefit);
    }

    public void saveBenefit(Benefit benefit) {
        if (benefit == null) {
            System.err.println("Benefit is null. Are you sure you have connected your form to the application?");
            return;
        }
        benefitRepository.save(benefit);
    }

    public List<Benefit> findBenefitsByUserValidation(User user){
        return benefitRepository.findByRateValidation(user.getRate());
    }
}