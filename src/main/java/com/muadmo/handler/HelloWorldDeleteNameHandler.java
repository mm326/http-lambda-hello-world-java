package com.muadmo.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.service.HelloWorldService;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class HelloWorldDeleteNameHandler {
    private DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();

    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent request) throws JsonMappingException, JsonProcessingException {
        HelloWorldService helloWorldService = new HelloWorldService(dynamoDbClient);
        return helloWorldService.deleteUserFromDatabase(request);
    }
}
