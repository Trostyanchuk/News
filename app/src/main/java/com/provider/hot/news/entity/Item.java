package com.provider.hot.news.entity;


public class Item {

    private String title;
    private String link;
    private String date;
    private String description;
    private String imageUrl;
    private byte[] image;

    //Empty item for parser
    public Item() {
        this(null, null, null, null, null, null);
    }

    public Item(String title, String link, String date, String description, String imageUrl, byte[] image) {
        this.title = title;
        this.link = link;
        this.date = date;
        this.description = description;
        this.imageUrl = imageUrl;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
