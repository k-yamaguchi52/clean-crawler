package com.example.cleancrawler.crawler;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class URLLoader {

    private final Set<String> fetchedLinks = new HashSet<>();

    synchronized public List<String> filterNewLinks(List<String> links) {
        return links.stream()
                .filter(link -> {
                    if (fetchedLinks.contains(hashUrl(link))) {
                        return false;
                    } else {
                        fetchedLinks.add(hashUrl(link));
                        return true;
                    }
                }).collect(Collectors.toList());
    }

    public static String hashUrl(String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(url.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void printFetchedLinks() {
        fetchedLinks.forEach(System.out::println);
    }

    public int countFetchedLinks() {
        return this.fetchedLinks.size();
    }
}
