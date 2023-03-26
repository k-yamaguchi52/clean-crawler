package com.example.cleancrawler.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class URLExtractor {

    @Value("${crawl_seed_url}")
    private String seedUrl;

    @Value("${str_code}")
    private String strCode = StandardCharsets.UTF_8.name();;

    private final Set<String> badLinks = new HashSet<>();

    public List<String> extractLink(FetchResponse fetchResponse) {
        try {
            if (fetchResponse.status != HttpStatus.OK.value()) {
                badLinks.add(fetchResponse.url);
                return Collections.emptyList();
            }
            Document doc = Jsoup.parse(new String(fetchResponse.body, strCode), seedUrl);
            return extractLinksFromDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
            badLinks.add(fetchResponse.url);
            return Collections.emptyList();
        }
    }

    public List<String> extractLinksFromDocument(Document doc) {
        return doc.select("a[href]")
                .stream()
                .map(this::getAbsLinkFromElement)
                .filter(this::isPartOfSeedUrl)
                .filter(this::isNotBadLink)
                .distinct()
                .collect(Collectors.toList());
    }

    private String getAbsLinkFromElement(Element element) {
        return element.absUrl("href");
    }

    private boolean isPartOfSeedUrl(String link) {
        return link.contains(seedUrl);
    }

    private boolean isNotBadLink(String link) {
        return !this.badLinks.contains(link);
    }
}
