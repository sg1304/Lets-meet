package com.project.letsmeet;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email;
    FirebaseUser currentUser;
    TextView error_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = (EditText)findViewById(R.id.forgot_password_email_input);
        error_status = (TextView)findViewById(R.id.forgot_password_error_status);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

    public void resetPassword(View view) {
        if (!validateForm()) {
            Toast.makeText(ForgotPasswordActivity.this,"Email address is not valid, Please try again",Toast.LENGTH_SHORT).show();
            email.setText("");
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            error_status.setVisibility(View.VISIBLE);
                            error_status.setText("Password reset link has been sent to your email");
                            Log.d("Forgot Password", "Email sent.");
                            Toast.makeText(ForgotPasswordActivity.this,"Password reset link has been sent to your email",Toast.LENGTH_SHORT).show();
                            email.setText("");
                        }
                        else {
                            error_status.setVisibility(View.VISIBLE);
                            error_status.setText("Email is not registered with Let's Meet, Try again");
                            Toast.makeText(ForgotPasswordActivity.this, "Email is not registered with Let's Meet / Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

        return valid;
    }
}
