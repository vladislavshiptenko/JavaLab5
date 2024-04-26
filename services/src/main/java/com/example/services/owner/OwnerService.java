package com.example.services.owner;

import com.example.dto.owner.OwnerDto;

import java.util.List;

public interface OwnerService {
    OwnerDto addOwner(OwnerDto ownerDto);
    void deleteOwner(Long id);
    OwnerDto getOwnerById(Long id);
    List<OwnerDto> getOwnersByName(String name);
    void updateOwner(OwnerDto newOwnerDto);
}
