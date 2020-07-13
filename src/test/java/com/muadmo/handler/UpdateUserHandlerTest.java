package com.muadmo.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
public class UpdateUserHandlerTest {

    private UpdateUserHandler underTest;
    @Mock
    DynamoDbService dynamoDbService;

    
    @BeforeEach
    void setUp() {
        underTest = new UpdateUserHandler(dynamoDbService);
    }
    
    @Test
    void shouldReturn404ForUnkownUser() throws JsonMappingException, JsonProcessingException {
        String nameId = "test2";
        String inputBody = "{\"nameId\":\"test2\", \"age\":\"25\", \"email\":\"test@email.com\"}";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(404).withBody("{\"error\": \"user test2 does not exist\"}");
        when(dynamoDbService.doesItemExist(nameId)).thenReturn(false);
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent()
                .withPathParameters(Map.of("nameId", "test2"))
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(inputBody);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest);
        assertEquals(expectedResponse, actualResponse);
    }
    
    @ParameterizedTest
    @MethodSource("inputBody")
    void shouldReturn400ForNoBody(APIGatewayProxyRequestEvent inputRequest) throws JsonMappingException, JsonProcessingException {
        String nameId = "test";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("{\"error\": \"No body provided\"}");
        when(dynamoDbService.doesItemExist(nameId)).thenReturn(true);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest);
        assertEquals(expectedResponse, actualResponse);
    }
    
    @Test
    void shouldReturn400ForNoHeaders() throws JsonMappingException, JsonProcessingException {
        String nameId = "test";
        String inputBody = "{\"nameId\":\"test2\", \"age\":\"25\", \"email\":\"test2@email.com\"}";
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("{\"error\": \"No headers provided\"}");
        when(dynamoDbService.doesItemExist(nameId)).thenReturn(true);
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent()
                .withPathParameters(Map.of("nameId", "test"))
                .withBody(inputBody);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest);
        assertEquals(expectedResponse, actualResponse);
    }
    
    @Test
    void shouldReturn204ForSuccessfulUpdate() throws JsonMappingException, JsonProcessingException {
        String nameId = "test";
        String inputBody = "{\"nameId\":\"test2\", \"age\":\"25\", \"email\":\"test2@email.com\"}";
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent()
                .withPathParameters(Map.of("nameId", "test"))
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(inputBody);
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(204);
        when(dynamoDbService.doesItemExist(nameId)).thenReturn(true);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest.withBody(inputBody));
        assertEquals(expectedResponse, actualResponse);
    }
    
    @Test
    void shouldReturn415ForBadContentType() throws JsonMappingException, JsonProcessingException {
        String nameId = "test";
        String inputBody = "{\"nameId\":\"test2\", \"age\":\"25\", \"email\":\"test2@email.com\"}";
        APIGatewayProxyRequestEvent inputRequest = new APIGatewayProxyRequestEvent()
                .withPathParameters(Map.of("nameId", "test"))
                .withHeaders(Map.of("Content-Type", "application/text"))
                .withBody(inputBody);
        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent().withStatusCode(415);
        when(dynamoDbService.doesItemExist(nameId)).thenReturn(true);
        APIGatewayProxyResponseEvent actualResponse = underTest.handle(inputRequest.withBody(inputBody));
        assertEquals(expectedResponse, actualResponse);
    }
    
    private static Stream<Arguments> inputBody() {
        return Stream.of(
                Arguments.of(new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test")).withHeaders(Map.of("Content-Type", "application/json"))),
                Arguments.of(new APIGatewayProxyRequestEvent().withPathParameters(Map.of("nameId", "test")).withHeaders(Map.of("Content-Type", "application/json")).withBody("{}"))
        );
    }
}
