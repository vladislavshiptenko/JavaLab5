package com.example.services.cat;

import com.example.dto.cat.CatDto;
import com.example.exceptions.EqualCatIdException;
import com.example.exceptions.WrongCatIdException;
import com.example.exceptions.WrongColorException;
import com.example.exceptions.WrongOwnerException;

import java.util.List;

public interface CatService {
    CatDto addCat(CatDto catDto) throws WrongOwnerException, WrongColorException;
    void deleteCat(Long id);
    CatDto getCatById(Long id) throws WrongColorException;
    List<CatDto> getCatsByBreed(String breed);
    List<CatDto> getCatsByName(String name);
    List<CatDto> getCatsByColor(String color) throws WrongColorException;
    List<CatDto> getCatsByOwnerId(Long ownerId) throws WrongOwnerException;
    void updateCat(CatDto newCatDto) throws WrongOwnerException, WrongColorException;
    void makeFriends(Long firstCatId, Long secondCatId) throws WrongCatIdException, EqualCatIdException;
}
