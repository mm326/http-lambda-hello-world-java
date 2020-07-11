package com.muadmo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@ExtendWith(MockitoExtension.class)
public class DynamoDbServiceTest {
    
    private static final String NAME_ID = "test";

    @Mock
    private DynamoDbClient dynamoDbClient;
    
    @Captor
    private ArgumentCaptor<PutItemRequest> putItemArgumentCaptor;
    
    @Captor
    private ArgumentCaptor<UpdateItemRequest> updateItemArgumentCaptor;
    
    private DynamoDbService underTest;
    
    private static String TABLE_NAME = "table";
    
    @BeforeEach
    void setUp() {
        underTest =  new DynamoDbService(dynamoDbClient, TABLE_NAME);
    }
    @Test
    void shouldGetSingleItemFromTable() {
        String key = NAME_ID;
        Map<String, AttributeValue> expectedItem = Map.of(
                key, AttributeValue.builder().s(NAME_ID).build(),
                "age", AttributeValue.builder().s("24").build(), 
                "email", AttributeValue.builder().s("test@email.com").build());
        QueryResponse response = QueryResponse.builder().count(1).items(expectedItem).build();
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(response);
        
        Map<String, AttributeValue> actualItem = underTest.getItemFromTable(key);
        assertEquals(expectedItem, actualItem);
    }
    
    @Test
    void shouldGetAllItemsFromTable() {
        Map<String, AttributeValue> item1 = Map.of(
                "nameId", AttributeValue.builder().s(NAME_ID).build(),
                "age", AttributeValue.builder().s("24").build(), 
                "email", AttributeValue.builder().s("test@email.com").build());
        
        Map<String, AttributeValue> item2 = Map.of(
                "nameId", AttributeValue.builder().s("test2").build(),
                "age", AttributeValue.builder().s("41").build(), 
                "email", AttributeValue.builder().s("test2@email.com").build());
        List<Map<String, AttributeValue>> expectedItems = List.of(item1, item2);
        ScanResponse scanResponse = ScanResponse.builder().items(item1, item2).build();
      
        when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(scanResponse);
        
        List<Map<String, AttributeValue>> actualItems = underTest.getAllItemsFromTable();
        assertEquals(expectedItems, actualItems);
    }
    
    @Test
    void shouldPutItemIntoTable() {
        String body = "{\"nameId\":\"test\", \"age\":\"24\", \"email\":\"test@email.com\"}";
        Map<String, AttributeValue> expectedItem = Map.of(
                "nameId", AttributeValue.builder().s(NAME_ID).build(),
                "age", AttributeValue.builder().s("24").build(), 
                "email", AttributeValue.builder().s("test@email.com").build());

        underTest.putItemIntoTable(body);
        verify(dynamoDbClient).putItem(putItemArgumentCaptor.capture());
        assertEquals(expectedItem , putItemArgumentCaptor.getAllValues().get(0).item());
    }
    
    @Test
    void shouldCheckItemExistsInTable() {
        String nameId = NAME_ID;
        Map<String, AttributeValue> expectedItem = Map.of(
                "nameId", AttributeValue.builder().s(NAME_ID).build(),
                "age", AttributeValue.builder().s("24").build(), 
                "email", AttributeValue.builder().s("test@email.com").build());

        QueryResponse response = QueryResponse.builder().count(1).items(expectedItem).build();
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(response);
        assertTrue(underTest.doesItemExist(nameId));
    }
    
    @Test
    void shouldUpdateItemFromTable() {
        String nameId = NAME_ID;
        String body = "{\"nameId\":\"test2\", \"age\":\"25\", \"email\":\"test2@email.com\"}";
        Map<String, AttributeValueUpdate> expectedItem = Map.of(
                "nameId", AttributeValueUpdate.builder().value(AttributeValue.builder().s("test2").build()).build(),
                "age", AttributeValueUpdate.builder().value(AttributeValue.builder().s("25").build()).build(), 
                "email", AttributeValueUpdate.builder().value(AttributeValue.builder().s("test2@email.com").build()).build());
        
        underTest.updateItemInTable(nameId, body);
        verify(dynamoDbClient).updateItem(updateItemArgumentCaptor.capture());
        assertEquals(expectedItem, updateItemArgumentCaptor.getValue().attributeUpdates());
    }
    
    @Test
    void shouldDeleteItemFromTable() {
        String nameId = NAME_ID;
        DeleteItemResponse deleteResponse = DeleteItemResponse.builder().attributes(Map.of("nameId", AttributeValue.builder().s(NAME_ID).build())).build();
        when(dynamoDbClient.deleteItem(any(DeleteItemRequest.class))).thenReturn(deleteResponse);
        underTest.deleteItemFromTable(nameId);
    }
    
}
