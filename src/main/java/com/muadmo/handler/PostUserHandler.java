package com.muadmo.handler;

import javax.inject.Inject;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.service.DynamoDbService;

public class PostUserHandler {

    private final DynamoDbService dynamoDbService;
    
    @Inject
    public PostUserHandler(DynamoDbService dynamoDbService) {
       this.dynamoDbService = dynamoDbService;
    }


    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent request) throws JsonMappingException, JsonProcessingException {
        if (request.getHeaders() == null) {
            return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("{\"error\": \"No headers provided\"}");
        } else if (request.getBody() == null || request.getBody().equals("{}")) {
            return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("{\"error\": \"No body provided\"}");
        } else if (!request.getHeaders().get("Content-Type").equals("application/json")) {
            return new APIGatewayProxyResponseEvent().withStatusCode(415);
        } else {
            dynamoDbService.putItemIntoTable(request.getBody());
            return new APIGatewayProxyResponseEvent().withStatusCode(201).withBody(request.getBody());
        }
    }
}
