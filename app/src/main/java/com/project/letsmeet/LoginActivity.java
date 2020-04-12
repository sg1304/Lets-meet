package com.project.letsmeet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText email;
    EditText password;
    TextView mStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.login_email_input);
        password = (EditText)findViewById(R.id.login_password_input);
        mStatusTextView = (TextView)findViewById(R.id.login_error_status);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void handleLogin(View view) {
            Log.d("Login", "Login:" + email);
            if (!validateForm()) {
                return;
            }

            //showProgressDialog();

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updatePage(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updatePage(null);
                        }
                        if (!task.isSuccessful()) {
                            mStatusTextView.setVisibility(View.VISIBLE);
                            mStatusTextView.setText(R.string.auth_failed);
                        }

                    }
                });
    }

    private void updatePage(FirebaseUser user) {
        //hideProgressDialog();
        if (user != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            email.setText("");
            password.setText("");
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email_temp = email.getText().toString();
        if (TextUtils.isEmpty(email_temp)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String password_temp = password.getText().toString();
        if (TextUtils.isEmpty(password_temp)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    public void handleSignup(View view) {

        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void handleForgotPassword(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
