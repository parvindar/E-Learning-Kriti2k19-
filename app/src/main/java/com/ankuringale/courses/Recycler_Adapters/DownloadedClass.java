package com.ankuringale.courses.Recycler_Adapters;

import java.io.File;
import java.io.Serializable;

public class DownloadedClass implements Serializable {
    private String fileUri;

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    private String title,desc,inst;
    private long likes, dislikes;

    public DownloadedClass(String fileUri, String title, String desc, String inst, long likes, long dislikes) {
        this.fileUri = fileUri;
        this.title = title;
        this.desc = desc;
        this.inst = inst;
        this.likes = likes;
        this.dislikes = dislikes;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInst() {
        return inst;
    }

    public void setInst(String inst) {
        this.inst = inst;
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
}
