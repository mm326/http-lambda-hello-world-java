package com.muadmo.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.service.DynamoDbService;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@ExtendWith(MockitoExtension.class)
public class HelloWorldGetUserHandlerTest {

    @Mock
    DynamoDbService dynamoDbService;
    
    @Test
    void shouldReturn404ForUnkownUser() throws JsonMappingException, JsonProcessingException {
        String nameId = "test2";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(404).withBody("{\"error\": \"user test2 does not exist\"}");
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test2"));
        HelloWorldGetUserHandler underTest = new HelloWorldGetUserHandler(dynamoDbService);      
        when(dynamoDbService.doesItemExist(nameId)).thenReturn(false);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest);
        assertEquals(expectedResponse, actualResponse);
    }
    
    @Test
    void shouldReturn200forSuccessfulGet() throws JsonMappingException, JsonProcessingException {
        String nameId = "test";
        String expectedJson = "{\"nameId\":\"test\",\"age\":\"24\",\"email\":\"test@email.com\"}";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(expectedJson);
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test"));
        HelloWorldGetUserHandler underTest = new HelloWorldGetUserHandler(dynamoDbService);      
        when(dynamoDbService.doesItemExist(nameId)).thenReturn(true);
        Map<String, AttributeValue> item = Map.of(
                "nameId", AttributeValue.builder().s(nameId).build(),
                "age", AttributeValue.builder().s("24").build(), 
                "email", AttributeValue.builder().s("test@email.com").build());
        when(dynamoDbService.getItemFromTable(nameId)).thenReturn(item);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest);
        assertEquals(expectedResponse, actualResponse);
    }
}
