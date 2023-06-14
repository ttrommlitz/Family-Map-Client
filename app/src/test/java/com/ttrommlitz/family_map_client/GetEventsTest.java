package com.ttrommlitz.family_map_client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.*;
import request.EventRequest;
import request.RegisterRequest;
import result.EventResult;
import result.RegisterResult;

public class GetEventsTest {
    private static ServerProxy serverProxy;
    private static RegisterRequest registerRequest;
    private static EventRequest eventRequest;
    @BeforeAll
    public static void setUp() {
        serverProxy = new ServerProxy("127.0.0.1", "8080");
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("username");
        registerRequest.setFirstName("Tyler");
        registerRequest.setLastName("Trommlitz");
        registerRequest.setEmail("ttromm@byu.edu");
        registerRequest.setPassword("password");
        registerRequest.setGender("m");
        RegisterResult result = serverProxy.register(registerRequest);
        eventRequest = new EventRequest();
        eventRequest.setAuthtoken(result.getAuthtoken());
    }

    @Test
    public void getAllEventsPass() {
        EventResult result = serverProxy.getAllEvents(eventRequest);
        assertTrue(result.isSuccess());
        assertEquals("username", result.getData()[0].getAssociatedUsername());
        assertEquals(123, result.getData().length);
    }

    @Test
    public void getAllEventsFail() {
        EventRequest badRequest = new EventRequest();
        badRequest.setAuthtoken("badAuthtoken");
        EventResult result = serverProxy.getAllEvents(badRequest);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().toLowerCase().contains("error"));
    }
}
