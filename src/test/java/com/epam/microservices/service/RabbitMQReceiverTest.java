package com.epam.microservices.service;

import com.epam.processor.service.RabbitMQReceiver;
import com.epam.processor.service.ResourceProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
class RabbitMQReceiverTest {
    @MockBean
    private ResourceProcessor resourceProcessor;

    @InjectMocks
    private RabbitMQReceiver receiver;

    @Test
    void processTest() {
        String resourceId = "3";
        doNothing().when(resourceProcessor).process(resourceId);

        receiver.process(resourceId);
        verify(resourceProcessor).process(resourceId);
    }
}