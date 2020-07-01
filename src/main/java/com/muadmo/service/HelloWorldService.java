package com.muadmo.service;

import java.util.Map;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class HelloWorldService {

    private final DynamoDbClient dynamoDbClient;
    private static String TABLE_NAME = System.getenv("NAME_TABLE");

    public HelloWorldService(DynamoDbClient dynamoDBClient) {
        this.dynamoDbClient = dynamoDBClient;
    }

    public String inputToUpperCase(String input) {
        String upperCaseName = "Hello, " + input.toUpperCase() + "!";
        putItemInTable(upperCaseName);
        return upperCaseName;
    }

    public void putItemInTable(String name) {
        Map<String, AttributeValue> item = Map.of("nameId", AttributeValue.builder().s(name).build());
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .conditionExpression("attribute_not_exists(nameId)")
                .item(item)
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }
}
