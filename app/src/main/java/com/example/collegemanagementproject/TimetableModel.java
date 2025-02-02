package com.example.collegemanagementproject;
public class TimetableModel {
    public String id, department, year, imageUrl;

    public TimetableModel() {}

    public TimetableModel(String id, String department, String year, String imageUrl) {
        this.id = id;
        this.department = department;
        this.year = year;
        this.imageUrl = imageUrl;
    }
}
