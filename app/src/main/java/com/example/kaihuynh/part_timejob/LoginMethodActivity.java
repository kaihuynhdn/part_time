package com.example.kaihuynh.part_timejob;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.Common;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginMethodActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";

    private SignInButton mGoogleSignIn;
    private Button mCreateButton;
    private TextView mToLoginTextView;
    private ProgressDialog mProgress;
    private ListenerRegistration listenerRegistration;

    public static LoginMethodActivity sInstance = null;

    //Firebase instance variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseFirestore db;
    private CollectionReference mUserReference;

    //Google instance variables
    private GoogleApiClient mGoogleApiClient;

    //Fb Instance variables
    private LoginButton mFbLoginButton;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_method);

        addComponents();
        initialize();
        addEvents();

    }

    private void initialize() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Đang kiểm tra dữ liệu...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        db = FirebaseFirestore.getInstance();
        mUserReference = db.collection("users");

        googleButtonUi();
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    listenerRegistration = mUserReference.document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                            if(documentSnapshot!=null && documentSnapshot.exists()){
                                User u = documentSnapshot.toObject(User.class);
                                UserManager.getInstance().load(u);
                                if (mProgress.isShowing()) {
                                    mProgress.dismiss();
                                }
                                Common.currentToken = FirebaseInstanceId.getInstance().getToken();
                                if (u != null) {
                                    startActivity(new Intent(LoginMethodActivity.this, HomePageActivity.class));
                                    finish();
                                    LoginActivity.getInstance().finish();
                                }
                            }
                        }
                    });

                } else {

                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mCallbackManager = CallbackManager.Factory.create();
        mFbLoginButton.setReadPermissions("email", "public_profile");
        mFbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mProgress.show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                if (mProgress.isShowing()) {
                    mProgress.dismiss();
                }
            }
        });

    }

    private void addEvents() {
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginMethodActivity.this, RegisterAccountInfoActivity.class));
            }
        });

        mToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void addComponents() {
        mFbLoginButton = findViewById(R.id.btn_fb_login);
        mCreateButton = findViewById(R.id.btn_create);
        mToLoginTextView = findViewById(R.id.tv_toLogin_method);
        mGoogleSignIn = findViewById(R.id.btn_google_login);
        sInstance = this;
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googleButtonUi() {
        mGoogleSignIn.setSize(SignInButton.SIZE_STANDARD);
        for (int i = 0; i < mGoogleSignIn.getChildCount(); i++) {
            View v = mGoogleSignIn.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(14);
                tv.setText("ĐĂNG NHẬP BẰNG GOOGLE");
                tv.setSingleLine(true);
                tv.setPadding(15, 17, 20, 17);
                return;
            }
        }
    }

    public static LoginMethodActivity getInstance() {
        if (sInstance == null) {
            sInstance = new LoginMethodActivity();
        }
        return sInstance;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(LoginMethodActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(LoginMethodActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                if (mProgress.isShowing()) {
                    mProgress.dismiss();
                }
                // ...
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
        if (listenerRegistration!=null){
            listenerRegistration.remove();
        }
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mProgress.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser userFirebase = mAuth.getCurrentUser();
//                            mUserReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                                    if (documentSnapshots != null) {
//                                        for (DocumentChange document : documentSnapshots.getDocumentChanges()) {
//                                            User u = document.getDocument().toObject(User.class);
//                                            if (u.getId().equals(userFirebase.getUid())) {
//                                                return;
//                                            }
//                                        }
//                                    }
//                                    User user = new User();
//                                    user.setId(userFirebase.getUid().toString());
//                                    user.setEmail(userFirebase.getEmail());
//                                    user.setFullName(userFirebase.getDisplayName());
//                                    mUserReference.document(userFirebase.getUid()).set(user);
//                                }
//                            });

                            mUserReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && task.getResult()!=null){
                                        for (DocumentSnapshot d : task.getResult()){
                                            User u = d.toObject(User.class);
                                            if (u.getId().equals(userFirebase.getUid())) {
                                                return;
                                            }
                                        }
                                    }
                                    User user = new User();
                                    user.setId(userFirebase.getUid().toString());
                                    user.setEmail(userFirebase.getEmail());
                                    user.setFullName(userFirebase.getDisplayName());
                                    UserManager.getInstance().updateUser(user);
//                                    mUserReference.document(userFirebase.getUid()).set(user);
                                }
                            });
                        }

                        // ...
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser userFirebase = mAuth.getCurrentUser();
//                            mUserReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                                    if (documentSnapshots != null) {
//                                        for (DocumentChange document : documentSnapshots.getDocumentChanges()) {
//                                            User u = document.getDocument().toObject(User.class);
//                                            if (u.getId().equals(userFirebase.getUid())) {
//                                                return;
//                                            }
//                                        }
//                                    }
//                                    User user = new User();
//                                    user.setId(userFirebase.getUid().toString());
//                                    user.setEmail(userFirebase.getEmail());
//                                    user.setFullName(userFirebase.getDisplayName());
//                                    mUserReference.document(userFirebase.getUid()).set(user);
//                                }
//                            });

                            mUserReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && task.getResult()!=null){
                                        for (DocumentSnapshot d : task.getResult()){
                                            User u = d.toObject(User.class);
                                            if (u.getId().equals(userFirebase.getUid())) {
                                                return;
                                            }
                                        }
                                    }
                                    User user = new User();
                                    user.setId(userFirebase.getUid().toString());
                                    user.setEmail(userFirebase.getEmail());
                                    user.setFullName(userFirebase.getDisplayName());
                                    UserManager.getInstance().updateUser(user);
//                                    mUserReference.document(userFirebase.getUid()).set(user);
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginMethodActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


}
