package com.ttrommlitz.family_map_client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.EventRequest;
import request.PersonRequest;
import request.RegisterRequest;
import result.EventResult;
import result.PersonResult;
import result.RegisterResult;

public class GetPersonsTest {
    private static ServerProxy serverProxy;
    private static RegisterRequest registerRequest;
    private static PersonRequest personRequest;
    @BeforeAll
    public static void setUp() {
        serverProxy = new ServerProxy("127.0.0.1", "8080");
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newUsername");
        registerRequest.setFirstName("Tyler");
        registerRequest.setLastName("Trommlitz");
        registerRequest.setEmail("ttromm@byu.edu");
        registerRequest.setPassword("password");
        registerRequest.setGender("m");
        RegisterResult result = serverProxy.register(registerRequest);
        personRequest = new PersonRequest();
        personRequest.setAuthtoken(result.getAuthtoken());
    }

    @Test
    public void getAllEventsPass() {
        PersonResult result = serverProxy.getAllPersons(personRequest);
        assertTrue(result.isSuccess());
        assertEquals("newUsername", result.getData()[0].getAssociatedUsername());
        assertEquals(31, result.getData().length);
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
