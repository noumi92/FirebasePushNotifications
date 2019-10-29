package com.example.firebasepushnotifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SendActivity extends AppCompatActivity {
    private String mUserId;
    private String mReceiverId;
    private String mUserName;
    private TextView mUserNameView;
    private EditText mMessageView;
    private Button mNotifyButton;
    private ProgressBar mProgressBar;
    private FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        mUserNameView = (TextView) findViewById(R.id.user_id_view);
        mMessageView = (EditText) findViewById(R.id.notification_text_view);
        mNotifyButton = (Button) findViewById(R.id.send_notification_button);
        mProgressBar = (ProgressBar) findViewById(R.id.send_notification_progress_bar);
        mFirestore = FirebaseFirestore.getInstance();

        mUserId = FirebaseAuth.getInstance().getUid();
        mReceiverId = getIntent().getStringExtra("userId");
        mUserName = getIntent().getStringExtra("userName");

        mUserNameView.setText("Send to: " + mUserName);

        mNotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                String message = mMessageView.getText().toString();
                if(!TextUtils.isEmpty(message)){
                    String id = UUID.randomUUID().toString();
                    Notification notification = new Notification(mUserId, mReceiverId, message, id);
                    mFirestore.collection("notifications").document(id).set(notification)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    mMessageView.setText("");
                                    mMessageView.clearFocus();
                                    Toast.makeText(SendActivity.this, "Notification Sent", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(SendActivity.this, "Notification Sent Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
            }
        });
    }
}
