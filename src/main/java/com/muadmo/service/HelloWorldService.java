package com.muadmo.service;

import java.util.Map;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

public class HelloWorldService {

    private final DynamoDbClient dynamoDbClient;
    private static String TABLE_NAME = System.getenv("NAME_TABLE");

    public HelloWorldService(DynamoDbClient dynamoDBClient) {
        this.dynamoDbClient = dynamoDBClient;
    }

    public APIGatewayProxyResponseEvent inputToUpperCase(APIGatewayProxyRequestEvent request) throws JsonMappingException, JsonProcessingException {
        JSONObject body = new JSONObject(request.getBody());
        String name = body.getString("name");
        String upperCaseName = name.toUpperCase();
        putItemInTable(name);
        String responseBody = "{\"name\":\""+ upperCaseName +"\"}";;
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

     public APIGatewayProxyResponseEvent getNameFromDatabase(APIGatewayProxyRequestEvent request) {
         String name = request.getQueryStringParameters().getOrDefault("id", null);
         QueryRequest queryRequest = QueryRequest.builder()
                 .keyConditionExpression("nameId = :" + name)
                 .tableName(TABLE_NAME)
                 .expressionAttributeValues(Map.of(":" + name, AttributeValue.builder().s(name).build()))
                 .build();
         Map<String, AttributeValue> item = dynamoDbClient.query(queryRequest).items().get(0);
         String nameValue = item.get("nameId").s().toUpperCase();
         String body = "{\"name\":\""+ nameValue +"\"}";
        return new APIGatewayProxyResponseEvent().withBody(body).withStatusCode(200);
    }
}
