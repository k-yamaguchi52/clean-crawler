package com.example.cleancrawler.crawler;

import org.springframework.stereotype.Component;

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
                    if (fetchedLinks.contains(link)) {
                        return false;
                    } else {
                        fetchedLinks.add(link);
                        return true;
                    }
                }).collect(Collectors.toList());
    }

    public void printFetchedLinks() {
        fetchedLinks.forEach(System.out::println);
    }

    public int countFetchedLinks() {
        return this.fetchedLinks.size();
    }
}
