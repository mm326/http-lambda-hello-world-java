package com.muadmo;

import java.util.Map;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class HelloWorldService {

    private final DynamoDbClient dynamoDbClient;

    public HelloWorldService(DynamoDbClient dynamoDBClient) {
        this.dynamoDbClient = dynamoDBClient;
    }

    public String handleInput(String input) {
        String upperCaseName = "Hello, " + input.toUpperCase() + "!";
        putItemInTable(upperCaseName);
        return upperCaseName;
    }

    public void putItemInTable(String name) {
        Map<String, AttributeValue> item = Map.of("name", AttributeValue.builder().s(name).build());
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("nameTable")
                .conditionExpression("attribute_not_exists(name)")
                .item(item)
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }
}
