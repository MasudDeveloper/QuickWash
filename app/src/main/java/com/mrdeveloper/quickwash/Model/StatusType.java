package com.mrdeveloper.quickwash.Model;

public class StatusType {
    private String id;
    private String title;
    private int iconResId; // Drawable icon resource ID

    public StatusType(String id, String title, int iconResId) {
        this.id = id;
        this.title = title;
        this.iconResId = iconResId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }
}

