package com.muadmo.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
<<<<<<< Updated upstream
=======
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
>>>>>>> Stashed changes

public class HelloWorldService {

    private final DynamoDbClient dynamoDbClient;
    private static String TABLE_NAME = System.getenv("NAME_TABLE");
    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public HelloWorldService(DynamoDbClient dynamoDBClient) {
        this.dynamoDbClient = dynamoDBClient;
    }

<<<<<<< Updated upstream
    public APIGatewayProxyResponseEvent inputToUpperCase(APIGatewayProxyRequestEvent input) throws JsonMappingException, JsonProcessingException {
        Pojo body = objectMapper.readValue(input.getBody(), Pojo.class);
        String upperCaseName = body.getName().toUpperCase();
        body.setName(upperCaseName);
        putItemInTable(upperCaseName);
        String responseBody = objectMapper.writeValueAsString(body);
=======
    public APIGatewayProxyResponseEvent inputToUpperCase(APIGatewayProxyRequestEvent request)
            throws JsonMappingException, JsonProcessingException {
        JSONObject body = new JSONObject(request.getBody());
        String name = body.getString("name");
        String upperCaseName = name.toUpperCase();
        putItemInTable(name);
        String responseBody = "{\"name\":\"" + upperCaseName + "\"}";
>>>>>>> Stashed changes
        return new APIGatewayProxyResponseEvent().withBody(responseBody).withStatusCode(200);
    }

    public void putItemInTable(String name) {
        Map<String, AttributeValue> item = Map.of("nameId", AttributeValue.builder().s(name).build());
        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(TABLE_NAME)
                .conditionExpression("attribute_not_exists(nameId)").item(item).build();
        dynamoDbClient.putItem(putItemRequest);
    }
<<<<<<< Updated upstream
=======

    public APIGatewayProxyResponseEvent getNameFromDatabase(APIGatewayProxyRequestEvent request) {
        String name = request.getQueryStringParameters().getOrDefault("id", null);
        QueryRequest queryRequest = QueryRequest.builder().keyConditionExpression("nameId = :" + name)
                .tableName(TABLE_NAME)
                .expressionAttributeValues(Map.of(":" + name, AttributeValue.builder().s(name).build())).build();
        Map<String, AttributeValue> item = dynamoDbClient.query(queryRequest).items().get(0);
        String nameValue = item.get("nameId").s().toUpperCase();
        String body = "{\"name\":\"" + nameValue + "\"}";
        return new APIGatewayProxyResponseEvent().withBody(body).withStatusCode(200);
    }

    public APIGatewayProxyResponseEvent getAllNamesFromDatabase(APIGatewayProxyRequestEvent request) {
        ScanRequest scanRequest = ScanRequest.builder().tableName(TABLE_NAME).projectionExpression("nameId").build();
        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        List<String> items = scanResponse.items().stream()
                .map(s -> s.get("nameId").s())
                .collect(Collectors.toList());
              
        List<JSONObject> body = items.stream()
                .map(n -> makeJson(n))
                .collect(Collectors.toList());
        return new APIGatewayProxyResponseEvent().withBody(body.toString()).withStatusCode(200);
    }

    private JSONObject makeJson(String n) {
        return new JSONObject().accumulate("name", n);
    }
>>>>>>> Stashed changes
}
