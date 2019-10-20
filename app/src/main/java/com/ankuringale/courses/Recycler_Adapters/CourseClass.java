package com.ankuringale.courses.Recycler_Adapters;

public class CourseClass {
    private String coursename;
    private long numberOfVideos;

    public CourseClass(String coursename , long numberOfVideos){
        this.coursename = coursename;
        this.numberOfVideos = numberOfVideos;
    }

    public long getNumberOfVideos(){
        return numberOfVideos;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename){
        this.coursename = coursename;
    }

    public void SetNumberOfVideos(long numberOfVideos){
        this.numberOfVideos = numberOfVideos;
    }
}
