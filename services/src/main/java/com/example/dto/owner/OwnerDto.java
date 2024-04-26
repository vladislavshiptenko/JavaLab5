package com.example.dto.owner;

import com.example.entities.cat.Cat;
import com.example.entities.owner.Owner;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OwnerDto {
    private long id;
    @NotBlank
    private String name;
    @NotNull
    @Past
    private Date birthday;
    @NotNull
    private List<Long> cats;

    private long userId;

    public OwnerDto(Owner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("owner");
        }

        id = owner.getId();
        name = owner.getName();
        birthday = owner.getBirthday();
        cats = new ArrayList<>();
        for (Cat cat : owner.getCats()) {
            cats.add(cat.getId());
        }
    }

    public OwnerDto() {

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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public List<Long> getCats() {
        return cats;
    }

    public void setCats(List<Long> cats) {
        this.cats = cats;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
