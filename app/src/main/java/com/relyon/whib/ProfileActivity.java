package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.relyon.whib.modelo.Report;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

public class ProfileActivity extends AppCompatActivity {

    private EditText nick;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView back = findViewById(R.id.back);
        ImageView photo = findViewById(R.id.photo);
        TextView usernamme = findViewById(R.id.userName);
        nick = findViewById(R.id.nick);
        RatingBar ratingBar = findViewById(R.id.my_stars);
        TextView rating = findViewById(R.id.my_rating);
        TextView goodValuation = findViewById(R.id.good_valuation);
        TextView mediumValuation = findViewById(R.id.medium_valuation);
        TextView badValuation = findViewById(R.id.bad_valuation);
        TextView sentReports = findViewById(R.id.sent_reports);
        TextView receivedReports = findViewById(R.id.received_reports);

        if (Util.getUser() != null) {
            user = Util.getUser();
        } else {
            //user =
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getNickName() == null && !nick.getText().toString().equals("")) {
                    user.setNickName(nick.getText().toString());
                    Util.getmUserDatabaseRef().child(user.getUserUID()).setValue(user);
                }
                onBackPressed();
            }
        });

        Glide.with(getApplicationContext())
                .load(user.getPhotoPath())
                .apply(RequestOptions.circleCropTransform())
                .into(photo);

        usernamme.setText(user.getUserName());
        ratingBar.setProgress((int) (user.getRating() * 2));
        ratingBar.setIsIndicator(true);
        rating.setText(String.format("%.2f", user.getRating()));
        goodValuation.setText(user.getValuation().getGoodPercentage() + "%");
        mediumValuation.setText(user.getValuation().getMediumPercentage() + "%");
        badValuation.setText(user.getValuation().getBadPercentage() + "%");
        if (user.getReportList() != null) {
            int sent = 0;
            int received = 0;
            for (Report report : user.getReportList()) {
                if (report.getUserSenderUID().equals(user.getUserUID())) {
                    sent++;
                    sentReports.setText(String.valueOf(sent));
                } else {
                    received++;
                    receivedReports.setText(String.valueOf(received));
                }
            }
        }

        if (user.getNickName() != null) {
            nick.setEnabled(false);
        } else {
            nick.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}