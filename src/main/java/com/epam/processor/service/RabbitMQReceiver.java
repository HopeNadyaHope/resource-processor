package com.epam.processor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
@RabbitListener(queues = "uploaded_resourcesIds_queue")
public class RabbitMQReceiver {
    private static final String RECEIVED_RESOURCE_ID = "Received resource id {0}";
    private final Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class);

    @Autowired
    private ResourceProcessor resourceProcessor;

    @RabbitHandler
    public void process(String resourceId) {
        String resourceIdReceived = MessageFormat.format(RECEIVED_RESOURCE_ID, resourceId);
        logger.info(resourceIdReceived);

        resourceProcessor.process(resourceId);
    }
}