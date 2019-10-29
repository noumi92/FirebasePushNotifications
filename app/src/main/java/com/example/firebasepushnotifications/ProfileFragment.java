package com.example.firebasepushnotifications;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private CircleImageView mProfileImage;
    private TextView mUserName;
    private Button mLogoutButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseUser mFirebaseUser;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        mProfileImage = (CircleImageView) view.findViewById(R.id.profile_image);
        mUserName = (TextView) view.findViewById(R.id.user_name);
        mLogoutButton = (Button) view.findViewById(R.id.log_out_button);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mFirebaseUser = mAuth.getCurrentUser();

        mFirestore.collection("users").document(mFirebaseUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task != null) {
                                String name = task.getResult().get("name").toString();
                                String image = task.getResult().get("image").toString();

                                mUserName.setText(name);
                                Glide.with(container.getContext()).load(image).into(mProfileImage);
                            }
                        }
                    }
                });
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> tokenRemoveMap = new HashMap<>();
                tokenRemoveMap.put("token_id", "");
                mFirestore.collection("users").document(mFirebaseUser.getUid()).update(tokenRemoveMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mAuth.signOut();
                                Intent intent = new Intent(container.getContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
            }
        });
        return view;
    }

}
