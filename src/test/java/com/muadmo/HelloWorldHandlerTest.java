package com.muadmo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HelloWorldHandlerTest {
    
    private HelloWorldHandler handler;
    
   @BeforeEach
   void setUp() {
       handler = new HelloWorldHandler();
   }
    @Test
    void shouldReturnHelloAndInput() {
        String actualOutput = handler.handleInput("Sam") ;
        String expectedOutput = "Hello, SAM!";
        assertEquals(expectedOutput, actualOutput);
    }
    
    @Test
    void shouldReturnMyInputInCapital() {
        String actualOutout = handler.handleInput("muad");
        String expectedOutput = "Hello, MUAD!";
        assertEquals(expectedOutput, actualOutout);
    }

}
