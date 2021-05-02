package com.BloomingSouls;

public class ClassNotification {
    String Title, Context, Date;

    public ClassNotification() {
    }

    public ClassNotification(String title, String context, String date) {
        Title = title;
        Context = context;
        Date = date;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContext() {
        return Context;
    }

    public void setContext(String context) {
        Context = context;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
