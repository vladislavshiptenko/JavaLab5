package com.example.services.cat;

import com.example.dao.cat.CatDao;
import com.example.dto.cat.CatDto;
import com.example.dto.owner.OwnerDto;
import com.example.jpa.cat.Cat;
import com.example.jpa.cat.Color;
import com.example.jpa.owner.Owner;
import com.example.rabbitmq.CatMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatServiceImpl implements CatService {
    private final CatDao catDao;
    private final CatMessageSender catMessageSender;
    private final CatJpaDtoConverter catJpaDtoConverter;

    @Autowired
    public CatServiceImpl(CatDao catDao, CatMessageSender catMessageSender, CatJpaDtoConverter catJpaDtoConverter) {
        this.catMessageSender = catMessageSender;
        this.catDao = catDao;
        this.catJpaDtoConverter = catJpaDtoConverter;
    }

    @Override
    public CatDto addCat(CatDto catDto) {
        return catJpaDtoConverter.catToCatDto(catDao.save(catJpaDtoConverter.catDtoToCat(catDto)));
    }

    @Override
    public void deleteCat(Long id) {
        catDao.deleteById(id);
    }

    @Override
    public CatDto getCatById(Long id) {
        Cat cat = catDao.findById(id).orElse(null);
        if (cat == null) {
            return null;
        }

        return catJpaDtoConverter.catToCatDto(cat);
    }

    @Override
    public List<CatDto> getCatsByBreed(String breed) {
        return catDao.findCatsByBreed(breed).stream().map(catJpaDtoConverter::catToCatDto).toList();
    }

    @Override
    public List<CatDto> getCatsByName(String name) {
        return catDao.findCatsByName(name).stream().map(catJpaDtoConverter::catToCatDto).toList();
    }

    @Override
    public List<CatDto> getCatsByColor(String color) {
        return catDao.findCatsByColor(Color.valueOf(color)).stream().map(catJpaDtoConverter::catToCatDto).toList();
    }

    @Override
    public List<CatDto> getCatsByOwnerId(Long ownerId) {
        Owner owner = catJpaDtoConverter.ownerDtoToOwner(catMessageSender.getOwnerById(ownerId));

        return catDao.findCatsByOwner(owner).stream().map(catJpaDtoConverter::catToCatDto).toList();
    }

    @Override
    public void updateCat(CatDto newCatDto) {
        catDao.save(catJpaDtoConverter.catDtoToCat(newCatDto));
    }

    @Override
    public void makeFriends(Long firstCatId, Long secondCatId) {

    }

//    @Override
//    public void makeFriends(Long firstCatId, Long secondCatId) {
//        if (firstCatId.longValue() == secondCatId.longValue()) {
//            throw new RuntimeException(""+firstCatId);
//        }
//
//        Optional<Cat> firstCat = catDao.findById(firstCatId);
//        Optional<Cat> secondCat = catDao.findById(secondCatId);
//
//        if (firstCat.isEmpty()) {
//            throw new IllegalArgumentException("firstCat");
//        }
//
//        if (secondCat.isEmpty()) {
//            throw new IllegalArgumentException("secondCat");
//        }
//
//        Cat cat = firstCat.orElse(new Cat());
//        List<Cat> friends = cat.getFriends();
//        friends.add(secondCat.orElse(new Cat()));
//        cat.setFriends(friends);
//
//        catDao.save(cat);
//    }
}
