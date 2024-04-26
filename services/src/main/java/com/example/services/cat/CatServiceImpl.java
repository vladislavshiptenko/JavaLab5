package com.example.services.cat;

import com.example.dao.cat.CatDao;
import com.example.dao.owner.OwnerDao;
import com.example.dto.cat.CatDto;
import com.example.entities.cat.Cat;
import com.example.entities.cat.Color;
import com.example.entities.owner.Owner;
import com.example.exceptions.EqualCatIdException;
import com.example.exceptions.WrongCatIdException;
import com.example.exceptions.WrongColorException;
import com.example.exceptions.WrongOwnerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatServiceImpl implements CatService {
    private final CatDao _catDao;
    private final OwnerDao _ownerDao;

    @Autowired
    public CatServiceImpl(CatDao catDao, OwnerDao ownerDao) {
        if (catDao == null) {
            throw new IllegalArgumentException("catDao");
        }

        if (ownerDao == null) {
            throw new IllegalArgumentException("ownerDao");
        }

        _catDao = catDao;
        _ownerDao = ownerDao;
    }

    @Override
    public CatDto addCat(CatDto catDto) throws WrongOwnerException, WrongColorException {
        return catToCatDto(_catDao.save(catDtoToCat(catDto)));
    }

    @Override
    public void deleteCat(Long id) {
        _catDao.deleteById(id);
    }

    @Override
    public CatDto getCatById(Long id) throws WrongColorException {
        Cat cat = _catDao.findById(id).orElse(null);
        if (cat == null) {
            return null;
        }

        return catToCatDto(cat);
    }

    @Override
    public List<CatDto> getCatsByBreed(String breed) {
        return _catDao.findCatsByBreed(breed).stream().map(cat -> {
            try {
                return catToCatDto(cat);
            } catch (WrongColorException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    @Override
    public List<CatDto> getCatsByName(String name) {
        return _catDao.findCatsByName(name).stream().map(cat -> {
            try {
                return catToCatDto(cat);
            } catch (WrongColorException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    @Override
    public List<CatDto> getCatsByColor(String color) throws WrongColorException {
        return _catDao.findCatsByColor(stringToColor(color)).stream().map(cat -> {
            try {
                return catToCatDto(cat);
            } catch (WrongColorException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    @Override
    public List<CatDto> getCatsByOwnerId(Long ownerId) throws WrongOwnerException {
        Owner owner = _ownerDao.findById(ownerId).orElse(null);
        if (owner == null) {
            throw new WrongOwnerException("ownerId");
        }

        return _catDao.findCatsByOwner(owner).stream().map(cat -> {
            try {
                return catToCatDto(cat);
            } catch (WrongColorException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    @Override
    public void updateCat(CatDto newCatDto) throws WrongOwnerException, WrongColorException {
        _catDao.save(catDtoToCat(newCatDto));
    }

    @Override
    public void makeFriends(Long firstCatId, Long secondCatId) throws WrongCatIdException, EqualCatIdException {
        if (firstCatId.longValue() == secondCatId.longValue()) {
            throw new EqualCatIdException(""+firstCatId);
        }

        Optional<Cat> firstCat = _catDao.findById(firstCatId);
        Optional<Cat> secondCat = _catDao.findById(secondCatId);

        if (firstCat.isEmpty()) {
            throw new WrongCatIdException("firstCat");
        }

        if (secondCat.isEmpty()) {
            throw new WrongCatIdException("secondCat");
        }

        Cat cat = firstCat.orElse(new Cat());
        List<Cat> friends = cat.getFriends();
        friends.add(secondCat.orElse(new Cat()));
        cat.setFriends(friends);

        _catDao.save(cat);
    }

    private Color stringToColor(String color) throws WrongColorException {
        if (color.equals("black")) {
            return Color.black;
        }
        else if (color.equals("white")) {
            return Color.white;
        }
        else {
            throw new WrongColorException(color);
        }
    }

    private String colorToString(Color color) throws WrongColorException {
        if (color == Color.black) {
            return "black";
        }
        else if (color == Color.white) {
            return "white";
        }
        else {
            throw new WrongColorException("color");
        }
    }

    private Cat catDtoToCat(CatDto catDto) throws WrongOwnerException, WrongColorException {
        Cat cat = new Cat();

        cat.setId(catDto.getId());

        Owner owner = _ownerDao.findById(catDto.getOwner()).orElse(null);
        if (owner == null) {
            throw new WrongOwnerException(""+catDto.getOwner());
        }

        cat.setOwner(owner);
        cat.setColor(stringToColor(catDto.getColor()));
        cat.setName(catDto.getName());
        cat.setBreed(catDto.getBreed());
        cat.setFriends(catDto.getFriends().stream().map(id -> _catDao.findById(id).orElse(new Cat())).toList());

        return cat;
    }

    private CatDto catToCatDto(Cat cat) throws WrongColorException {
        CatDto catDto = new CatDto();

        catDto.setId(cat.getId());
        catDto.setOwner(cat.getOwner().getId());
        catDto.setBreed(cat.getBreed());
        catDto.setName(cat.getName());
        catDto.setColor(colorToString(cat.getColor()));
        catDto.setFriends(cat.getFriends().stream().map(Cat::getId).toList());

        return catDto;
    }
}
