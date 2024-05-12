package com.example.services.owner;

import com.example.dao.owner.OwnerDao;
import com.example.dto.cat.CatDto;
import com.example.dto.owner.OwnerDto;
import com.example.dto.user.UserDetailsImpl;
import com.example.jpa.cat.Color;
import com.example.jpa.owner.Owner;
import com.example.jpa.cat.Cat;
import com.example.jpa.user.User;
import com.example.jpa.user.UserRole;
import com.example.rabbitmq.OwnerMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OwnerServiceImpl implements OwnerService {
    private final OwnerDao ownerDao;
    private final OwnerMessageSender ownerMessageSender;
    private final OwnerJpaDtoConverter ownerJpaDtoConverter;

    @Autowired
    public OwnerServiceImpl(OwnerDao ownerDao, OwnerMessageSender ownerMessageSender, OwnerJpaDtoConverter ownerJpaDtoConverter) {
        this.ownerDao = ownerDao;
        this.ownerMessageSender = ownerMessageSender;
        this.ownerJpaDtoConverter = ownerJpaDtoConverter;
    }

    @Override
    public OwnerDto addOwner(OwnerDto ownerDto) {
        return ownerJpaDtoConverter.ownerToOwnerDto(ownerDao.save(ownerJpaDtoConverter.ownerDtoToOwner(ownerDto)));
    }

    @Override
    public void deleteOwner(Long id) {
        ownerDao.deleteById(id);
    }

    @Override
    public OwnerDto getOwnerById(Long id) {
        Owner owner = ownerDao.findById(id).orElse(null);

        if (owner == null) {
            return null;
        }

        return ownerJpaDtoConverter.ownerToOwnerDto(owner);
    }

    @Override
    public List<OwnerDto> getOwnersByName(String name) {
        List<Owner> owners = ownerDao.findOwnersByName(name);

        return owners.stream().map(ownerJpaDtoConverter::ownerToOwnerDto).toList();
    }

    @Override
    public void updateOwner(OwnerDto newOwnerDto) {
        ownerDao.save(ownerJpaDtoConverter.ownerDtoToOwner(newOwnerDto));
    }
}
