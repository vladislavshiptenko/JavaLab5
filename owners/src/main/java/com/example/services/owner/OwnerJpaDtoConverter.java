package com.example.services.owner;

import com.example.dao.owner.OwnerDao;
import com.example.dto.cat.CatDto;
import com.example.dto.owner.OwnerDto;
import com.example.dto.user.UserDetailsImpl;
import com.example.dto.user.UserDto;
import com.example.jpa.cat.Cat;
import com.example.jpa.cat.Color;
import com.example.jpa.owner.Owner;
import com.example.jpa.user.User;
import com.example.jpa.user.UserRole;
import com.example.rabbitmq.OwnerMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OwnerJpaDtoConverter {
    private final OwnerDao ownerDao;
    private final OwnerMessageSender ownerMessageSender;

    @Autowired
    public OwnerJpaDtoConverter(OwnerDao ownerDao, OwnerMessageSender ownerMessageSender) {
        this.ownerDao = ownerDao;
        this.ownerMessageSender = ownerMessageSender;
    }

    public Owner ownerDtoToOwner(OwnerDto ownerDto) {
        Owner owner = new Owner();

        owner.setId(ownerDto.getId());
        owner.setName(ownerDto.getName());
        owner.setBirthday(ownerDto.getBirthday());
        owner.setUser(userDtoToUser(ownerMessageSender.getUserById(ownerDto.getUserId())));

        return owner;
    }

    public Cat catDtoToCat(CatDto catDto) {
        Cat cat = new Cat();

        cat.setId(catDto.getId());

        Optional<Owner> owner = ownerDao.findById(catDto.getOwner());
        if (owner.isEmpty()) {
            throw new RuntimeException(""+catDto.getOwner());
        }

        cat.setOwner(owner.orElse(new Owner()));
        cat.setColor(Color.valueOf(catDto.getColor()));
        cat.setName(catDto.getName());
        cat.setBreed(catDto.getBreed());

        return cat;
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
