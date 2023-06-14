package com.ttrommlitz.family_map_client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import request.EventRequest;
import request.RegisterRequest;
import result.EventResult;
import result.RegisterResult;

public class RegisterTest {
    private ServerProxy serverProxy;
    private RegisterRequest registerRequest;

    @BeforeEach
    public void setUp() {
        serverProxy = new ServerProxy("127.0.0.1", "8080");
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("ttromm6");
        registerRequest.setFirstName("Tyler");
        registerRequest.setLastName("Trommlitz");
        registerRequest.setEmail("ttromm@byu.edu");
        registerRequest.setPassword("password");
        registerRequest.setGender("m");
    }

    @Test
    public void registerPass() {
        RegisterResult result = serverProxy.register(registerRequest);
        assertNotNull(result.getAuthtoken());
        assertNotNull(result.getPersonID());
        assertEquals("ttromm6", result.getUsername());
        assertTrue(result.isSuccess());

        EventRequest eventRequest = new EventRequest();
        eventRequest.setAuthtoken(result.getAuthtoken());
        EventResult eventResult = serverProxy.getAllEvents(eventRequest);
        assertDoesNotThrow(() -> eventResult.getData()[0]);
    }

    @Test
    public void registerFail() {
        // registers same user a second time
        registerRequest.setUsername("New username");
        serverProxy.register(registerRequest);
        RegisterResult result = serverProxy.register(registerRequest);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().toLowerCase().contains("error"));
    }
}
