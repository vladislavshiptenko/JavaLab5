package com.example.controller.cat;

import com.example.dto.cat.CatDto;
import com.example.dto.user.UserDetailsImpl;
import com.example.rabbitmq.ControllerMessageSender;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cats")
public class CatController {
    private final ControllerMessageSender controllerMessageSender;

    @Autowired
    public CatController(ControllerMessageSender controllerMessageSender) {
        this.controllerMessageSender = controllerMessageSender;
    }

    @PostMapping("/addCat")
    public ResponseEntity<CatDto> addCat(@Valid @RequestBody CatDto catDto) {
        if (controllerMessageSender.getOwnerById(catDto.getOwner()).getUserId() != getUserDetails().getId() && !userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(controllerMessageSender.addCat(catDto));
    }

    @DeleteMapping("/deleteCat/{id}")
    public ResponseEntity<String> deleteCat(@PathVariable Long id) {
        if (controllerMessageSender.getOwnerById(controllerMessageSender.getCatById(id).getOwner()).getUserId() == getUserDetails().getId() || userIsAdmin()) {
            controllerMessageSender.deleteCat(id);
            return ResponseEntity.ok("Cat with id "+id+" was deleted");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/updateCat")
    public ResponseEntity<String> updateCat(@Valid @RequestBody CatDto newCatDto) {
        if (controllerMessageSender.getOwnerById(newCatDto.getOwner()).getUserId() != getUserDetails().getId() && !userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        controllerMessageSender.updateCat(newCatDto);
        return ResponseEntity.ok("Cat with id "+newCatDto.getId()+" was updated");
    }

    @GetMapping("/getCatById/{id}")
    public ResponseEntity<CatDto> getCatById(@PathVariable long id) {
        CatDto catDto = controllerMessageSender.getCatById(id);

        if (catDto == null) {
            return ResponseEntity.notFound().build();
        }

        if (controllerMessageSender.getOwnerById(catDto.getOwner()).getUserId() != getUserDetails().getId() && !userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(catDto);
    }

    @GetMapping("/getCatsByBreed/{breed}")
    public List<CatDto> getCatsByBreed(@PathVariable String breed) {
        List<CatDto> catDtoList = controllerMessageSender.getCatsByBreed(breed);

        if (userIsAdmin()) {
            return catDtoList;
        }

        return catDtoList.stream().filter(catDto -> controllerMessageSender.getOwnerById(catDto.getOwner()).getUserId() == getUserDetails().getId()).toList();
    }

    @GetMapping("/getCatsByName/{name}")
    public List<CatDto> getCatsByName(@PathVariable String name) {
        List<CatDto> catDtoList = controllerMessageSender.getCatsByName(name);

        if (userIsAdmin()) {
            return catDtoList;
        }

        return catDtoList.stream().filter(catDto -> controllerMessageSender.getOwnerById(catDto.getOwner()).getUserId() == getUserDetails().getId()).toList();
    }

    @GetMapping("/getCatsByColor/{color}")
    public List<CatDto> getCatsByColor(@PathVariable String color) {
        List<CatDto> catDtoList = controllerMessageSender.getCatsByColor(color);

        if (userIsAdmin()) {
            return catDtoList;
        }

        return catDtoList.stream().filter(catDto -> controllerMessageSender.getOwnerById(catDto.getOwner()).getUserId() == getUserDetails().getId()).toList();
    }

    @GetMapping("/getCatsByOwnerId/{ownerId}")
    public List<CatDto> getCatsByOwner(@PathVariable Long ownerId) {
        List<CatDto> catDtoList = controllerMessageSender.getCatsByOwnerId(ownerId);

        if (userIsAdmin()) {
            return catDtoList;
        }

        return catDtoList.stream().filter(catDto -> controllerMessageSender.getOwnerById(catDto.getOwner()).getUserId() == getUserDetails().getId()).toList();
    }

    private UserDetailsImpl getUserDetails() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean userIsAdmin() {
        return getUserDetails().getRole().equals("ROLE_ADMIN");
    }
}
