package com.whirlwind.school1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.whirlwind.school1.BuildConfig;
import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.popup.ConfirmationPopup;
import com.whirlwind.school1.popup.TextPopup;

public class SigninActivity extends BaseActivity implements OnCompleteListener<AuthResult>, GoogleApiClient.OnConnectionFailedListener {

    // just a random value to exclude the possibility of clashing with fb's RCs
    private static final int RC_SIGNIN_GOOGLE = 42632;

    private EditText emailText, passwordText;
    private Button signinButton, signupButton;

    private Button anonymousButton, googleButton;
    private LoginButton facebookButton;

    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        emailText = findViewById(R.id.activity_signin_edittext_email);
        passwordText = findViewById(R.id.activity_signin_edittext_password);
        signinButton = findViewById(R.id.activity_signin_button_email_signin);
        signupButton = findViewById(R.id.activity_signin_button_email_signup);

        anonymousButton = findViewById(R.id.activity_signin_button_anonymous);
        googleButton = findViewById(R.id.activity_signin_button_google);
        facebookButton = findViewById(R.id.activity_signin_button_facebook);

        // Email signin
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signInWithEmailAndPassword(emailText.getText().toString(),
                        passwordText.getText().toString())
                        .addOnCompleteListener(SigninActivity.this);
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.createUserWithEmailAndPassword(emailText.getText().toString(),
                        passwordText.getText().toString())
                        .addOnCompleteListener(SigninActivity.this);
            }
        });

        // Auth provider signin
        anonymousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConfirmationPopup(R.string.warning_title, R.string.warning_anonymous_auth, new Runnable() {
                    @Override
                    public void run() {
                        auth.signInAnonymously();
                    }
                });
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient == null) {
                    GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                            .requestEmail()
                            .build();
                    googleApiClient = new GoogleApiClient.Builder(SigninActivity.this)
                            .enableAutoManage(SigninActivity.this, SigninActivity.this)
                            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                            .build();
                }

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGNIN_GOOGLE);
            }
        });

        callbackManager = CallbackManager.Factory.create();
        facebookButton.setReadPermissions("email", "public_profile");
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(SigninActivity.this, SigninActivity.this);
            }

            @Override
            public void onCancel() {
                // TODO: Better listeners
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGNIN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, this);
            } else
                onFailure(result.getStatus().toString());
        } else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
    }

    // Email onCompleteListener
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful())
            onSuccess(task.getResult().getUser());
        else
            onFailure(task.getException().getMessage());
    }

    private void onSuccess(UserInfo user) {
        finish();
    }

    private void onFailure(String string) {
        new TextPopup(R.string.error_title, string).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        new TextPopup(R.string.error_title, connectionResult.getErrorMessage()).show();
    }
}