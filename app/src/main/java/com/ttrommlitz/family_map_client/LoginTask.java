package com.ttrommlitz.family_map_client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import request.LoginRequest;
import result.LoginResult;

public class LoginTask implements Runnable {
    private final Handler messageHandler;
    private final String host;
    private final String port;
    private final String username;
    private final String password;

    public LoginTask(Handler messageHandler, String host, String port, String username, String password) {
        this.messageHandler = messageHandler;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        ServerProxy server = new ServerProxy(host, port);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        LoginResult loginResult = server.login(loginRequest);

        if (loginResult.isSuccess()) {
            FetchDataTask fetchData = new FetchDataTask();
            fetchData.fetch(server, loginResult.getAuthtoken(), loginResult.getPersonID());
        }
        sendMessage(loginResult);
    }

    private void sendMessage(LoginResult loginResult) {
        Message message = Message.obtain();
        Bundle messageBundle = new Bundle();

        messageBundle.putString("Message", loginResult.getMessage());
        messageBundle.putBoolean("Success", loginResult.isSuccess());

        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }
}
