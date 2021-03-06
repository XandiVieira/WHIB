package com.relyon.whib.activity.tab;

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
import com.relyon.whib.R;
import com.relyon.whib.activity.ProfileActivity;
import com.relyon.whib.adapter.RecyclerViewCommentAdapter;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.User;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabHistory extends Fragment {

    private ProfileActivity profileActivity;
    private User user;
    private RecyclerViewCommentAdapter commentAdapter;
    private List<Comment> fixedCommentList;
    private List<Comment> commentList;

    private RecyclerView rvComments;
    private ProgressBar progressBar;
    private TextView empty;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        profileActivity = (ProfileActivity) getActivity();

        setLayoutAttributes(rootView);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.comment_filters, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(filterAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (commentList != null && commentList.size() > 0) {
                    sortComments(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (profileActivity != null) {
            user = profileActivity.getUser();
            if (user != null && Util.getUser() != null && !user.getUserUID().equals(Util.getUser().getUserUID())) {
                empty.setVisibility(View.GONE);
            }
        }

        progressBar.setVisibility(View.VISIBLE);

        return rootView;
    }

    private void setLayoutAttributes(View rootView) {
        rvComments = rootView.findViewById(R.id.rvComments);
        progressBar = rootView.findViewById(R.id.progress_bar);
        empty = rootView.findViewById(R.id.empty);
        spinner = rootView.findViewById(R.id.filters);
    }

    private void sortComments(int position) {
        if (position == 0) {
            Collections.sort(commentList, Comment.dateComparator);
        } else if (position == 1) {
            Collections.sort(commentList, Comment.rateComparator);
        } else {
            Collections.sort(fixedCommentList, Comment.dateComparator);
        }
        commentAdapter.addAllComments(commentList, true);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveComments();
    }

    private void retrieveComments() {
        if (Util.getUser().isExtra()) {
            commentList = Util.getUser().getCommentList();
            fixedCommentList = Util.getUser().getCommentList();
            if (commentList != null && commentList.size() > 0) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rvComments.setLayoutManager(layoutManager);
                commentAdapter = new RecyclerViewCommentAdapter(getContext(), profileActivity, true, false);
                rvComments.setAdapter(commentAdapter);
                commentAdapter.addAllComments(commentList, true);
            }
        }
        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Server> serverList = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Subject subject = snap.getValue(Subject.class);
                    if (subject != null && subject.getServers() != null) {
                        for (Server server : subject.getServers().values()) {
                            if (server != null && server.getTimeline() != null) {
                                serverList.add(server);
                            }
                        }
                    }
                }

                commentList = new ArrayList<>();
                fixedCommentList = new ArrayList<>();
                for (int i = 0; i < serverList.size(); i++) {
                    Server server = serverList.get(i);
                    int finalI = i;
                    Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(server.getServerUID()).child(Constants.DATABASE_REF_TIMELINE).child(Constants.DATABASE_REF_COMMENT_LIST).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Comment comment = snapshot1.getValue(Comment.class);
                                if (comment != null && comment.getAuthorsUID().equals(user.getUserUID())) {
                                    commentList.add(comment);
                                    fixedCommentList.add(comment);
                                    empty.setVisibility(View.GONE);
                                }
                            }
                            if (finalI == serverList.size() - 1) {
                                progressBar.setVisibility(View.GONE);
                                if (commentList != null && commentList.size() > 0) {
                                    if (Util.getUser().isExtra() && Util.getUser().getCommentList() != null) {
                                        if (commentList.size() > Util.getUser().getCommentList().size()) {
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                            rvComments.setLayoutManager(layoutManager);
                                            commentAdapter = new RecyclerViewCommentAdapter(getContext(), profileActivity, true, false);
                                            rvComments.setAdapter(commentAdapter);
                                            commentAdapter.addAllComments(commentList, true);
                                            empty.setVisibility(View.GONE);
                                        }
                                    } else {
                                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                        rvComments.setLayoutManager(layoutManager);
                                        commentAdapter = new RecyclerViewCommentAdapter(getContext(), profileActivity, true, false);
                                        rvComments.setAdapter(commentAdapter);
                                        commentAdapter.addAllComments(commentList, true);
                                        empty.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}