package com.workforce.app.ec.holder;

public class MenuItemHolder {
    private int imageResource;
    private String description;

    public MenuItemHolder(int imageResource, String description) {
        this.imageResource = imageResource;
        this.description = description;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
