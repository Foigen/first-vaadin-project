package com.foigen.socrate.repositories;

import com.foigen.socrate.entities.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {
    @Query("select c from Benefit c " +
            "where lower(c.name) like lower(concat('%', :searchTerm, '%')) ")
    List<Benefit> search(String searchTerm);

    @Query("select c from Benefit c where higher=true and thenRate < :rate" +
            " or higher=false and thenRate > :rate")
    List<Benefit> findByRateValidation(Integer rate);
}
