package com.example.baiahiu1_remake.Function;

import java.util.Date;

public class FileItem {
    private String name;
    private String path;
    private boolean isDirectory;
    private Date dateCreated;
    private int itemCount;

    public FileItem(String name, String path, boolean isDirectory, Date dateCreated, int itemCount) {
        this.name = name;
        this.path = path;
        this.isDirectory = isDirectory;
        this.dateCreated = dateCreated;
        this.itemCount = itemCount;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public int getItemCount() {
        return itemCount;
    }
}