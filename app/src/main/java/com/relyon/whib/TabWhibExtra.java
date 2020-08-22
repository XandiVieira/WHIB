package com.relyon.whib;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Advantage;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;

public class TabWhibExtra extends Fragment {

    private ListView resourceLV;
    private ArrayList<Advantage> advantages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_whib_extra, container, false);

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
}