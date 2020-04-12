package com.project.letsmeet;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {


    EditText fname;
    EditText lname;
    EditText email;
    EditText password;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fname = (EditText)findViewById(R.id.sign_up_first_name);
        lname = (EditText)findViewById(R.id.sign_up_last_name);
        email = (EditText)findViewById(R.id.sign_up_email_input);
        password = (EditText)findViewById(R.id.sign_up_password_input);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

    }

    public void handleCreateuser(View view) {
        Log.d("SignUp", "createAccount:" + email.getText().toString());
        if (!validateForm()) {
            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
            Log.d("SignUperror", "createAccount:" + email.getText().toString());
            return;
        }

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("SignUpValidation", "createAccount:" + email.getText().toString());
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            User user = getFields();
                            String currentUserId = mAuth.getCurrentUser().getUid();
                            rootRef.child("Users").child(currentUserId).setValue(user);
                            getToken();
                            Log.d("SignUp", "createUserWithEmail success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            updateUI(currentUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("SignUp", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });


    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    public void handleSignuptoLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private User getFields(){
        String firstName = fname.getText().toString();
        String lastName = lname.getText().toString();
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        String image = "NO_IMAGE";
        User user = new User(firstName, lastName, mail, pass, image);
        return user;
    }

    public void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            saveToken(token);
                        } else {

                        }
                    }
                });
    }
    private void saveToken(String token) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserId).child("token").setValue(token);
    }

}
