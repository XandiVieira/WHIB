package com.relyon.whib.modelo;

import java.util.List;

public class History {

    private Participation bestSubject;
    private Participation worstSubject;
    private Comment bestComment;
    private Comment worstComment;
    private List<Comment> commentList;
    private List<Argument> argumentList;

    public History(Participation bestSubject, Participation worstSubject, Comment bestComment, Comment worstComment, List<Comment> commentList, List<Argument> argumentList) {
        this.bestSubject = bestSubject;
        this.worstSubject = worstSubject;
        this.bestComment = bestComment;
        this.worstComment = worstComment;
        this.commentList = commentList;
        this.argumentList = argumentList;
    }

    public History() {
    }

    public Participation getBestSubject() {
        return bestSubject;
    }

    public void setBestSubject(Participation bestSubject) {
        this.bestSubject = bestSubject;
    }

    public Participation getWorstSubject() {
        return worstSubject;
    }

    public void setWorstSubject(Participation worstSubject) {
        this.worstSubject = worstSubject;
    }

    public Comment getBestComment() {
        return bestComment;
    }

    public void setBestComment(Comment bestComment) {
        this.bestComment = bestComment;
    }

    public Comment getWorstComment() {
        return worstComment;
    }

    public void setWorstComment(Comment worstComment) {
        this.worstComment = worstComment;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public List<Argument> getArgumentList() {
        return argumentList;
    }

    public void setArgumentList(List<Argument> argumentList) {
        this.argumentList = argumentList;
    }
}
