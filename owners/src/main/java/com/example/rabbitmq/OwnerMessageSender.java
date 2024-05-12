package com.example.rabbitmq;

import com.example.dto.cat.CatDto;
import com.example.dto.user.UserDetailsImpl;
import com.example.dto.user.UserDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OwnerMessageSender {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OwnerMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public CatDto getCatById(Long id) {
        return (CatDto) rabbitTemplate.convertSendAndReceive("queueGetCatById", id);
    }

    public UserDto getUserById(Long id) {
        return (UserDto) rabbitTemplate.convertSendAndReceive("queueGetUserById", id);
    }
}
