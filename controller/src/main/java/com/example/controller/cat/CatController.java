package com.example.controller.cat;

import com.example.dto.cat.CatDto;
import com.example.dto.user.UserDetailsImpl;
import com.example.exceptions.EqualCatIdException;
import com.example.exceptions.WrongCatIdException;
import com.example.exceptions.WrongColorException;
import com.example.exceptions.WrongOwnerException;
import com.example.services.owner.OwnerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.services.cat.CatService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/cats")
public class CatController {
    private final CatService catService;
    private final OwnerService ownerService;

    @Autowired
    public CatController(CatService catService, OwnerService ownerService) {
        this.catService = catService;
        this.ownerService = ownerService;
    }

    @PostMapping("/addCat")
    public ResponseEntity<CatDto> addCat(@Valid @RequestBody CatDto catDto) throws WrongOwnerException, WrongColorException {
        if (ownerService.getOwnerById(catDto.getOwner()).getUserId() != getUserDetails().getId() && !userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(catService.addCat(catDto));
    }

    @DeleteMapping("/deleteCat/{id}")
    public ResponseEntity<String> deleteCat(@PathVariable Long id) throws WrongColorException {
        if (ownerService.getOwnerById(catService.getCatById(id).getOwner()).getUserId() == getUserDetails().getId() || userIsAdmin()) {
            catService.deleteCat(id);
            return ResponseEntity.ok("Cat with id "+id+" was deleted");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/updateCat")
    public ResponseEntity<String> updateCat(@Valid @RequestBody CatDto newCatDto) throws WrongOwnerException, WrongColorException {
        if (ownerService.getOwnerById(newCatDto.getOwner()).getUserId() != getUserDetails().getId() && !userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        catService.updateCat(newCatDto);
        return ResponseEntity.ok("Cat with id "+newCatDto.getId()+" was updated");
    }

    @GetMapping("/getCatById/{id}")
    public ResponseEntity<CatDto> getCatById(@PathVariable long id) throws WrongColorException {
        CatDto catDto = catService.getCatById(id);

        if (catDto == null) {
            return ResponseEntity.notFound().build();
        }

        if (ownerService.getOwnerById(catDto.getOwner()).getUserId() != getUserDetails().getId() && !userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(catDto);
    }

    @PutMapping("/makeFriends/{firstCatId}, {secondCatId}")
    public ResponseEntity<String> makeFriends(@PathVariable Long firstCatId, @PathVariable Long secondCatId) throws WrongCatIdException, EqualCatIdException {
        if (!userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        catService.makeFriends(firstCatId, secondCatId);
        return ResponseEntity.ok("Cats with id "+firstCatId+" and "+secondCatId+" are friends now");
    }

    @GetMapping("/getCatsByBreed/{breed}")
    public List<CatDto> getCatsByBreed(@PathVariable String breed) {
        List<CatDto> catDtoList = catService.getCatsByBreed(breed);

        if (userIsAdmin()) {
            return catDtoList;
        }

        return catDtoList.stream().filter(catDto -> ownerService.getOwnerById(catDto.getOwner()).getUserId() == getUserDetails().getId()).toList();
    }

    @GetMapping("/getCatsByName/{name}")
    public List<CatDto> getCatsByName(@PathVariable String name) {
        List<CatDto> catDtoList = catService.getCatsByName(name);

        if (userIsAdmin()) {
            return catDtoList;
        }

        return catDtoList.stream().filter(catDto -> ownerService.getOwnerById(catDto.getOwner()).getUserId() == getUserDetails().getId()).toList();
    }

    @GetMapping("/getCatsByColor/{color}")
    public List<CatDto> getCatsByColor(@PathVariable String color) throws WrongColorException {
        List<CatDto> catDtoList = catService.getCatsByColor(color);

        if (userIsAdmin()) {
            return catDtoList;
        }

        return catDtoList.stream().filter(catDto -> ownerService.getOwnerById(catDto.getOwner()).getUserId() == getUserDetails().getId()).toList();
    }

    @GetMapping("/getCatsByOwnerId/{ownerId}")
    public List<CatDto> getCatsByOwner(@PathVariable Long ownerId) throws WrongOwnerException {
        List<CatDto> catDtoList = catService.getCatsByOwnerId(ownerId);

        if (userIsAdmin()) {
            return catDtoList;
        }

        return catDtoList.stream().filter(catDto -> ownerService.getOwnerById(catDto.getOwner()).getUserId() == getUserDetails().getId()).toList();
    }

    private UserDetailsImpl getUserDetails() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean userIsAdmin() {
        return getUserDetails().getRole().equals("ROLE_ADMIN");
    }
}
