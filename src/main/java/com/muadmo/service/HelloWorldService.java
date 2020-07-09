package com.muadmo.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class HelloWorldService {

    private final DynamoDbClient dynamoDbClient;
    private static String TABLE_NAME = System.getenv("USER_TABLE");

    public HelloWorldService(DynamoDbClient dynamoDBClient) {
        this.dynamoDbClient = dynamoDBClient;
    }

    public APIGatewayProxyResponseEvent getUserFromDatabase(APIGatewayProxyRequestEvent request) {
        Map<String, String> pathParameters = request.getPathParameters();
        String nameParameter = pathParameters.get("nameId");
        QueryRequest queryRequest = QueryRequest.builder().keyConditionExpression("nameId = :" + nameParameter)
                .tableName(TABLE_NAME).expressionAttributeValues(
                        Map.of(":" + nameParameter, AttributeValue.builder().s(nameParameter).build()))
                .build();
        List<Map<String, AttributeValue>> items = dynamoDbClient.query(queryRequest).items();
        if (!dynamoDbClient.query(queryRequest).items().isEmpty()) {
            Map<String, AttributeValue> item = items.get(0);
            JSONObject jsonObject = mapItemToJsonObject(item);
            return new APIGatewayProxyResponseEvent().withBody(jsonObject.toString()).withStatusCode(200);
        } else {
            return new APIGatewayProxyResponseEvent().withStatusCode(404);
        }
    }

    public APIGatewayProxyResponseEvent getAllNamesFromDatabase(APIGatewayProxyRequestEvent request) {
        ScanRequest scanRequest = ScanRequest.builder().tableName(TABLE_NAME).projectionExpression("nameId, age, email")
                .build();
        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);

        List<JSONObject> jsonItems = scanResponse.items().stream().map(this::mapItemToJsonObject)
                .collect(Collectors.toList());
        JSONObject body = new JSONObject();

        body.put("users", jsonItems);
        return new APIGatewayProxyResponseEvent().withBody(body.toString()).withStatusCode(200);
    }

    public APIGatewayProxyResponseEvent getAllUsersFromDatabase(APIGatewayProxyRequestEvent request) {
        ScanRequest scanRequest = ScanRequest.builder().tableName(TABLE_NAME).projectionExpression("nameId, age, email")
                .build();
        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);

        List<JSONObject> jsonItems = scanResponse.items().stream().map(this::mapItemToJsonObject)
                .collect(Collectors.toList());

        JSONObject body = new JSONObject();
        body.put("users", jsonItems);
        return new APIGatewayProxyResponseEvent().withBody(body.toString()).withStatusCode(200);
    }

    public APIGatewayProxyResponseEvent deleteUserFromDatabase(APIGatewayProxyRequestEvent request) {
        Map<String, String> pathParameters = request.getPathParameters();
        String nameParameter = pathParameters.get("nameId");
        if (!doesUserExist(nameParameter)) {
            DeleteItemRequest deleteRequest = DeleteItemRequest.builder().tableName(TABLE_NAME)
                    .key(Map.of("nameId", AttributeValue.builder().s(nameParameter).build())).build();
            dynamoDbClient.deleteItem(deleteRequest);
            return new APIGatewayProxyResponseEvent().withStatusCode(204);
        } else {
            return new APIGatewayProxyResponseEvent().withStatusCode(404);

        }
    }

    private boolean doesUserExist(String nameId) {
        QueryRequest queryRequest = QueryRequest.builder().keyConditionExpression("nameId = :" + nameId)
                .tableName(TABLE_NAME)
                .expressionAttributeValues(Map.of(":" + nameId, AttributeValue.builder().s(nameId).build())).build();
        return dynamoDbClient.query(queryRequest).items().isEmpty();
    }

    public APIGatewayProxyResponseEvent updateNameFromDatabase(APIGatewayProxyRequestEvent request) {
        Map<String, String> pathParameters = request.getPathParameters();
        String nameParameter = pathParameters.get("nameId");
        if (!doesUserExist(nameParameter)) {
            JSONObject json = new JSONObject(request.getBody());
            Iterator<String> keys = json.keys();
            HashMap<String, AttributeValueUpdate> items = new HashMap<>();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = json.getString(key);
                items.put(key, AttributeValueUpdate.builder().value(AttributeValue.builder().s(value).build()).build());
            }
            UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                    .key(Map.of("nameId", AttributeValue.builder().s(nameParameter).build())).attributeUpdates(items)
                    .returnValues(ReturnValue.ALL_OLD).tableName(TABLE_NAME).build();
            dynamoDbClient.updateItem(updateItemRequest);
            return new APIGatewayProxyResponseEvent().withStatusCode(204);
        } else {
            return new APIGatewayProxyResponseEvent().withStatusCode(404);
        }
    }

    public APIGatewayProxyResponseEvent postUserToDataDatabase(APIGatewayProxyRequestEvent request) throws ConditionalCheckFailedException{
        String body = request.getBody();
        Map<String, AttributeValue> items = mapBodyToItem(body);
        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(TABLE_NAME)
                .conditionExpression("attribute_not_exists(nameId)").item(items).build();
        dynamoDbClient.putItem(putItemRequest);
        return new APIGatewayProxyResponseEvent().withStatusCode(201).withBody(body);
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

    private JSONObject mapItemToJsonObject(Map<String, AttributeValue> item) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameId", item.get("nameId").s());
        jsonObject.put("age", item.get("age").s());
        jsonObject.put("email", item.get("email").s());
        return jsonObject;
    }
}
