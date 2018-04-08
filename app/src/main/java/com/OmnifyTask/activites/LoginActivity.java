package com.OmnifyTask.activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.OmnifyTask.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String UNIQUE_ID = "UNIQUE_ID";
    private static final String TAG = "FirebasePhoneNumAuth";
    private static final int RC_SIGN_IN = 007;
    private static String uniqueIdentifier = null;
    LinearLayout otp_parent_layout;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth firebaseAuth;
    private String phoneNumber;
    private Button sendCodeButton;
    private Button verifyCodeButton, nextactivity;
    private EditText phoneNum;
    private EditText verifyCodeET;
    private FirebaseFirestore firestoreDB;
    private FirebaseUser firebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInButton btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        inti();
        clientBuilder();

        firebaseAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();

        createCallback();
        getInstallationIdentifier();
        getVerificationDataFromFirestoreAndVerify(null);

    }

    private void clientBuilder() {

        // Intilize the clientBuilder for the google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(LoginActivity.this, LoginActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
    }

    private void inti() {

        // intilize view and set onclicklistner for buttons

        phoneNum = findViewById(R.id.phone);
        verifyCodeET = findViewById(R.id.phone_auth_code);
        otp_parent_layout = findViewById(R.id.otp_parent_layout);
        otp_parent_layout.setVisibility(View.INVISIBLE);

        btnSignIn = findViewById(R.id.googleLoginbutton);
        btnSignIn.setOnClickListener(this);

        sendCodeButton = findViewById(R.id.send_code_b);
        verifyCodeButton = findViewById(R.id.verify_code_b);

        nextactivity = findViewById(R.id.nextactivity);
        sendCodeButton.setOnClickListener(this);
        verifyCodeButton.setOnClickListener(this);
        nextactivity.setOnClickListener(this);
    }


    private void createCallback() {

        // firebase phone login callback listners
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.e(TAG, "verification completed" + credential);
                signInWithPhoneAuthCredential(credential);
                Toast.makeText(LoginActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e(TAG, "verification failed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    phoneNum.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(LoginActivity.this,
                            "Trying too many timeS",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                otp_parent_layout.setVisibility(View.VISIBLE);
                Log.e(TAG, "code sent " + verificationId);
                Toast.makeText(LoginActivity.this, "code sent ", Toast.LENGTH_SHORT).show();

            }
        };
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        //validate the phone number .This will check for the empty edittext
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNum.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    private void verifyPhoneNumberInit() {
        // validate the phone number
        phoneNumber = phoneNum.getText().toString();
        if (!validatePhoneNumber(phoneNumber)) {
            return;
        }
        verifyPhoneNumber(phoneNumber);

    }

    private void verifyPhoneNumber(String phno) {
        // calling the API
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phno, 70,
                TimeUnit.SECONDS, this, callbacks);
    }

    private void verifyPhoneNumberCode() {
        final String phone_code = verifyCodeET.getText().toString();
        getVerificationDataFromFirestoreAndVerify(phone_code);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        //verify the Code sent to the PhoneNumber
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "code verified signIn successful");
                            firebaseUser = task.getResult().getUser();
                        } else {
                            Log.e(TAG, "code verification failed", task.getException());
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                verifyCodeET.setError("Invalid code.");
                            }
                        }
                    }
                });
    }

    private void createCredentialSignIn(String verificationId, String verifyCode) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verifyCode);
        signInWithPhoneAuthCredential(credential);
    }

    private void signOut() {
        firebaseAuth.signOut();

    }


    private void getVerificationDataFromFirestoreAndVerify(final String code) {

        // Verify the OTP and send to the firestoreDB
        firestoreDB.collection("phoneAuth").document(uniqueIdentifier)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot ds = task.getResult();
                            if (ds.exists()) {

                                if (code != null) {
                                    createCredentialSignIn(ds.getString("verificationId"), code);
                                } else {
                                    verifyPhoneNumber(ds.getString("phone"));
                                }
                            } else {
                                Log.e(TAG, "Code hasn't been sent yet");
                            }

                        } else {
                            Log.e(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }

    public synchronized String getInstallationIdentifier() {
        if (uniqueIdentifier == null) {
            SharedPreferences sharedPrefs = this.getSharedPreferences(
                    UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueIdentifier = sharedPrefs.getString(UNIQUE_ID, null);
            if (uniqueIdentifier == null) {
                uniqueIdentifier = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(UNIQUE_ID, uniqueIdentifier);
                editor.commit();
            }
        }
        return uniqueIdentifier;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.googleLoginbutton:
                signIn();
                break;


            case R.id.send_code_b:
                verifyPhoneNumberInit();
                break;

            case R.id.verify_code_b:
                verifyPhoneNumberCode();
                break;


            case R.id.nextactivity:

                Intent articlelist = new Intent(LoginActivity.this, ArticleListView.class);
                startActivity(articlelist);
                break;
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {


        Log.e("Result g Login", String.valueOf(result));

        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();

            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);

            Toast.makeText(getApplicationContext(), "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl, Toast.LENGTH_LONG).show();

        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

}
