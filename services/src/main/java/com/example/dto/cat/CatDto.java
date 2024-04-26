package com.example.dto.cat;

import com.example.entities.cat.Cat;
import com.example.entities.cat.Color;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.stream.Collectors;

public class CatDto {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String breed;
    @NotBlank
    private String color;
    @NotNull
    private Long owner;
    @NotNull
    private List<Long> friends;

    public CatDto(Cat cat) {
        id = cat.getId();
        name = cat.getName();
        breed = cat.getBreed();
        color = parseColor(cat.getColor());
        owner = cat.getOwner().getId();
        friends = cat.getFriends().stream().map(Cat::getId).collect(Collectors.toList());
    }

    public CatDto() {

    }

    private String parseColor(Color color) {
        if (color == Color.black) {
            return "black";
        }
        else if (color == Color.white) {
            return "white";
        }
        else {
            throw new IllegalArgumentException("color");
        }
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

    public List<Long> getFriends() {
        return friends;
    }

    public void setFriends(List<Long> friends) {
        this.friends = friends;
    }
}
