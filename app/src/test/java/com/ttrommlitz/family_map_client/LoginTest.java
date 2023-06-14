package com.ttrommlitz.family_map_client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;

public class LoginTest {
    private static ServerProxy serverProxy;
    private static LoginRequest loginRequest;
    @BeforeAll
    public static void setUp() {
        serverProxy = new ServerProxy("127.0.0.1", "8080");
        loginRequest = new LoginRequest();
        loginRequest.setUsername("ttromm");
        loginRequest.setPassword("myPassword");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("ttromm");
        registerRequest.setFirstName("Tyler");
        registerRequest.setLastName("Trommlitz");
        registerRequest.setEmail("ttromm@byu.edu");
        registerRequest.setPassword("myPassword");
        registerRequest.setGender("m");
        serverProxy.register(registerRequest);

    }

    @Test
    public void loginPass() {
        LoginResult result = serverProxy.login(loginRequest);
        assertNotNull(result.getAuthtoken());
        assertNotNull(result.getPersonID());
        assertEquals("ttromm", result.getUsername());
        assertTrue(result.isSuccess());
    }

    @Test
    public void loginFail() {
        LoginRequest badRequest = new LoginRequest();
        badRequest.setUsername("ttromm");
        badRequest.setPassword("BadPassword");
        LoginResult result = serverProxy.login(badRequest);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().toLowerCase().contains("error"));
    }
}
