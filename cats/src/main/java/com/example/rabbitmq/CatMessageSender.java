package com.example.rabbitmq;

import com.example.dto.owner.OwnerDto;
import com.example.dto.user.UserDetailsImpl;
import com.example.dto.user.UserDto;
import com.example.jpa.owner.Owner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CatMessageSender {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CatMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public OwnerDto getOwnerById(Long id) {
        return (OwnerDto) rabbitTemplate.convertSendAndReceive("queueGetOwnerById", id);
    }

    public UserDto getUserById(Long id) {
        return (UserDto) rabbitTemplate.convertSendAndReceive("queueGetUserById", id);
    }
}
