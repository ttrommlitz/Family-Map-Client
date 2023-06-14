package com.ttrommlitz.family_map_client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Objects;

import request.RegisterRequest;
import result.RegisterResult;

public class RegisterTask implements Runnable {
    private final Handler messageHandler;
    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String gender;

    public RegisterTask(Handler messageHandler, String host, String port, String username, String password,
                        String firstName, String lastName, String email, String gender) {
        this.messageHandler = messageHandler;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = Objects.equals(gender, "Male") ? "m" : "f";
    }

    @Override
    public void run() {
        ServerProxy server = new ServerProxy(host, port);

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(password);
        registerRequest.setFirstName(firstName);
        registerRequest.setLastName(lastName);
        registerRequest.setEmail(email);
        registerRequest.setGender(gender);

        RegisterResult registerResult = server.register(registerRequest);

        if (registerResult.isSuccess()) {
            FetchDataTask fetchData = new FetchDataTask();
            fetchData.fetch(server, registerResult.getAuthtoken(), registerResult.getPersonID());
        }

        sendMessage(registerResult);
    }

    private void sendMessage(RegisterResult registerResult) {
        Message message = Message.obtain();
        Bundle messageBundle = new Bundle();

        messageBundle.putString("Message", registerResult.getMessage());
        messageBundle.putBoolean("Success", registerResult.isSuccess());

        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }
}
