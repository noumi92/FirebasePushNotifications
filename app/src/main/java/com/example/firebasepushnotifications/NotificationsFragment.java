package com.example.firebasepushnotifications;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<Notification> mNotifications;
    private NotificationAdapter mNotificationAdapter;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;


    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notifications, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.notifications_recycler_view);

        mFirestore = FirebaseFirestore.getInstance();

        mNotifications = new ArrayList<>();
        mNotificationAdapter = new NotificationAdapter(container.getContext(), mNotifications);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mRecyclerView.setAdapter(mNotificationAdapter);

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mNotifications.clear();
        mFirestore.collection("notifications").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    Notification notification = documentChange.getDocument().toObject(Notification.class);
                                    mNotifications.add(notification);
                                    mNotificationAdapter.notifyDataSetChanged();
                                    Log.d("firebasepushapp", "Notifications Count: " + mNotificationAdapter.getItemCount());
                                }
                            }
                        }
                    }
                });
    }

}
