package com.relyon.whib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.List;

public class TabGallery extends Fragment {

    private ProfileActivity profileActivity;
    private User user;
    private List<Product> stickersList = new ArrayList<>();
    private RecyclerView stickersRV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        profileActivity = (ProfileActivity) getActivity();
        stickersRV = rootView.findViewById(R.id.stickers);

        if (profileActivity != null) {
            user = profileActivity.getUser();
        }

        Util.mDatabaseRef.child("product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    stickersList.add(snap.getValue(Product.class));
                }
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
                RecyclerViewGalleryAdapter adapter = new RecyclerViewGalleryAdapter(stickersList, user.getProducts(), getContext(), getActivity());
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(stickersRV.getContext(),
                        layoutManager.getOrientation());
                stickersRV.addItemDecoration(dividerItemDecoration);
                stickersRV.setLayoutManager(layoutManager);
                stickersRV.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}