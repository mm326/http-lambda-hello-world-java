package com.muadmo.handler;

import java.util.Map;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.service.DynamoDbService;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class HelloWorldGetUserHandler {

    private DynamoDbService dynamoDbService;

    public HelloWorldGetUserHandler(DynamoDbService dynamoDbService) {
        this.dynamoDbService = dynamoDbService;
    }

    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent request) throws JsonMappingException, JsonProcessingException {
        String nameId = request.getPathParameters().get("nameId");
        
        if (!dynamoDbService.doesItemExist(nameId)) {
            return new APIGatewayProxyResponseEvent().withStatusCode(404).withBody("{\"error\": \"user "+ nameId + " does not exist\"}");
        }
        else {
            Map<String, AttributeValue> item = dynamoDbService.getItemFromTable(nameId);
            String body = mapItemToJsonObject(item).toString();
            return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(body);
        }
    }
    
    private JSONObject mapItemToJsonObject(Map<String, AttributeValue> item) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameId", item.get("nameId").s());
        jsonObject.put("age", item.get("age").s());
        jsonObject.put("email", item.get("email").s());
        return jsonObject;
    }
}
