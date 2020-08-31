package com.relyon.whib.activity.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.adapter.AdvantageAdapter;
import com.relyon.whib.dialog.DialogChooseSubscription;
import com.relyon.whib.modelo.Advantage;
import com.relyon.whib.util.Util;

import java.util.ArrayList;

public class TabWhibExtra extends Fragment {

    private ArrayList<Advantage> advantages;

    private ListView resourceLV;
    private Button subscribe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_whib_extra, container, false);

        setLayoutAttributes(rootView);

        subscribe.setOnClickListener(v -> {
            FragmentTransaction fm = ((getActivity()).getSupportFragmentManager().beginTransaction());
            DialogChooseSubscription dialog = DialogChooseSubscription.newInstance();
            dialog.show(fm, "");
        });

        Util.mAdvantagesDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                advantages = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    advantages.add(snap.getValue(Advantage.class));
                }
                setAdvantageAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void setAdvantageAdapter() {
        AdvantageAdapter adapter = new AdvantageAdapter(getContext(), advantages);
        resourceLV.setAdapter(adapter);
    }

    private void setLayoutAttributes(View rootView) {
        resourceLV = rootView.findViewById(R.id.lv_resources);
        subscribe = rootView.findViewById(R.id.signWhibExtraBT);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}