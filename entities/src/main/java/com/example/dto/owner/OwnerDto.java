package com.example.dto.owner;

import com.example.jpa.owner.Owner;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OwnerDto implements Serializable {
    private long id;
    @NotBlank
    private String name;
    @NotNull
    @Past
    private Date birthday;

    private long userId;

    public OwnerDto(Owner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("owner");
        }

        id = owner.getId();
        name = owner.getName();
        birthday = owner.getBirthday();
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
