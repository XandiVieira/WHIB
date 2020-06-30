package com.relyon.whib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Advantage;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.util.IabBroadcastReceiver;
import com.relyon.whib.util.IabHelper;
import com.relyon.whib.util.IabResult;
import com.relyon.whib.util.Inventory;
import com.relyon.whib.util.Purchase;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.relyon.whib.util.Constants.SKU_WHIB_MONTHLY;
import static com.relyon.whib.util.Constants.SKU_WHIB_SIXMONTH;
import static com.relyon.whib.util.Constants.SKU_WHIB_YEARLY;
import static com.relyon.whib.util.Constants.base64EncodedPublicKey;

public class TabValuations extends Fragment {
    
    private MaterialRatingBar ratingBar;
    private TextView rating;
    private TextView goodValuation;
    private TextView mediumValuation;
    private TextView badValuation;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_valuations, container, false);

        ProfileActivity profileActivity = (ProfileActivity) getActivity();

        ratingBar = rootView.findViewById(R.id.my_stars);
        rating = rootView.findViewById(R.id.my_rating);
        goodValuation = rootView.findViewById(R.id.good_valuation);
        mediumValuation = rootView.findViewById(R.id.medium_valuation);
        badValuation = rootView.findViewById(R.id.bad_valuation);

        if (profileActivity != null) {
            user = profileActivity.getUser();
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadData(){
        ratingBar.setStepSize(0.01f);
        if (user.getValuation().getSumOfRatings() != 0 && user.getValuation().getNumberOfRatings() != 0) {
            ratingBar.setRating(user.getValuation().getSumOfRatings() / user.getValuation().getNumberOfRatings());
            rating.setText(String.format("%.2f", user.getValuation().getSumOfRatings() / user.getValuation().getNumberOfRatings()));
        } else {
            ratingBar.setRating(0);
            rating.setText(String.format("%.2f", 0));
        }
        ratingBar.setIsIndicator(true);
        goodValuation.setText(user.getValuation().getGoodPercentage() + "%");
        mediumValuation.setText(user.getValuation().getMediumPercentage() + "%");
        badValuation.setText(user.getValuation().getBadPercentage() + "%");
    }
}