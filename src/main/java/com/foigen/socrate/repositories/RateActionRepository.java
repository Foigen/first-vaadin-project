package com.foigen.socrate.repositories;

import com.foigen.socrate.entities.RateAction;
import com.foigen.socrate.entities.RateCondition;
import com.foigen.socrate.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RateActionRepository extends JpaRepository<RateAction,Long> {
    @Query("select c from RateAction c " +
            "where lower(c.title) like lower(concat('%', :searchTerm, '%')) and c.isTemplate= true ")
    List<RateAction> searchTemplates(String searchTerm);

    @Query("select c from RateAction c where c.isTemplate=true")
    List<RateAction> findAllTemplates();

    List<RateAction> findAllByUser(User user);
}
