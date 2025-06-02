package com.example.notealarm;

import java.io.Serializable;


public class ListItem implements Serializable {
    String time;
    String notification;
    int pendingIntentTime;
    Long targetTime;

    public ListItem(String time, String notification, int pendingIntentTime, Long targetTime) {
        this.time = time;
        this.notification = notification;
        this.pendingIntentTime = pendingIntentTime;
        this.targetTime = targetTime;
    }
}
