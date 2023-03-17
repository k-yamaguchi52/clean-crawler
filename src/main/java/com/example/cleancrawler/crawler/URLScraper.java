package com.example.cleancrawler.crawler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class URLScraper {

    @Value("${rabbitmq.queue.fetch_request}")
    private String fetchRequestQueue;

    private final RabbitTemplate rabbitTemplate;

    private final URLExtractor urlExtractor;

    private final URLLoader urlLoader;

    public URLScraper(RabbitTemplate rabbitTemplate, URLExtractor urlExtractor, URLLoader urlLoader) {
        this.rabbitTemplate = rabbitTemplate;
        this.urlExtractor = urlExtractor;
        this.urlLoader = urlLoader;
    }

    @RabbitListener(queues = "${rabbitmq.queue.fetch_response}")
    public void scrapeNewUrlsAndSendFetchRequestQueue(FetchResponse fetchResponse) {
        List<String> extractedLinks = urlExtractor.extractLink(fetchResponse);
        List<String> newLinks = urlLoader.filterNewLinks(extractedLinks);
        sendFetchRequestQueues(newLinks);
    }

    private void sendFetchRequestQueues(List<String> links) {
        links.forEach(link -> {
                    rabbitTemplate.convertAndSend(fetchRequestQueue, new FetchRequest(link));
                }
        );
    }

}
