package com.example.firebasepushnotifications;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private EditText mNameInput;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private Button mLoginButton;
    private Button mRegisterButton;
    private CircleImageView mProfileImage;
    private Uri mImageUri;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private FirebaseFirestore mFirestore;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNameInput = (EditText) findViewById(R.id.register_name);
        mEmailInput = (EditText) findViewById(R.id.register_email);
        mPasswordInput = (EditText) findViewById(R.id.register_password);
        mProfileImage = (CircleImageView) findViewById(R.id.register_image);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mProgressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
        mImageUri = null;
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("images");

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mImageUri != null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    final String name = mNameInput.getText().toString();
                    String email = mEmailInput.getText().toString();
                    String password = mPasswordInput.getText().toString();
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull final Task<AuthResult> authTask) {
                                if (authTask.isSuccessful()) {
                                    final String userId = mAuth.getCurrentUser().getUid();
                                    final StorageReference userProfile = mStorage.child(userId + ".jpg");
                                    userProfile.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadTask) {
                                            if (uploadTask.isSuccessful()) {
                                                userProfile.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> Urltask) {
                                                        if(Urltask.isSuccessful()) {
                                                            final String downloadUrl = Urltask.getResult().toString();
                                                            FirebaseInstanceId.getInstance().getInstanceId()
                                                                    .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                                                        @Override
                                                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                                                            String tokenId = instanceIdResult.getToken();
                                                                            Map<String, Object> userMap = new HashMap<>();
                                                                            userMap.put("name", name);
                                                                            userMap.put("image", downloadUrl);
                                                                            userMap.put("token_id", tokenId);
                                                                            mFirestore.collection("users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> firestoreTask) {
                                                                                    if (firestoreTask.isSuccessful()) {
                                                                                        sendToMain();
                                                                                    } else {
                                                                                        Toast.makeText(RegisterActivity.this, "Firestore Error: " + firestoreTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                                        mProgressBar.setVisibility(View.INVISIBLE);
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Storage Error: " + uploadTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                mProgressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Auth Error: " + authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void sendToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE){
            mImageUri = data.getData();
            mProfileImage.setImageURI(mImageUri);
            Log.d("firebasepushapp", "imageuri" + mImageUri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            sendToMain();
        }
    }
}
