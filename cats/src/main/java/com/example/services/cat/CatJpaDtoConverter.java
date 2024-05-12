package com.example.services.cat;

import com.example.dao.cat.CatDao;
import com.example.dto.cat.CatDto;
import com.example.dto.owner.OwnerDto;
import com.example.dto.user.UserDetailsImpl;
import com.example.dto.user.UserDto;
import com.example.jpa.cat.Cat;
import com.example.jpa.cat.Color;
import com.example.jpa.owner.Owner;
import com.example.jpa.user.User;
import com.example.jpa.user.UserRole;
import com.example.rabbitmq.CatMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CatJpaDtoConverter {
    private final CatDao catDao;
    private final CatMessageSender catMessageSender;

    @Autowired
    public CatJpaDtoConverter(CatDao catDao, CatMessageSender catMessageSender) {
        this.catDao = catDao;
        this.catMessageSender = catMessageSender;
    }

    public Cat catDtoToCat(CatDto catDto) {
        Cat cat = new Cat();

        cat.setId(catDto.getId());

        OwnerDto ownerDto = catMessageSender.getOwnerById(catDto.getOwner());
        if (ownerDto == null) {
            throw new RuntimeException(""+catDto.getOwner());
        }

        Owner owner = ownerDtoToOwner(ownerDto);

        cat.setOwner(owner);
        cat.setColor(Color.valueOf(catDto.getColor()));
        cat.setName(catDto.getName());
        cat.setBreed(catDto.getBreed());

        return cat;
    }

    public CatDto catToCatDto(Cat cat) {
        CatDto catDto = new CatDto();

        catDto.setId(cat.getId());
        catDto.setOwner(cat.getOwner().getId());
        catDto.setBreed(cat.getBreed());
        catDto.setName(cat.getName());
        catDto.setColor(cat.getColor().name());

        return catDto;
    }

    public Owner ownerDtoToOwner(OwnerDto ownerDto) {
        Owner owner = new Owner();

        owner.setId(ownerDto.getId());
        owner.setName(ownerDto.getName());
        owner.setBirthday(ownerDto.getBirthday());
        owner.setUser(userDtoToUser(catMessageSender.getUserById(ownerDto.getUserId())));

        return owner;
    }

    public OwnerDto ownerToOwnerDto(Owner owner) {
        OwnerDto ownerDto = new OwnerDto();

        ownerDto.setId(owner.getId());
        ownerDto.setBirthday(owner.getBirthday());
        ownerDto.setName(owner.getName());
        ownerDto.setUserId(owner.getUser().getId());

        return ownerDto;
    }

    public User userDtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setRole(UserRole.valueOf(userDto.getRole()));
        user.setUsername(userDto.getUsername());

        return user;
    }
}
