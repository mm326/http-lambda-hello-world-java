package com.muadmo.service;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muadmo.data.Pojo;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class HelloWorldService {

    private final DynamoDbClient dynamoDbClient;
    private static String TABLE_NAME = System.getenv("NAME_TABLE");
    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public HelloWorldService(DynamoDbClient dynamoDBClient) {
        this.dynamoDbClient = dynamoDBClient;
    }

    public APIGatewayProxyResponseEvent inputToUpperCase(APIGatewayProxyRequestEvent input) throws JsonMappingException, JsonProcessingException {
        Pojo body = objectMapper.readValue(input.getBody(), Pojo.class);
        String upperCaseName = body.getName().toUpperCase();
        body.setName(upperCaseName);
        putItemInTable(upperCaseName);
        String responseBody = objectMapper.writeValueAsString(body);
        return new APIGatewayProxyResponseEvent().withBody(responseBody).withStatusCode(200);
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
