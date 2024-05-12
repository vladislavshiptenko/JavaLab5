package com.example.rabbitmq;

import com.example.dto.user.UserDto;
import com.example.services.auth.UserDetailsServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMessageReceiver {
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserMessageReceiver(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @RabbitListener(queues = "queueGetUserById")
    public UserDto getUserById(Long id) {
        return userDetailsService.getUserById(id);
    }
}
