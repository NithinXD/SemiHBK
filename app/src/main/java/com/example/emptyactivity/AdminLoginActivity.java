package com.example.emptyactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText editTextAdminEmail, editTextAdminPassword;
    private Button buttonAdminLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        editTextAdminEmail = findViewById(R.id.editTextAdminEmail);
        editTextAdminPassword = findViewById(R.id.editTextAdminPassword);
        buttonAdminLogin = findViewById(R.id.buttonAdminLogin);

        buttonAdminLogin.setOnClickListener(v -> {
            String email = editTextAdminEmail.getText().toString();
            String password = editTextAdminPassword.getText().toString();

            if (email.equals("srisa@gmail.com") && password.equals("sri@123")) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Intent i = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
                                startActivity(i);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminLoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(AdminLoginActivity.this, "Invalid Admin Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
