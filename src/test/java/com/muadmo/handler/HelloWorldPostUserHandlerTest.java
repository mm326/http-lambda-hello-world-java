package com.muadmo.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.muadmo.service.HelloWorldService;

@ExtendWith(MockitoExtension.class)
public class HelloWorldPostUserHandlerTest {
    
    @Mock
    HelloWorldService service;
    
    @Test
    void shouldReturn400ForNoBody() throws Exception {
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("{\"error\": \"no body provided\"}");
        HelloWorldPostUserHandler underTest = new HelloWorldPostUserHandler();
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test"));
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest);
        assertEquals(expectedResponse, actualResponse);
    }
}
