package com.ankuringale.courses.Recycler_Adapters;

public class VideoClass {
    private String title , id;
    private long likes, dislikes;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getDislikes() {
        return dislikes;
    }

    public void setDislikes(long dislikes) {
        this.dislikes = dislikes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VideoClass(String title, String id, long likes, long dislikes) {
        this.title = title;
        this.likes = likes;
        this.dislikes = dislikes;
        this.id = id;

    }
}
