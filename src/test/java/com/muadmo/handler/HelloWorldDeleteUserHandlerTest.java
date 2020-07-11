package com.muadmo.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.service.DynamoDbService;

@ExtendWith(MockitoExtension.class)
public class HelloWorldDeleteUserHandlerTest {
    
    @Mock
    private DynamoDbService dynamoDbService;
    
    private HelloWorldDeleteUserHandler underTest;
    
    @BeforeEach
    void setUp() {
        underTest = new HelloWorldDeleteUserHandler(dynamoDbService);
    }
    
    @Test
    void shouldReturn404ForUnkownUser() throws JsonMappingException, JsonProcessingException {
        String nameId = "test2";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(404).withBody("{\"error\": \"user test2 does not exist\"}");
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test2"));
        when(dynamoDbService.doesItemExist(nameId)).thenReturn(false);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest);
        assertEquals(expectedResponse, actualResponse);
    }
    
    @Test
    void shouldReturn204ForSuccessfulDelete() throws JsonMappingException, JsonProcessingException {
        String nameId = "test";
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test"));
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(204);
        when(dynamoDbService.doesItemExist(nameId)).thenReturn(true);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest);
        assertEquals(expectedResponse, actualResponse);
    }

}
