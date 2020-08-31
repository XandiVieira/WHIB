package com.relyon.whib.activity.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.activity.ProfileActivity;
import com.relyon.whib.adapter.RecyclerViewGalleryAdapter;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.User;
import com.relyon.whib.util.Util;
import com.relyon.whib.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class TabGallery extends Fragment {

    private User user;
    private List<Product> stickersList;

    private RecyclerView rvStickers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        ProfileActivity profileActivity = (ProfileActivity) getActivity();
        rvStickers = rootView.findViewById(R.id.stickers);

        if (profileActivity != null) {
            user = profileActivity.getUser();
        }

        Util.mDatabaseRef.child(Constants.DATABASE_REF_PRODUCT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stickersList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    stickersList.add(snap.getValue(Product.class));
                }
                setProductAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void setProductAdapter() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        RecyclerViewGalleryAdapter adapter = new RecyclerViewGalleryAdapter(stickersList, user.getProducts(), getContext(), true, false, null, null, null);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvStickers.getContext(),
                layoutManager.getOrientation());
        rvStickers.addItemDecoration(dividerItemDecoration);
        rvStickers.setLayoutManager(layoutManager);
        rvStickers.setAdapter(adapter);
    }
}