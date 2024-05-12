package com.example.rabbitmq;

import com.example.dto.cat.CatDto;
import com.example.services.cat.CatService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CatMessageReceiver {
    private final CatService catService;

    @Autowired
    public CatMessageReceiver(CatService catService) {
        this.catService = catService;
    }

    @RabbitListener(queues = "queueGetCatById")
    public CatDto getCatById(Long id) {
        return catService.getCatById(id);
    }

    @RabbitListener(queues = "queueAddCat")
    public CatDto addCat(CatDto catDto) {
        return catService.addCat(catDto);
    }

    @RabbitListener(queues = "queueDeleteCat")
    public void deleteCat(Long id) {
        catService.deleteCat(id);
    }

    @RabbitListener(queues = "queueUpdateCat")
    public void updateCat(CatDto newCatDto) {
        catService.updateCat(newCatDto);
    }

    @RabbitListener(queues = "queueGetCatsByBreed")
    public List<CatDto> getCatsByBreed(String breed) {
        return catService.getCatsByBreed(breed);
    }

    @RabbitListener(queues = "queueGetCatsByName")
    public List<CatDto> getCatsByName(String name) {
        return catService.getCatsByName(name);
    }

    @RabbitListener(queues = "queueGetCatsByColor")
    public List<CatDto> getCatsByColor(String color) {
        return catService.getCatsByColor(color);
    }

    @RabbitListener(queues = "queueGetCatsByOwnerId")
    public List<CatDto> getCatsByOwnerId(Long ownerId) {
        return catService.getCatsByOwnerId(ownerId);
    }
}
