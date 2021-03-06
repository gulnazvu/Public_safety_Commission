package com.example.publicsafetycommission;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText name, passwordtext;
    Button btnLogin;
    ProgressBar simpleProgressBar;
    TextView signup, forgotPass, tokn;
    SharedPreferences sharedPreferences;
    private static final  String SHARED_PREF_NAME = "myPref";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name=findViewById(R.id.editTextTextPersonName);
        passwordtext=findViewById(R.id.editTextTextPassword);
        btnLogin=findViewById(R.id.button);
        simpleProgressBar= findViewById(R.id.progressBar);
        signup=findViewById(R.id.textView7);
        forgotPass=findViewById(R.id.forgetpassword);

        sharedPreferences =getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleProgressBar.setVisibility(View.VISIBLE);
                processdata(
                        name.getText().toString().trim(),
                        passwordtext.getText().toString().trim());
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signintent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(signintent);
            }
        });
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passintent = new Intent(LoginActivity.this, PassReset.class);
                startActivity(passintent);
            }
        });
    }
    public  void processdata(String user_name, String password)
    {
        Call<Responsemodel> call= apiController.getInstance()
                .getapi()
                .getlogin(user_name, password);

        call.enqueue(new Callback<Responsemodel>() {
            @Override
            public void onResponse(Call<Responsemodel> call, Response<Responsemodel> response) {
                simpleProgressBar.setVisibility(View.INVISIBLE);
                Boolean valid = true;
                if ( TextUtils.isEmpty(user_name) ) {
                    name.setError("Username is missing");
                    valid = false;
                }

                if ( TextUtils.isEmpty(password) ) {
                    passwordtext.setError("Password is missing");
                    valid = false;
                }

                if ( valid ) {
                    Responsemodel object = response.body();
                    if(object.getResponse()==1) {
                        name.setText("");
                        passwordtext.setText("");
                        int userId = object.getUser_id();

                        SharedPreferences sharedPreferences = getSharedPreferences("YOUR_PREF_NAME", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("user_id",userId);
                        editor.apply();
                        Intent myintent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(myintent);
                    } else {
                        Toast.makeText(getApplicationContext(), object.getResponseMsg(), Toast.LENGTH_LONG).show();
                        name.setText("");
                        passwordtext.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<Responsemodel> call, Throwable t) {
                simpleProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                name.setText("");
                passwordtext.setText("");
            }
        });



    }
}