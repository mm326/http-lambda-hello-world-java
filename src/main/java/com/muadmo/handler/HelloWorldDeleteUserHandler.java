package com.muadmo.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.service.DynamoDbService;

public class HelloWorldDeleteUserHandler {

    private DynamoDbService dynamoDbService;

    public HelloWorldDeleteUserHandler(DynamoDbService dynamoDbService) {
        this.dynamoDbService = dynamoDbService;
    }

    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent request) throws JsonMappingException, JsonProcessingException {
        String nameId = request.getPathParameters().get("nameId");
        if (!dynamoDbService.doesItemExist(nameId)) {
            return new APIGatewayProxyResponseEvent().withStatusCode(404).withBody("{\"error\": \"user " + nameId + " does not exist\"}");
        } else {
            dynamoDbService.deleteItemFromTable(nameId);
            return new APIGatewayProxyResponseEvent().withStatusCode(204);
        }
    }
}
