package com.example.troublesgetaway;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.troublesgetaway.admin.AdminMainActivity;
import com.example.troublesgetaway.comune.ComuneMainActivity;
import com.example.troublesgetaway.utente.UtenteMainActivity;
import com.example.troublesgetaway.data.model.LoginResponse;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    EditText usernameTxt, passwordTxt;
    Button loginBtn;
    Intent intent;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameTxt = findViewById(R.id.username);
        passwordTxt = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login_btn);
        register = findViewById(R.id.registrationButton);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registrazione = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registrazione);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameTxt.getText().toString();
                String password = passwordTxt.getText().toString();
                if (!TextUtils.isEmpty(username.trim()) && !TextUtils.isEmpty(password.trim())) {
                    tryLogin(username, password);
                } else {
                    showMessage(R.string.missing_element);
                }
            }
        });
    }

    private void showMessage(int text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text)
                .setPositiveButton(R.string.OK_Button_Text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();


    }


    private void tryLogin(String username, String password) {
        MyApiService apiService = RetrofitService.getInstance();
        apiService.login(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                //risposta affermativa
                if (response.isSuccessful()) {
                    LoginResponse resp = response.body();
                    if (resp.success) {
                        switch (resp.tipoUtente) {
                            case 0: // CASO UTENTE
                                intent = new Intent(LoginActivity.this, UtenteMainActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            case 1: // CASO COMUNE
                                intent = new Intent(LoginActivity.this, ComuneMainActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            case 2: // CASO ADMIN
                                intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                        }
                    } else {
                        showMessage(R.string.invalidLoginParameters);
                    }

                } else {
                    response.errorBody();
                }
            }

            @Override
            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
                showMessage(R.string.Connection_Error);
            }
        });
    }
}
