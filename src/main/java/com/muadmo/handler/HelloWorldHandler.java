package com.muadmo.handler;

import com.muadmo.service.HelloWorldService;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class HelloWorldHandler {

    public String handle(String input) {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();
        HelloWorldService helloWorldService = new HelloWorldService(dynamoDbClient);
        return helloWorldService.inputToUpperCase(input);
    }

}
