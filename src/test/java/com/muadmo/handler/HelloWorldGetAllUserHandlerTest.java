package com.muadmo.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
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
public class HelloWorldGetAllUserHandlerTest {

    @Mock
    private DynamoDbService dynamoDbService;
    @Test
    void shouldReturn200ForSuccessfulGet() throws JsonMappingException, JsonProcessingException {
        String expectedJson = "{\"users\":[{\"nameId\":\"test\",\"age\":\"24\",\"email\":\"test@email.com\"},{\"nameId\":\"test2\",\"age\":\"41\",\"email\":\"test2@email.com\"}]}";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(expectedJson);
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test"));
        Map<String, AttributeValue> item1 = Map.of(
                "nameId", AttributeValue.builder().s("test").build(),
                "age", AttributeValue.builder().s("24").build(), 
                "email", AttributeValue.builder().s("test@email.com").build());
        
        Map<String, AttributeValue> item2 = Map.of(
                "nameId", AttributeValue.builder().s("test2").build(),
                "age", AttributeValue.builder().s("41").build(), 
                "email", AttributeValue.builder().s("test2@email.com").build());
        List<Map<String, AttributeValue>> items = List.of(item1, item2);
        
        HelloWorldGetAllUserHandler underTest = new HelloWorldGetAllUserHandler(dynamoDbService);      
        when(dynamoDbService.getAllItemsFromTable()).thenReturn(items);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest);
        assertEquals(expectedResponse, actualResponse);
    }
}
