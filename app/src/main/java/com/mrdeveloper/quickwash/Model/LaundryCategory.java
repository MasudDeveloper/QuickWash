package com.mrdeveloper.quickwash.Model;

public class LaundryCategory {
    private String title;
    private String description;
    private int iconResId; // Drawable icon resource ID

    public LaundryCategory(String title, String description, int iconResId) {
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }

}

