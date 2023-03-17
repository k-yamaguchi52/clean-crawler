package com.example.cleancrawler.crawler;

import java.io.Serializable;

public class FetchResponse implements Serializable {
    public String localFilePath;
    public String url;

    public FetchResponse(String localFilePath, String url){
            this.localFilePath = localFilePath;
            this.url = url;
    }
}
