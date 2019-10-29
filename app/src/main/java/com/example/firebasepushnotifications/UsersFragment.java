package com.example.firebasepushnotifications;


import android.icu.text.ListFormatter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<User> mUsers;
    private UserAdapter mUserAdapter;
    private FirebaseFirestore mFirestore;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_users, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.users_recycler_view);

        mFirestore = FirebaseFirestore.getInstance();

        mUsers = new ArrayList<>();
        mUserAdapter = new UserAdapter(container.getContext(), mUsers);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mRecyclerView.setAdapter(mUserAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mUsers.clear();
        mFirestore.collection("users").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
            for(DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    String userId = documentChange.getDocument().getId();

                    User user = documentChange.getDocument().toObject(User.class).withId(userId);
                    mUsers.add(user);
                    mUserAdapter.notifyDataSetChanged();
                }
            }
            }
        });
    }
}
