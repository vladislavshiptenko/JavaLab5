package com.example.services.cat;

import com.example.dto.cat.CatDto;

import java.util.List;

public interface CatService {
    CatDto addCat(CatDto catDto);
    void deleteCat(Long id);
    CatDto getCatById(Long id);
    List<CatDto> getCatsByBreed(String breed);
    List<CatDto> getCatsByName(String name);
    List<CatDto> getCatsByColor(String color);
    List<CatDto> getCatsByOwnerId(Long ownerId);
    void updateCat(CatDto newCatDto);
    void makeFriends(Long firstCatId, Long secondCatId);
}
