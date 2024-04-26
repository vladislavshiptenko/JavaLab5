package com.example.services.owner;

import com.example.dao.cat.CatDao;
import com.example.dao.owner.OwnerDao;
import com.example.dao.user.UserDao;
import com.example.dto.owner.OwnerDto;
import com.example.entities.cat.Cat;
import com.example.entities.owner.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerServiceImpl implements OwnerService {
    private final OwnerDao _ownerDao;
    private final CatDao _catDao;
    private final UserDao userDao;

    @Autowired
    public OwnerServiceImpl(OwnerDao ownerDao, CatDao catDao, UserDao userDao) {
        this.userDao = userDao;
        if (ownerDao == null) {
            throw new IllegalArgumentException("ownerDao");
        }

        if (catDao == null) {
            throw new IllegalArgumentException("catDao");
        }

        _ownerDao = ownerDao;
        _catDao = catDao;
    }

    @Override
    public OwnerDto addOwner(OwnerDto ownerDto) {
        return ownerToOwnerDto(_ownerDao.save(ownerDtoToOwner(ownerDto)));
    }

    @Override
    public void deleteOwner(Long id) {
        _ownerDao.deleteById(id);
    }

    @Override
    public OwnerDto getOwnerById(Long id) {
        Owner owner = _ownerDao.findById(id).orElse(null);

        if (owner == null) {
            return null;
        }

        return ownerToOwnerDto(owner);
    }

    @Override
    public List<OwnerDto> getOwnersByName(String name) {
        List<Owner> owners = _ownerDao.findOwnersByName(name);

        return owners.stream().map(this::ownerToOwnerDto).toList();
    }

    @Override
    public void updateOwner(OwnerDto newOwnerDto) {
        _ownerDao.save(ownerDtoToOwner(newOwnerDto));
    }

    private Owner ownerDtoToOwner(OwnerDto ownerDto) {
        Owner owner = new Owner();

        owner.setId(ownerDto.getId());
        owner.setName(ownerDto.getName());
        owner.setBirthday(ownerDto.getBirthday());
        owner.setCats(ownerDto.getCats().stream().map(id -> _catDao.findById(id).orElse(new Cat())).toList());
        owner.setUser(userDao.findById(ownerDto.getUserId()).orElseThrow(() -> new RuntimeException("User wasn't found")));

        return owner;
    }

    private OwnerDto ownerToOwnerDto(Owner owner) {
        OwnerDto ownerDto = new OwnerDto();

        ownerDto.setId(owner.getId());
        ownerDto.setBirthday(owner.getBirthday());
        ownerDto.setName(owner.getName());
        ownerDto.setCats(owner.getCats().stream().map(Cat::getId).toList());
        ownerDto.setUserId(owner.getUser().getId());

        return ownerDto;
    }
}
