package com.relyon.whib.modelo;

public class Preferences {

    private boolean sound;
    private boolean notification;
    private boolean vibration;
    private boolean showPhoto;

    public Preferences() {
    }

    public Preferences(boolean sound, boolean notification, boolean vibration, boolean showPhoto) {
        this.sound = sound;
        this.notification = notification;
        this.vibration = vibration;
        this.showPhoto = showPhoto;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    public boolean isShowPhoto() {
        return showPhoto;
    }

    public void setShowPhoto(boolean showPhoto) {
        this.showPhoto = showPhoto;
    }
}