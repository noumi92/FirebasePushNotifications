package com.example.firebasepushnotifications;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    private List<User> mUsers;
    private Context mContext;
    public UserAdapter(Context context, List<User> users) {
        mUsers = users;
        mContext = context;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        final String userName = mUsers.get(position).getName();
        holder.mUserNameView.setText(userName);
        CircleImageView user_image_view = holder.mProfilaImage;
        Glide.with(mContext).load(mUsers.get(position).getImage()).into(user_image_view);
        final String userId = mUsers.get(position).mUserId;
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(mContext, SendActivity.class);
                sendIntent.putExtra("userName", userName);
                sendIntent.putExtra("userId", userId);
                mContext.startActivity(sendIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder{
        private View mView;
        private CircleImageView mProfilaImage;
        private TextView mUserNameView;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mProfilaImage = (CircleImageView) mView.findViewById(R.id.profile_image);
            mUserNameView = (TextView) mView.findViewById(R.id.user_name);

        }
    }
}
