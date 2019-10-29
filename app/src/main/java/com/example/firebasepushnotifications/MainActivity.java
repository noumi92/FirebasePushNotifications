package com.example.firebasepushnotifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private TextView mProfileLabel;
    private TextView mUsersLabel;
    private TextView mNotificationsLabel;
    private ViewPager mMainPager;
    private PagerViewAdapter mPagerViewAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.default_channel));

        mAuth = FirebaseAuth.getInstance();

        mProfileLabel = (TextView) findViewById(R.id.profile);
        mUsersLabel = (TextView) findViewById(R.id.all_users);
        mNotificationsLabel = (TextView) findViewById(R.id.notifications);

        mMainPager = (ViewPager) findViewById(R.id.main_pager);

        mPagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        mMainPager.setAdapter(mPagerViewAdapter);
        mMainPager.setOffscreenPageLimit(2);

        mMainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mProfileLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPager.setCurrentItem(0);
            }
        });
        mUsersLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPager.setCurrentItem(1);
            }
        });
        mNotificationsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPager.setCurrentItem(2);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser == null){
            sendBackToLogin();
        }
    }

    private void sendBackToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void changeTabs(int position) {
        if(position == 0){
            mProfileLabel.setTextColor(getColor(R.color.textTabBright));
            mProfileLabel.setTextSize(22);

            mUsersLabel.setTextColor(getColor(R.color.textTabLight));
            mUsersLabel.setTextSize(16);

            mNotificationsLabel.setTextColor(getColor(R.color.textTabLight));
            mNotificationsLabel.setTextSize(16);
        }
        if(position == 1){
            mProfileLabel.setTextColor(getColor(R.color.textTabLight));
            mProfileLabel.setTextSize(16);

            mUsersLabel.setTextColor(getColor(R.color.textTabBright));
            mUsersLabel.setTextSize(22);

            mNotificationsLabel.setTextColor(getColor(R.color.textTabLight));
            mNotificationsLabel.setTextSize(16);
        }
        if(position == 2){
            mProfileLabel.setTextColor(getColor(R.color.textTabLight));
            mProfileLabel.setTextSize(16);

            mUsersLabel.setTextColor(getColor(R.color.textTabLight));
            mUsersLabel.setTextSize(16);

            mNotificationsLabel.setTextColor(getColor(R.color.textTabBright));
            mNotificationsLabel.setTextSize(22);
        }
    }
}
