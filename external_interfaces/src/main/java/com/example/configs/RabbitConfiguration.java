package com.example.configs;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory("localhost");
        return connectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public Queue queueGetCatById() {
        return new Queue("queueGetCatById");
    }

    @Bean
    public Queue queueGetOwnerById() {
        return new Queue("queueGetOwnerById");
    }

    @Bean
    public Queue queueAddCat() {
        return new Queue("queueAddCat");
    }

    @Bean
    public Queue queueDeleteCat() {
        return new Queue("queueDeleteCat");
    }

    @Bean
    public Queue queueUpdateCat() {
        return new Queue("queueUpdateCat");
    }

    @Bean
    public Queue queueGetCatsByBreed() {
        return new Queue("queueGetCatsByBreed");
    }

    @Bean
    public Queue queueGetCatsByName() {
        return new Queue("queueGetCatsByName");
    }

    @Bean
    public Queue queueGetCatsByColor() {
        return new Queue("queueGetCatsByColor");
    }

    @Bean
    public Queue queueGetCatsByOwnerId() {
        return new Queue("queueGetCatsByOwnerId");
    }

    @Bean
    public Queue queueAddOwner() {
        return new Queue("queueAddOwner");
    }

    @Bean
    public Queue queueDeleteOwner() {
        return new Queue("queueDeleteOwner");
    }

    @Bean
    public Queue queueUpdateOwner() {
        return new Queue("queueUpdateOwner");
    }

    @Bean
    public Queue queueGetOwnersByName() {
        return new Queue("queueGetOwnersByName");
    }

    @Bean
    public Queue queueGetUserById() {
        return new Queue("queueGetUserById");
    }
}
