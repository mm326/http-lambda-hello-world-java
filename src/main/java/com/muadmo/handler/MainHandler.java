package com.muadmo.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.di.DaggerLambdaComponent;
import com.muadmo.di.LambdaComponent;

public class MainHandler {

    private UpdateUserHandler updateUserHandler;
    private DeleteUserHandler deleteHandler;
    private GetAllUserHandler getAllUserHandler;
    private GetUserHandler getUserHandler;
    private PostUserHandler postHandler;

    public APIGatewayProxyResponseEvent route(APIGatewayProxyRequestEvent request) throws JsonMappingException, JsonProcessingException {

        LambdaComponent component = DaggerLambdaComponent.builder().build();
            if (request.getHttpMethod().equals("GET") && request.getPath().equals("/users")) {
                getAllUserHandler  = component.getAllUserHandler();
                return getAllUserHandler.handle(request);
            } else if (request.getHttpMethod().equals("GET") && request.getResource().equals("/users/{nameId}")) {
                getUserHandler = component.getHandler(); 
                return getUserHandler.handle(request);
            } else if (request.getHttpMethod().equals("PUT") && request.getResource().equals("/users/{nameId}")) {
                updateUserHandler = component.updateHandler();
                return updateUserHandler.handle(request);
            } else if (request.getHttpMethod().equals("DELETE") && request.getResource().equals("/users/{nameId}")) {
                deleteHandler = component.deleteHandler();
                return deleteHandler.handle(request);
            } else if (request.getHttpMethod().equals("POST") && request.getPath().equals("/users")) {
                postHandler = component.postHandler();
                return postHandler.handle(request);
            }
        return new APIGatewayProxyResponseEvent().withStatusCode(400) .withBody("{\"error\": \" No Lambda matched\"}");
    }
}
