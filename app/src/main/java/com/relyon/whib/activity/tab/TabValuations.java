package com.relyon.whib.activity.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.relyon.whib.R;
import com.relyon.whib.activity.ProfileActivity;
import com.relyon.whib.modelo.User;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class TabValuations extends Fragment {

    private User user;

    private MaterialRatingBar ratingBar;
    private TextView rating;
    private TextView goodValuation;
    private TextView mediumValuation;
    private TextView badValuation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_valuations, container, false);

        ProfileActivity profileActivity = (ProfileActivity) getActivity();

        setLayoutAttributes(rootView);

        if (profileActivity != null) {
            user = profileActivity.getUser();
            setUserRating();
        }

        return rootView;
    }

    private void setLayoutAttributes(View rootView) {
        ratingBar = rootView.findViewById(R.id.my_stars);
        rating = rootView.findViewById(R.id.my_rating);
        goodValuation = rootView.findViewById(R.id.good_valuation);
        mediumValuation = rootView.findViewById(R.id.medium_valuation);
        badValuation = rootView.findViewById(R.id.bad_valuation);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUserRating() {
        ratingBar.setStepSize(0.01f);
        if (user.getValuation().getSumOfRatings() != 0 && user.getValuation().getNumberOfRatings() != 0) {
            ratingBar.setRating(user.getValuation().getSumOfRatings() / user.getValuation().getNumberOfRatings());
            rating.setText(String.format("%.2f", user.getValuation().getSumOfRatings() / user.getValuation().getNumberOfRatings()));
        } else {
            ratingBar.setRating(0);
            rating.setText(String.format("%.2f", 0f));
        }
        ratingBar.setIsIndicator(true);
        goodValuation.setText(user.getValuation().getGoodPercentage() + "%");
        mediumValuation.setText(user.getValuation().getMediumPercentage() + "%");
        badValuation.setText(user.getValuation().getBadPercentage() + "%");
    }
}