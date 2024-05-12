package com.example.rabbitmq;

import com.example.dto.cat.CatDto;
import com.example.dto.owner.OwnerDto;
import com.example.dto.user.UserDetailsImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ControllerMessageSender {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ControllerMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public CatDto getCatById(Long id) {
        return (CatDto) rabbitTemplate.convertSendAndReceive("queueGetCatById", id);
    }

    public OwnerDto getOwnerById(Long id) {
        return (OwnerDto) rabbitTemplate.convertSendAndReceive("queueGetOwnerById", id);
    }

    public CatDto addCat(CatDto catDto) {
        return (CatDto) rabbitTemplate.convertSendAndReceive("queueAddCat", catDto);
    }

    public void deleteCat(Long id) {
        rabbitTemplate.convertAndSend("queueDeleteCat", id);
    }

    public void updateCat(CatDto newCatDto) {
        rabbitTemplate.convertAndSend("queueUpdateCat", newCatDto);
    }

    public List<CatDto> getCatsByBreed(String breed) {
        return (List<CatDto>) rabbitTemplate.convertSendAndReceive("queueGetCatsByBreed", breed);
    }

    public List<CatDto> getCatsByName(String name) {
        return (List<CatDto>) rabbitTemplate.convertSendAndReceive("queueGetCatsByName", name);
    }

    public List<CatDto> getCatsByColor(String color) {
        return (List<CatDto>) rabbitTemplate.convertSendAndReceive("queueGetCatsByColor", color);
    }

    public List<CatDto> getCatsByOwnerId(Long ownerId) {
        return (List<CatDto>) rabbitTemplate.convertSendAndReceive("queueGetCatsByOwnerId", ownerId);
    }

    public OwnerDto addOwner(OwnerDto ownerDto) {
        System.out.println(ownerDto);
        Object owner = rabbitTemplate.convertSendAndReceive("queueAddOwner", ownerDto);
        System.out.println(owner);
        return (OwnerDto) owner;
    }

    public void deleteOwner(Long id) {
        rabbitTemplate.convertAndSend("queueDeleteOwner", id);
    }

    public void updateOwner(OwnerDto newOwnerDto) {
        rabbitTemplate.convertAndSend("queueUpdateOwner", newOwnerDto);
    }

    public List<OwnerDto> getOwnersByName(String name) {
        return (List<OwnerDto>) rabbitTemplate.convertSendAndReceive("queueGetOwnersByName", name);
    }
}
