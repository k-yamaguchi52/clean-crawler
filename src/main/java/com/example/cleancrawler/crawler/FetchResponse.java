package com.example.cleancrawler.crawler;

import java.io.Serializable;
import java.util.Map;

public class FetchResponse implements Serializable {
    public String url;
    public int status;
    public Map<String, String> headers;
    public byte[] body;

    public FetchResponse(String url, int status, Map<String, String> headers, byte[] body) {
        this.url = url;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }
}
