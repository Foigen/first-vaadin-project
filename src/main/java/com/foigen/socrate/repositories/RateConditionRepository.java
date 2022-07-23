package com.foigen.socrate.repositories;

import com.foigen.socrate.entities.RateCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RateConditionRepository extends JpaRepository<RateCondition, Long> {
    @Query("select c from RateCondition c " +
            "where lower(c.title) like lower(concat('%', :searchTerm, '%')) ")
    List<RateCondition> search(String searchTerm);

    Optional<RateCondition> findByTitle(String title);
}
