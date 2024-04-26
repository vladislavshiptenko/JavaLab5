package com.example.dao.cat;

import com.example.entities.cat.Cat;
import com.example.entities.cat.Color;
import com.example.entities.owner.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatDao extends JpaRepository<Cat, Long> {
    List<Cat> findCatsByBreed(String breed);
    List<Cat> findCatsByName(String name);
    List<Cat> findCatsByColor(Color color);
    List<Cat> findCatsByOwner(Owner owner);
}
