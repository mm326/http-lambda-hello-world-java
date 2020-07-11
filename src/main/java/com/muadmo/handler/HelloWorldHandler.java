package com.muadmo.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.service.DynamoDbService;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class HelloWorldHandler {

    private static String TABLE_NAME = System.getenv("USER_TABLE");

    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent request) throws JsonMappingException, JsonProcessingException {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();
        DynamoDbService dynamoDbService = new DynamoDbService(dynamoDbClient, TABLE_NAME);
        
        if (request.getHttpMethod().equals("GET") && request.getPath().equals("/users")) {
            return new HelloWorldGetAllUserHandler(dynamoDbService).handle(request);
        } else if (request.getHttpMethod().equals("GET") && request.getResource().equals("/users/{nameId}")) {
            return new HelloWorldGetUserHandler(dynamoDbService).handle(request);
        } else if (request.getHttpMethod().equals("PUT") && request.getResource().equals("/users/{nameId}")) {
            return new HelloWorldUpdateUserHandler(dynamoDbService).handle(request);
        } else if (request.getHttpMethod().equals("DELETE") && request.getResource().equals("/users/{nameId}")) {
            return new HelloWorldDeleteUserHandler(dynamoDbService).handle(request);
        } else if (request.getHttpMethod().equals("POST") && request.getPath().equals("/users")) {
            return new HelloWorldPostUserHandler(dynamoDbService).handle(request);
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("{\"error\": \" No Lambda matched\"}");
    }

}
