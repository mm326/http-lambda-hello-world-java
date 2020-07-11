package com.muadmo.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class DynamoDbService {
    
    private static String TABLE_NAME = System.getenv("USER_TABLE");
    private DynamoDbClient dynamoDbClient;

    public DynamoDbService(DynamoDbClient dynamoDbClient, String TABLE_NAME) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public Map<String, AttributeValue> getItemFromTable(String nameId) {
        QueryRequest queryRequest = QueryRequest.builder().keyConditionExpression("nameId = :" + nameId)
                .tableName(TABLE_NAME).expressionAttributeValues( Map.of(":" + nameId, AttributeValue.builder().s(nameId).build()))
                .build();
        return dynamoDbClient.query(queryRequest).items().get(0);
    }

    public List<Map<String, AttributeValue>> getAllItemsFromTable() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(TABLE_NAME)
                .projectionExpression("nameId, age, email")
                .build();
        return dynamoDbClient.scan(scanRequest).items();
    }

    public void putItemIntoTable(String body) {
        Map<String, AttributeValue> item = mapBodyToItem(body);
        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(TABLE_NAME)
                .conditionExpression("attribute_not_exists(nameId)").item(item).build();
         dynamoDbClient.putItem(putItemRequest);
    }
    
    private HashMap<String, AttributeValue> mapBodyToItem(String body) {
        JSONObject json = new JSONObject(body);
        Iterator<String> keys = json.keys();
        HashMap<String, AttributeValue> items = new HashMap<>();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = json.getString(key);
            items.put(key, AttributeValue.builder().s(value).build());
        }
        return items;
    }

    public boolean doesItemExist(String nameId) {
        QueryRequest queryRequest = QueryRequest.builder().keyConditionExpression("nameId = :" + nameId)
                .tableName(TABLE_NAME)
                .expressionAttributeValues(Map.of(":" + nameId, AttributeValue.builder().s(nameId).build()))
                .build();
        
        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);
        System.out.println("RESP "+queryResponse);
        return !queryResponse.items().isEmpty();
    }

    public void updateItemInTable(String nameId, String body) {
        JSONObject json = new JSONObject(body);
        Iterator<String> keys = json.keys();
        HashMap<String, AttributeValueUpdate> items = new HashMap<>();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = json.getString(key);
            items.put(key, AttributeValueUpdate.builder().value(AttributeValue.builder().s(value).build()).build());
        }
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .key(Map.of("nameId", AttributeValue.builder().s(nameId).build())).attributeUpdates(items)
                .returnValues(ReturnValue.ALL_NEW).tableName(TABLE_NAME).build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    public void deleteItemFromTable(String nameId) {
        DeleteItemRequest deleteRequest = DeleteItemRequest.builder().tableName(TABLE_NAME)
                .key(Map.of("nameId", AttributeValue.builder().s(nameId).build())).build();
        dynamoDbClient.deleteItem(deleteRequest);
    }
}
