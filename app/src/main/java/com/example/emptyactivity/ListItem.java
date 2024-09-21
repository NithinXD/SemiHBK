package com.example.emptyactivity;

public class ListItem {
    private int iconResource;
    private String mainText;
    private String subText;

    public ListItem(int iconResource, String mainText, String subText) {
        this.iconResource = iconResource;
        this.mainText = mainText;
        this.subText = subText;
    }

    public int getIconResource() {
        return iconResource;
    }

    public String getMainText() {
        return mainText;
    }

    public String getSubText() {
        return subText;
    }
}
