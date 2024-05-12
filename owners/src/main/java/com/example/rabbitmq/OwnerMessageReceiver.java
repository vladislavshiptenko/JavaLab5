package com.example.rabbitmq;

import com.example.dao.owner.OwnerDao;
import com.example.dto.owner.OwnerDto;
import com.example.jpa.owner.Owner;
import com.example.services.owner.OwnerJpaDtoConverter;
import com.example.services.owner.OwnerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OwnerMessageReceiver {
    private final OwnerService ownerService;

    @Autowired
    public OwnerMessageReceiver(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @RabbitListener(queues = "queueGetOwnerById")
    public OwnerDto getOwnerById(Long id) {
        return ownerService.getOwnerById(id);
    }

    @RabbitListener(queues = "queueAddOwner")
    public OwnerDto addOwner(OwnerDto ownerDto) {
        try {
            return ownerService.addOwner(ownerDto);
        } catch (Exception e) {
            return null;
        }
    }

    @RabbitListener(queues = "queueDeleteOwner")
    public void deleteOwner(Long id) {
        ownerService.deleteOwner(id);
    }

    @RabbitListener(queues = "queueUpdateOwner")
    public void updateOwner(OwnerDto newOwnerDto) {
        ownerService.updateOwner(newOwnerDto);
    }

    @RabbitListener(queues = "queueGetOwnersByName")
    public List<OwnerDto> getOwnersByName(String name) {
        return ownerService.getOwnersByName(name);
    }
}
