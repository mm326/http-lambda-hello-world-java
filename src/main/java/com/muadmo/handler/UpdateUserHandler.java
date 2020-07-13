package com.muadmo.handler;

import java.util.Map;

import javax.inject.Inject;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.service.DynamoDbService;

public class UpdateUserHandler {

    private DynamoDbService dynamoDbService;
    
    @Inject
    public UpdateUserHandler(DynamoDbService dynamoDbService) {
        this.dynamoDbService = dynamoDbService;
    }

    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent request) throws JsonMappingException, JsonProcessingException {
        String nameId = request.getPathParameters().get("nameId");
        Map<String, String> headers = request.getHeaders();
        if (!dynamoDbService.doesItemExist(nameId)) {
            return new APIGatewayProxyResponseEvent().withStatusCode(404).withBody("{\"error\": \"user " + nameId + " does not exist\"}");
        } else if (request.getHeaders() == null) {
            return new APIGatewayProxyResponseEvent().withStatusCode(400)
                    .withBody("{\"error\": \"No headers provided\"}");
        } else if (request.getBody() == null || request.getBody().equals("{}")) {
            return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("{\"error\": \"No body provided\"}");
        } else if (headers.get("content-type") == null || !headers.get("content-type").equals("application/json")) {
            return new APIGatewayProxyResponseEvent().withStatusCode(415);
        } else {
            dynamoDbService.updateItemInTable(nameId, request.getBody());
            return new APIGatewayProxyResponseEvent().withStatusCode(204);
        }
    }
}
