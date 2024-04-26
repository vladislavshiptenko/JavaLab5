package com.example.dao.owner;

import com.example.entities.owner.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnerDao extends JpaRepository<Owner, Long> {
    List<Owner> findOwnersByName(String name);
}
