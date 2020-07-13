package com.muadmo.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.di.DaggerLambdaComponent;
import com.muadmo.service.DynamoDbService;


public class MainHandler {

//    private UpdateUserHandler updateUserHandler;
//    private DeleteUserHandler deleteHandler;
//    private GetAllUserHandler getAllUserHandler;
//    private GetUserHandler getHandler;
//    private PostUserHandler postHandler;

    public APIGatewayProxyResponseEvent route(APIGatewayProxyRequestEvent request) throws JsonMappingException, JsonProcessingException {

        DynamoDbService dynamoDbService = DaggerLambdaComponent.builder().build().dynamoDbService();
        System.out.println(request);
            if (request.getHttpMethod().equals("GET") && request.getPath().equals("/users")) {
                return new GetAllUserHandler(dynamoDbService).handle(request);
            } else if (request.getHttpMethod().equals("GET") && request.getResource().equals("/users/{nameId}")) {
                return new GetUserHandler(dynamoDbService).handle(request);
            } else if (request.getHttpMethod().equals("PUT") && request.getResource().equals("/users/{nameId}")) {
                return new UpdateUserHandler(dynamoDbService).handle(request);
            } else if (request.getHttpMethod().equals("DELETE") && request.getResource().equals("/users/{nameId}")) {
                return new DeleteUserHandler(dynamoDbService).handle(request);
            } else if (request.getHttpMethod().equals("POST") && request.getPath().equals("/users")) {
                return new PostUserHandler(dynamoDbService).handle(request);
            }
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(400) .withBody("{\"error\": \" No Lambda matched\"}");
    }
}
