package com.muadmo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

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
    @SuppressWarnings("unchecked")
    void shouldGetUserFromDatabase() {
            String expectedJson = "{\"nameId\":\"test\",\"email\":\"test@email.com\"}";
            APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withBody(expectedJson).withStatusCode(200);
            APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test"));
            Map<String, AttributeValue> item = Map.of(
                    "nameId", AttributeValue.builder().s("test").build(),
                    "age", AttributeValue.builder().n("24").build(), 
                    "email", AttributeValue.builder().s("test@email.com").build());
            
            QueryResponse response = QueryResponse.builder().count(1).items(item).build();
            when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(response);
            APIGatewayProxyResponseEvent actualResponse = handler.getUserFromDatabase(input);
            assertEquals(expectedResponse, actualResponse);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    void shouldReturn404ForUnknownGetUser() {
            APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(404);
            APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test2"));
            QueryResponse response = QueryResponse.builder().count(0).build();
            when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(response);
            APIGatewayProxyResponseEvent actualResponse = handler.getUserFromDatabase(input);
            assertEquals(expectedResponse, actualResponse);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    void shouldGetAllNamesFromDatabase() {
            String expectedJson = "{\"users\":[{\"nameId\":\"test\",\"email\":\"test@email.com\"},{\"nameId\":\"test2\",\"email\":\"test2@email.com\"}]}";
            APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withBody(expectedJson).withStatusCode(200);
            APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent().withPath("/names");
            Map<String, AttributeValue> item1 = Map.of(
                    "nameId", AttributeValue.builder().s("test").build(),
                    "age", AttributeValue.builder().n("24").build(), 
                    "email", AttributeValue.builder().s("test@email.com").build());
            
            Map<String, AttributeValue> item2 = Map.of(
                    "nameId", AttributeValue.builder().s("test2").build(),
                    "age", AttributeValue.builder().n("41").build(), 
                    "email", AttributeValue.builder().s("test2@email.com").build());
            ScanResponse scanResponse = ScanResponse.builder().items(item1, item2).build();
            when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(scanResponse);
            APIGatewayProxyResponseEvent actualResponse = handler.getAllUsersFromDatabase(input);
            assertEquals(expectedResponse, actualResponse);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    void shouldDeleteNameFromDatabase() {
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(204);
        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "alex"));
        Map<String, AttributeValue> item = Map.of("nameId", AttributeValue.builder().s("test").build());
        QueryResponse response = QueryResponse.builder().count(1).items(item).build();
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(response);
        DeleteItemResponse deleteResponse = DeleteItemResponse.builder().attributes(Map.of("name", AttributeValue.builder().s("alex").build())).build();
        when(dynamoDbClient.deleteItem(any(DeleteItemRequest.class))).thenReturn(deleteResponse);
        APIGatewayProxyResponseEvent actualResponse = handler.deleteUserFromDatabase(input);
        assertEquals(expectedResponse, actualResponse);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    void shouldUpdateName() {
        String body = "{\"age\" : \"23\"}";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(204);
        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent().withBody(body).withPathParameters(Map.of("nameId", "test"));
        Map<String, AttributeValue> item = Map.of("nameId", AttributeValue.builder().s("test").build());
        
        QueryResponse response = QueryResponse.builder().count(1).items(item).build();
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(response);
        
        UpdateItemResponse updateResponse = UpdateItemResponse.builder()
                .attributes(Map.of("nameId", AttributeValue.builder().s("test").build(), "age", AttributeValue.builder().s("24").build()))
                .build();
        when(dynamoDbClient.updateItem(any(UpdateItemRequest.class))).thenReturn(updateResponse);
        
        APIGatewayProxyResponseEvent actualResponse = handler.updateNameFromDatabase(input);
        assertEquals(expectedResponse, actualResponse);

    }
    
    @Test
    void shouldAddMultipleAttributesInPost() throws JsonMappingException, JsonProcessingException{
        String expectedJson = "{\"nameId\":\"muad\", \"age\":\"24\", \"email\":\"muad@email.com\"}";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withBody(expectedJson).withStatusCode(201);
        Map<String, AttributeValue> expectedItems = Map.of(
                "nameId", AttributeValue.builder().s("muad").build(),
                "age", AttributeValue.builder().s("24").build(),
                "email", AttributeValue.builder().s("muad@email.com").build());
        String inputJson = "{\"nameId\":\"muad\", \"age\":\"24\", \"email\":\"muad@email.com\"}";
        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent().withBody(inputJson);
        
        APIGatewayProxyResponseEvent actualResponse = handler.postUserToDataDatabase(input);

        verify(dynamoDbClient).putItem(argumentCaptor.capture());
        assertEquals(expectedResponse, actualResponse);
        assertEquals(expectedItems , argumentCaptor.getAllValues().get(0).item());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    void shouldReturn404ForUnknownUser() {
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(404);
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test4"));
        QueryResponse response = QueryResponse.builder().count(0).build();
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(response);
        
        APIGatewayProxyResponseEvent actualResponse = handler.deleteUserFromDatabase(request);
        assertEquals(expectedResponse, actualResponse);

    }
}
