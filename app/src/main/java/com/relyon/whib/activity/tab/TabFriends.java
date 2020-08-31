package com.relyon.whib.activity.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.relyon.whib.R;
import com.relyon.whib.activity.ProfileActivity;
import com.relyon.whib.modelo.User;

public class TabFriends extends Fragment {

    private ProfileActivity profileActivity;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        setLayoutAttributes();

        if (profileActivity != null) {
            user = profileActivity.getUser();
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setLayoutAttributes() {
        profileActivity = (ProfileActivity) getActivity();
    }
}