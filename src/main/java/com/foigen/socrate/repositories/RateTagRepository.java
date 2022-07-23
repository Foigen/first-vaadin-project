package com.foigen.socrate.repositories;

import com.foigen.socrate.entities.RateTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RateTagRepository extends JpaRepository<RateTag,Long> {
    @Query("select c from RateTag c " +
            "where lower(c.name) like lower(concat('%', :name, '%'))")
    List<RateTag> search(String name);
}
