package com.example.firebasepushnotifications;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder>{
    private List<Notification> mNotifications;
    private FirebaseFirestore mFirestore;
    private Context mContext;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        mNotifications = notifications;
        mFirestore = FirebaseFirestore.getInstance();
        mContext = context;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifications_list, parent, false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationHolder holder, final int position) {
        final Notification notification = mNotifications.get(position);
       String senderId = notification.getSenderId();
        Log.d("firebasepushapp", "Sender ID: " + senderId);
       mFirestore.collection("users").document(senderId).get()
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                       String name = documentSnapshot.getString("name");
                       String image = documentSnapshot.getString("image");
                       holder.mMessageView.setText(notification.getMessage());
                       holder.mUserNameView.setText(name);
                       holder.onBind(notification, name, position);
                       RequestOptions requestOptions = new RequestOptions();
                       requestOptions.placeholder(R.mipmap.ic_launcher_flower);

                       Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(image).into(holder.mProfilaImage);
                   }
               });
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    class NotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CircleImageView mProfilaImage;
        private TextView mUserNameView;
        private TextView mMessageView;
        private Notification mNotification;
        private int mPosition;
        private String mName;
        NotificationHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mProfilaImage = (CircleImageView) itemView.findViewById(R.id.profile_image);
            mUserNameView = (TextView) itemView.findViewById(R.id.user_name);
            mMessageView = (TextView) itemView.findViewById(R.id.notification_body);
        }
        @Override
        public void onClick(final View view) {
            Log.d("firebasepushapp", "data: " + mName + mNotification.getNotificationId());
            new AlertDialog.Builder(mContext)
                    .setTitle(mName)
                    .setMessage(mNotification.getMessage())
                    .setIcon(R.mipmap.ic_launcher_flower)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mFirestore.collection("notifications").document(mNotification.getNotificationId()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(mContext, "Notification Deleted", Toast.LENGTH_LONG).show();
                                    notifyItemRemoved(mPosition);
                                    notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Notification Delete Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).show();
        }

        void onBind(Notification notification, String name, int position) {
            mNotification = notification;
            mName = name;
            mPosition = position;
        }
    }
}
