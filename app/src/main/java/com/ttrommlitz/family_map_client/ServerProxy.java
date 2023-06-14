package com.ttrommlitz.family_map_client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import request.EventRequest;
import request.LoginRequest;
import request.PersonRequest;
import request.RegisterRequest;
import result.EventResult;
import result.LoginResult;
import result.PersonResult;
import result.RegisterResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ServerProxy { // server facade
    private String host;
    private String port;
    public ServerProxy(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public LoginResult login(LoginRequest request) {
        try {
            URL url = getUrl("/user/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.connect();

            Gson requestGson = new GsonBuilder().setPrettyPrinting().create();
            String requestData = requestGson.toJson(request);
            OutputStream requestBody = connection.getOutputStream();
            writeString(requestData, requestBody);
            requestBody.close();

            InputStream responseBody;
            Reader json;
            Gson gson = new Gson();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                responseBody = connection.getInputStream();
            } else {
                responseBody = connection.getErrorStream();
            }
            json = new InputStreamReader(responseBody);
            return gson.fromJson(json, LoginResult.class);
        } catch (Exception e) {
            LoginResult badResult = new LoginResult();
            badResult.setSuccess(false);
            badResult.setMessage(e.getMessage());
            return badResult;
        }
    }

    public RegisterResult register(RegisterRequest request) {
        try {
            URL url = getUrl("/user/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.connect();

            Gson requestGson = new GsonBuilder().setPrettyPrinting().create();
            String requestData = requestGson.toJson(request);
            OutputStream requestBody = connection.getOutputStream();
            writeString(requestData, requestBody);
            requestBody.close();

            InputStream responseBody;
            Reader json;
            Gson gson = new Gson();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                responseBody = connection.getInputStream();
            } else {
                responseBody = connection.getErrorStream();
            }
            json = new InputStreamReader(responseBody);
            return gson.fromJson(json, RegisterResult.class);
        } catch (Exception e) {
            RegisterResult badResult = new RegisterResult();
            badResult.setSuccess(false);
            badResult.setMessage(e.getMessage());
            return badResult;
        }
    }

    public PersonResult getAllPersons(PersonRequest request) {
        try {
            URL url = getUrl("/person");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.addRequestProperty("Authorization", request.getAuthtoken());
            connection.connect();

            InputStream responseBody;
            Reader json;
            Gson gson = new Gson();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                responseBody = connection.getInputStream();
            } else {
                responseBody = connection.getErrorStream();
            }
            json = new InputStreamReader(responseBody);
            return gson.fromJson(json, PersonResult.class);
        } catch (Exception e) {
            PersonResult badResult = new PersonResult();
            badResult.setSuccess(false);
            badResult.setMessage(e.getMessage());
            return badResult;
        }
    }

    public EventResult getAllEvents(EventRequest request) {
        try {
            URL url = getUrl("/event");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.addRequestProperty("Authorization", request.getAuthtoken());
            connection.connect();

            InputStream responseBody;
            Reader json;
            Gson gson = new Gson();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                responseBody = connection.getInputStream();
            } else {
                responseBody = connection.getErrorStream();
            }
            json = new InputStreamReader(responseBody);
            return gson.fromJson(json, EventResult.class);
        } catch (Exception e) {
            EventResult badResult = new EventResult();
            badResult.setSuccess(false);
            badResult.setMessage(e.getMessage());
            return badResult;
        }
    }

    private URL getUrl(String apiPath) {
        try {
            return new URL("http://" + host + ":" + port + apiPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
		The readString method shows how to read a String from an InputStream.
	*/
    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

}
