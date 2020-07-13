package com.muadmo.handler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.service.DynamoDbService;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class GetAllUserHandler {

    private DynamoDbService dynamoDbService;

    @Inject
    public GetAllUserHandler(DynamoDbService dynamoDbService) {
        this.dynamoDbService = dynamoDbService;
    }

    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent request) throws JsonMappingException, JsonProcessingException {
        List<Map<String, AttributeValue>> items = dynamoDbService.getAllItemsFromTable();
        if(items.isEmpty()) {
            return new APIGatewayProxyResponseEvent().withBody("{users:[]}").withStatusCode(200);
        }
        List<JSONObject> jsonItems = items.stream()
        .map(this::mapItemToJsonObject)
        .collect(Collectors.toList());
        
        JSONObject body = new JSONObject();
        body.put("users", jsonItems);
        return new APIGatewayProxyResponseEvent().withBody(body.toString()).withStatusCode(200);
    }
    
    private JSONObject mapItemToJsonObject(Map<String, AttributeValue> item) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameId", item.get("nameId").s());
        jsonObject.put("age", item.get("age").s());
        jsonObject.put("email", item.get("email").s());
        return jsonObject;
    }
    
}
