package com.relyon.whib.modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable {

    private String subjectUID;
    private String title;
    private String date;
    private Popularity popularity;
    private boolean on; //true - on / false - off

    public Subject(String subjectUID, String title, String date, Popularity popularity, boolean on) {
        this.subjectUID = subjectUID;
        this.title = title;
        this.date = date;
        this.popularity = popularity;
        this.on = on;
    }

    public Subject() {
    }

    protected Subject(Parcel in) {
        subjectUID = in.readString();
        title = in.readString();
        date = in.readString();
        on = in.readByte() != 0;
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    public String getSubjectUID() {
        return subjectUID;
    }

    public void setSubjectUID(String subjectUID) {
        this.subjectUID = subjectUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Popularity getPopularity() {
        return popularity;
    }

    public void setPopularity(Popularity popularity) {
        this.popularity = popularity;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subjectUID);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeByte((byte) (on ? 1 : 0));
    }
}
