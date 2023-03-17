package com.example.cleancrawler.crawler;

import com.example.cleancrawler.crawler.exception.CrawlerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class URLExtractor {

    @Value("${crawl_seed_url}")
    private String seedUrl;

    public List<String> extractLink(FetchResponse fetchResponse) {
        try {
            String strCode = StandardCharsets.UTF_8. name();
            Document doc = Jsoup.parse(Paths.get(fetchResponse.localFilePath).toFile(), strCode, seedUrl);
            return extractLinksFromDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
//            throw new CrawlerException(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<String> extractLinksFromDocument(Document doc) {
        return doc.getElementsByTag("a")
                .stream()
                .map(this::getAbsLinkFromElement)
                .filter(this::isPartOfSeedUrl)
                .distinct()
                .collect(Collectors.toList());
    }

    private String getAbsLinkFromElement(Element element) {
        return element.attr("abs:href");
    }

    private boolean isPartOfSeedUrl(String link) {
        return link.contains(seedUrl);
    }
}
