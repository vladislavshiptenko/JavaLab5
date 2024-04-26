package com.example.controller.owner;

import com.example.dto.owner.OwnerDto;
import com.example.dto.user.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.services.owner.OwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {
    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        if (ownerService == null) {
            throw new IllegalArgumentException("ownerService");
        }

        this.ownerService = ownerService;
    }

    @PostMapping("/addOwner")
    public OwnerDto addOwner(@Valid @RequestBody OwnerDto ownerDto) {
        ownerDto.setUserId(getUserDetails().getId());
        return ownerService.addOwner(ownerDto);
    }

    @DeleteMapping("/deleteOwner/{id}")
    public ResponseEntity<String> deleteOwner(@PathVariable Long id) {
        if (userIsAdmin() || getUserDetails().getId() == ownerService.getOwnerById(id).getUserId()) {
            ownerService.deleteOwner(id);
            return ResponseEntity.ok("Owner with id "+id+" was deleted");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/updateOwner")
    public ResponseEntity<String> updateOwner(@Valid @RequestBody OwnerDto newOwnerDto) {
        newOwnerDto.setUserId(ownerService.getOwnerById(newOwnerDto.getId()).getUserId());

        if (userIsAdmin() || getUserDetails().getId() == newOwnerDto.getUserId()) {
            ownerService.updateOwner(newOwnerDto);
            return ResponseEntity.ok("Owner with id "+newOwnerDto.getId()+" was updated");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/getOwnerById/{id}")
    public ResponseEntity<OwnerDto> getOwnerById(@PathVariable Long id) {
        OwnerDto ownerDto = ownerService.getOwnerById(id);

        if (ownerDto == null) {
            return ResponseEntity.notFound().build();
        }

        if (ownerDto.getUserId() != getUserDetails().getId() && !userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(ownerDto);
    }

    @GetMapping("/getOwnersByName/{name}")
    public List<OwnerDto> getOwnersByName(@PathVariable String name) {
        List<OwnerDto> ownerDtoList = ownerService.getOwnersByName(name);
        if (userIsAdmin()) {
            return ownerDtoList;
        }

        return ownerDtoList.stream().filter(ownerDto -> ownerDto.getUserId() == getUserDetails().getId()).toList();
    }

    private UserDetailsImpl getUserDetails() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean userIsAdmin() {
        return getUserDetails().getRole().equals("ROLE_ADMIN");
    }
}
