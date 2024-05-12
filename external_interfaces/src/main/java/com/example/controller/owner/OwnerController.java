package com.example.controller.owner;

import com.example.dto.owner.OwnerDto;
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
@RequestMapping("/api/owners")
public class OwnerController {
    private final ControllerMessageSender controllerMessageSender;

    @Autowired
    public OwnerController(ControllerMessageSender controllerMessageSender) {
        this.controllerMessageSender = controllerMessageSender;
    }

    @PostMapping("/addOwner")
    public ResponseEntity<OwnerDto> addOwner(@Valid @RequestBody OwnerDto ownerDto) {
        ownerDto.setUserId(getUserDetails().getId());
        OwnerDto response = controllerMessageSender.addOwner(ownerDto);

        if (response != null) {
            return ResponseEntity.ok(response);
        } else  {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/deleteOwner/{id}")
    public ResponseEntity<String> deleteOwner(@PathVariable Long id) {
        if (userIsAdmin() || getUserDetails().getId() == controllerMessageSender.getOwnerById(id).getUserId()) {
            controllerMessageSender.deleteOwner(id);
            return ResponseEntity.ok("Owner with id "+id+" was deleted");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/updateOwner")
    public ResponseEntity<String> updateOwner(@Valid @RequestBody OwnerDto newOwnerDto) {
        newOwnerDto.setUserId(controllerMessageSender.getOwnerById(newOwnerDto.getId()).getUserId());

        if (userIsAdmin() || getUserDetails().getId() == newOwnerDto.getUserId()) {
            controllerMessageSender.updateOwner(newOwnerDto);
            return ResponseEntity.ok("Owner with id "+newOwnerDto.getId()+" was updated");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/getOwnerById/{id}")
    public ResponseEntity<OwnerDto> getOwnerById(@PathVariable Long id) {
        OwnerDto ownerDto = controllerMessageSender.getOwnerById(id);

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
        List<OwnerDto> ownerDtoList = controllerMessageSender.getOwnersByName(name);
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
