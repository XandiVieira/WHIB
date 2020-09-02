package com.relyon.whib.activity.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.adapter.RecyclerViewProductAdapter;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.List;

public class TabStore extends Fragment {

    private List<Product> productList;

    private RecyclerView productsRV;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store, container, false);

        setLayoutAttributes(rootView);

        retrieveProducts();

        return rootView;
    }

    private void setLayoutAttributes(View rootView) {
        productsRV = rootView.findViewById(R.id.products);
        progressBar = rootView.findViewById(R.id.progress_bar);
    }

    private void retrieveProducts() {
        Util.mDatabaseRef.child(Constants.DATABASE_REF_PRODUCT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Product product = snap.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                setProductAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProductAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerViewProductAdapter adapter = new RecyclerViewProductAdapter(productList, getContext(), getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(productsRV.getContext(),
                layoutManager.getOrientation());
        productsRV.addItemDecoration(dividerItemDecoration);
        productsRV.setLayoutManager(layoutManager);
        productsRV.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        productsRV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}