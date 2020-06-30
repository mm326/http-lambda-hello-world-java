package com.muadmo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@ExtendWith(MockitoExtension.class)
public class DynamoDbTest {
    
    @Mock
    private DynamoDbClient dynamoDBClient;
    
    @Captor
    private ArgumentCaptor<PutItemRequest> argumentCaptor;

    @Test
    @Disabled
    void shouldPutItemInTable() throws Exception {
        HelloWorldHandler handler = new HelloWorldHandler();
        handler.putItemInTable("muad");
        Map<String, AttributeValue> expectedItem = Map.of("name", AttributeValue.builder().s("MUAD").build());
        verify(dynamoDBClient).putItem(argumentCaptor.capture());
        assertEquals(expectedItem, argumentCaptor.getValue().item());
    }
}
