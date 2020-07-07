package com.muadmo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
<<<<<<< Updated upstream
=======
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
>>>>>>> Stashed changes

@ExtendWith(MockitoExtension.class)
public class HelloWorldServiceTest {

    private HelloWorldService handler;

    @Mock
    private DynamoDbClient dynamoDbClient;
    @Captor
    private ArgumentCaptor<PutItemRequest> argumentCaptor;

   @BeforeEach
   void setUp() {
       handler = new HelloWorldService(dynamoDbClient);
   }

    @Test
    void shouldReturnMyInputInCapitals() throws JsonMappingException, JsonProcessingException {
        String expectedJson = "{\"name\":\"MUAD\"}";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withBody(expectedJson).withStatusCode(200);
        String inputJson = "{\"name\":\"muad\"}";
        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent().withBody(inputJson);

        APIGatewayProxyResponseEvent actualResponse = handler.inputToUpperCase(input);

        assertEquals(expectedResponse, actualResponse);
    }


    @Test
    void shouldPutItemInTable() {
        handler.putItemInTable("MUAD");
        Map<String, AttributeValue> expectedItem = Map.of("nameId", AttributeValue.builder().s("MUAD").build());
        verify(dynamoDbClient).putItem(argumentCaptor.capture());
        assertEquals(expectedItem, argumentCaptor.getValue().item());
    }
<<<<<<< Updated upstream
=======
    
    @Test
    @SuppressWarnings("unchecked")
    void shouldGetNameFromDatabase() {
            String expectedJson = "{\"name\":\"MUAD\"}";
            APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withBody(expectedJson).withStatusCode(200);
            APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent().withQueryStringParameters(Map.of("id", "muad"));
            Map<String, AttributeValue> item = Map.of("nameId", AttributeValue.builder().s("MUAD").build());
            
            QueryResponse response = QueryResponse.builder().count(1).items(item).build();
            when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(response);
            APIGatewayProxyResponseEvent actualResponse = handler.getNameFromDatabase(input);
            assertEquals(expectedResponse, actualResponse);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    void shouldGetAllNamesFromDatabase() {
            String expectedJson = "[{\"name\":\"muad\"}, {\"name\":\"alex\"}]";
            APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withBody(expectedJson).withStatusCode(200);
            APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent().withPath("/names");
            Map<String, AttributeValue> item1 = Map.of("nameId", AttributeValue.builder().s("muad").build());
            Map<String, AttributeValue> item2 = Map.of("nameId", AttributeValue.builder().s("alex").build());
            
            ScanResponse scanResponse = ScanResponse.builder().items(item1, item2).build();
            when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(scanResponse);
            APIGatewayProxyResponseEvent actualResponse = handler.getAllNamesFromDatabase(input);
            assertEquals(expectedResponse, actualResponse);
    }
>>>>>>> Stashed changes

}
