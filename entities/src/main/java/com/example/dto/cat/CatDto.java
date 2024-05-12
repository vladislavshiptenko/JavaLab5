package com.example.dto.cat;

import com.example.jpa.cat.Cat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class CatDto implements Serializable {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String breed;
    @NotBlank
    private String color;
    @NotNull
    private Long owner;

    public CatDto(Cat cat) {
        id = cat.getId();
        name = cat.getName();
        breed = cat.getBreed();
        color = cat.getColor().name();
        owner = cat.getOwner().getId();
    }

    public CatDto() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }
}
