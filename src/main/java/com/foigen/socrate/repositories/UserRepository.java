package com.foigen.socrate.repositories;

import com.foigen.socrate.entities.User;
import com.foigen.socrate.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select c from User c where passportSeries like :series and passportId like :id")
    User findByPassport(String series,String id);
    User findByUsername(String username);
    @Query("select c from User c " +
            "where lower(c.username) like lower(concat('%', :searchTerm, '%')) and role = com.foigen.socrate.enums.Role.ROLE_USER ")
    List<User> search(String searchTerm);
    @Query("select c from User c " +
            "where role = :searchTerm ")
    List<User> findAllByRole(Role searchTerm);

}