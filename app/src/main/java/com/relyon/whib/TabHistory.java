package com.relyon.whib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TabHistory extends Fragment {

    private ProfileActivity profileActivity;
    private User user;
    private RecyclerView rvComments;
    private RecyclerViewCommentAdapter adapter;
    private ProgressBar progressBar;
    private List<Comment> commentList;
    private TextView empty;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        profileActivity = (ProfileActivity) getActivity();
        rvComments = rootView.findViewById(R.id.rvComments);
        progressBar = rootView.findViewById(R.id.progress_bar);
        empty = rootView.findViewById(R.id.empty);
        spinner = rootView.findViewById(R.id.filters);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.comment_filters, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(filterAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (commentList != null && commentList.size() > 0) {
                    //sortComments();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (profileActivity != null) {
            user = profileActivity.getUser();
        }

        progressBar.setVisibility(View.VISIBLE);

        return rootView;
    }

    private void sortComments() {
        Collections.sort(commentList, new Comparator<Comment>() {
            @Override
            public int compare(Comment c1, Comment c2) {
                return String.valueOf(c1.getRating()).compareTo(String.valueOf(c2.getRating()));
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveMyComments();
    }

    private void retrieveMyComments() {
        if (Util.getUser().isExtra()) {
            commentList = Util.getUser().getCommentList();
            if (commentList != null && commentList.size() > 0) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rvComments.setLayoutManager(layoutManager);
                adapter = new RecyclerViewCommentAdapter(getContext(), profileActivity, true);
                rvComments.setAdapter(adapter);
                adapter.addAll(commentList, true, false, true);
            } else {
                empty.setVisibility(View.VISIBLE);
            }
        } else {
            Util.mServerDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Server> serverList = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Server server = snap.getValue(Server.class);
                        if (server != null && server.getTimeline() != null) {
                            serverList.add(server);
                        }
                    }
                    commentList = new ArrayList<>();
                    for (int i = 0; i < serverList.size(); i++) {
                        Server server = serverList.get(i);
                        int finalI = i;
                        Util.mServerDatabaseRef.child(server.getServerUID()).child("timeline").child("commentList").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Comment comment = snapshot1.getValue(Comment.class);
                                    if (comment != null && comment.getAuthorsUID().equals(user.getUserUID())) {
                                        comment.setCommentUID(snapshot1.getKey());
                                        commentList.add(comment);
                                    }
                                }
                                if (finalI == serverList.size() - 1) {
                                    progressBar.setVisibility(View.GONE);
                                    if (commentList.size() > 0) {
                                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                        rvComments.setLayoutManager(layoutManager);
                                        adapter = new RecyclerViewCommentAdapter(getContext(), profileActivity, true);
                                        rvComments.setAdapter(adapter);
                                        adapter.addAll(commentList, true, false, true);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}