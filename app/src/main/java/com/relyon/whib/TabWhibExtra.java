package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Advantage;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.util.SelectSubscription;

import java.util.ArrayList;

public class TabWhibExtra extends Fragment implements BillingProcessor.IBillingHandler, SelectSubscription {

    private ListView resourceLV;
    private ArrayList<Advantage> advantages;
    private BillingProcessor billingProcessor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_whib_extra, container, false);

        billingProcessor = new BillingProcessor(getContext(), getResources().getString(R.string.google_license_key), this);
        billingProcessor.initialize();

        resourceLV = rootView.findViewById(R.id.resourceLV);
        Button subscribe = rootView.findViewById(R.id.signWhibExtraBT);

        subscribe.setOnClickListener(v -> {
            FragmentTransaction fm = ((getActivity()).getSupportFragmentManager().beginTransaction());
            DialogChooseSubscription dialog = DialogChooseSubscription.newInstance(getContext());
            dialog.show(fm, "");
        });

        Util.mAdvantagesDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                advantages = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    advantages.add(snap.getValue(Advantage.class));
                }
                ArrayAdapter adapter = new AdvantageAdapter(getContext(), advantages);
                resourceLV.setAdapter(adapter);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("extra").setValue(true);
        Util.getUser().setExtra(true);
        DialogCongratsSubscription dialogCongratsSubscription = new DialogCongratsSubscription(getActivity());
        dialogCongratsSubscription.show();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    private void purchase(String sku) {
        billingProcessor.subscribe(getActivity(), sku);
    }

    @Override
    public void onChoose(String sku) {
        purchase(sku);
    }
}