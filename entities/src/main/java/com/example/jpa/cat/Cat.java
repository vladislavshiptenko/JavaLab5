package com.example.jpa.cat;


import com.example.jpa.owner.Owner;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "cats")
public class Cat {
    public Cat(String name, String breed, Color color, Owner owner) {
        this.name = name;
        this.breed = breed;
        this.color = color;
        this.owner = owner;
    }

    public Cat() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String breed;

    @Enumerated(EnumType.STRING)
    private Color color;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}
