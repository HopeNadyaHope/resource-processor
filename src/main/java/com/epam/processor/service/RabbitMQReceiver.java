package com.epam.processor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "uploaded_resourcesIds_queue")
public class RabbitMQReceiver {
    private final Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class);

    @Autowired
    private ResourceProcessor resourceProcessor;

    @RabbitHandler
    public void process(String resourceId) {
        logger.info("Resource with id={} received", resourceId);
        resourceProcessor.process(resourceId);
    }
}