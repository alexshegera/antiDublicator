package com.company;

import java.nio.file.Path;

public class MapOfFiles {
    private String path;
    private String md5Sum;
    private Boolean isProcessing = false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMd5Sum() {
        return md5Sum;
    }

    public void setMd5Sum(String md5Sum) {
        this.md5Sum = md5Sum;
    }

    public Boolean getProcessing() {
        return isProcessing;
    }

    public void setProcessing(Boolean processing) {
        isProcessing = processing;
    }
}
