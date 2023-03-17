package com.example.cleancrawler.crawler;

import com.example.cleancrawler.crawler.exception.CrawlerException;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

public class FetchRequest implements Serializable {
    public String url;
    public String domain;

    public FetchRequest(String url) {
        try {
            this.url = url;
            this.domain = new URI(url).getAuthority();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new CrawlerException(e.getMessage());
        }
    }
}
