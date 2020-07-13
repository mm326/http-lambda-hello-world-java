package com.muadmo.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.muadmo.service.DynamoDbService;

@ExtendWith(MockitoExtension.class)
public class PostUserHandlerTest {
    
    private PostUserHandler underTest;
    private APIGatewayProxyRequestEvent inputRequest;
    @Mock
    DynamoDbService dynamoDbService;
    
    @BeforeEach
    void setUp() {
        underTest = new PostUserHandler(dynamoDbService); 
        inputRequest = new APIGatewayProxyRequestEvent();
    }
        
    @Test
    void shouldReturn201() throws JsonMappingException, JsonProcessingException {
        String inputJson = "{\"nameId\":\"test\", \"age\":\"24\", \"email\":\"test@email.com\"}";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent()
                .withStatusCode(201)
                .withBody(inputJson);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest.withBody(inputJson).withHeaders(Map.of("content-type", "application/json")));
        assertEquals(expectedResponse, actualResponse);
    }
        
    @Test
    void shouldReturn400ForNoHeaders() throws JsonMappingException, JsonProcessingException {
        String inputBody = "{\"nameId\":\"test2\", \"age\":\"25\", \"email\":\"test2@email.com\"}";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("{\"error\": \"No headers provided\"}");
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent()
                .withPathParameters(Map.of("nameId", "test"))
                .withBody(inputBody);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest);
        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void shouldReturn415ForBadContentType() throws JsonMappingException, JsonProcessingException {
        String inputBody = "{\"nameId\":\"test2\", \"age\":\"25\", \"email\":\"test2@email.com\"}";
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent()
                .withPathParameters(Map.of("nameId", "test"))
                .withHeaders(Map.of("Content-Type", "application/text"))
                .withBody(inputBody);
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(415);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest.withBody(inputBody));
        assertEquals(expectedResponse, actualResponse);
    }
    @ParameterizedTest
    @MethodSource("inputBody")
    void shouldReturn400ForNoBody(APIGatewayProxyRequestEvent inputRequest) throws JsonMappingException, JsonProcessingException {
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("{\"error\": \"No body provided\"}");
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest);
        assertEquals(expectedResponse, actualResponse);
    }
    
    private static Stream<Arguments> inputBody() {
        return Stream.of(
                Arguments.of(new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test")).withHeaders(Map.of("content-type", "application/json"))),
                Arguments.of(new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test")).withHeaders(Map.of("content-type", "application/json")).withBody("{}"))
        );
    }
    
}
