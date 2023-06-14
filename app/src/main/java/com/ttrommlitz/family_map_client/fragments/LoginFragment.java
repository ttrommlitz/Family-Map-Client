package com.ttrommlitz.family_map_client.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ttrommlitz.family_map_client.DataCache;
import com.ttrommlitz.family_map_client.LoginTask;
import com.ttrommlitz.family_map_client.R;
import com.ttrommlitz.family_map_client.RegisterTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private Button loginButton;
    private Button registerButton;
    private Listener listener;
    private EditText serverAddress;
    private EditText serverPort;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private RadioGroup gender;
    private String chosenGender;
    private DataCache dataCache = DataCache.getInstance();


    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);


        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                checkButtonStatus();
            }
        };

        // set up private EditText vars
        serverAddress = view.findViewById(R.id.server_host_field);
        serverAddress.addTextChangedListener(watcher);

        serverPort = view.findViewById(R.id.server_port_field);
        serverPort.addTextChangedListener(watcher);

        username = view.findViewById(R.id.server_username_field);
        username.addTextChangedListener(watcher);

        password = view.findViewById(R.id.server_password_field);
        password.addTextChangedListener(watcher);

        firstName = view.findViewById(R.id.server_first_name_field);
        firstName.addTextChangedListener(watcher);

        lastName = view.findViewById(R.id.server_last_name_field);
        lastName.addTextChangedListener(watcher);

        email = view.findViewById(R.id.server_email_field);
        email.addTextChangedListener(watcher);

        gender = view.findViewById(R.id.radio_group_field);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                checkButtonStatus();
                chosenGender = ((RadioButton)(view.findViewById(gender.getCheckedRadioButtonId()))).getText().toString();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(
                        serverAddress.getText().toString(),
                        serverPort.getText().toString(),
                        username.getText().toString(),
                        password.getText().toString()
                );
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(
                        serverAddress.getText().toString(),
                        serverPort.getText().toString(),
                        username.getText().toString(),
                        password.getText().toString(),
                        firstName.getText().toString(),
                        lastName.getText().toString(),
                        email.getText().toString(),
                        chosenGender
                );
            }
        });
        checkButtonStatus();
        return view;
    }

    // checks to see if login and register buttons should be enabled or disabled
    private void checkButtonStatus() {
        if (serverAddress.getText().length() == 0 ||
                serverPort.getText().length() == 0 ||
                username.getText().length() == 0 ||
                password.getText().length() == 0) {
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);
        } else {
            loginButton.setEnabled(true);
            registerButton.setEnabled(true);
        }

        if (firstName.getText().length() == 0 ||
                lastName.getText().length() == 0 ||
                email.getText().length() == 0 ||
                gender.getCheckedRadioButtonId() == -1) {
            registerButton.setEnabled(false);
        }

    }

    private void loginUser(String host, String port, String username, String password) {
        Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                String error = bundle.getString("Message");
                if (!bundle.getBoolean("Success")) {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Welcome, " + dataCache.getUserFirstName()
                            + " " + dataCache.getUserLastName(), Toast.LENGTH_SHORT).show();
                    listener.notifyDone();
                }
            }
        };

        LoginTask task = new LoginTask(uiThreadMessageHandler, host, port, username, password);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task); // runnable here
    }

    private void registerUser(String host, String port, String username, String password,
                              String firstName, String lastName, String email, String gender) {
        Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                String error = bundle.getString("Message");
                if (!bundle.getBoolean("Success")) {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Welcome, " + dataCache.getUserFirstName()
                                    + " " + dataCache.getUserLastName(), Toast.LENGTH_SHORT).show();
                    listener.notifyDone();
                }
            }
        };

        RegisterTask task = new RegisterTask(uiThreadMessageHandler, host, port,
                username, password, firstName, lastName, email, gender);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task); // runnable here
    }
}