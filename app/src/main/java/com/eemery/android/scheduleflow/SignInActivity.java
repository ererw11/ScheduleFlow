package com.eemery.android.scheduleflow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 11;
    private static final String TAG = SignInActivity.class.getSimpleName();

    private MaterialButton signInButton;

    private FirebaseAuth firebaseAuth;

    public static Intent createIntent(Context context) {
        return new Intent(context, SignInActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInButton = findViewById(R.id.sign_in_button);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(AppointmentListActivity.newIntent(getApplicationContext()));
            finish();
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        AuthUI.getInstance().createSignInIntentBuilder()
                                .build(),
                        RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
        }
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        Log.d(TAG, "handleSignInResponse: " + response);
        // Successfully signed in
        if (resultCode == RESULT_OK) {
            startActivity(AppointmentListActivity.newIntent(getApplicationContext()));
            finish();
        } else {
            // Sign In Failed
            if (response == null) {
                // User pressed back button
                Toast.makeText(this, "Sign In Cancelled", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
