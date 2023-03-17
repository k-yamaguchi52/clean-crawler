package com.example.cleancrawler.crawler;

import com.example.cleancrawler.crawler.exception.CrawlerException;
import com.example.cleancrawler.crawler.util.FileUtil;
import org.jsoup.Jsoup;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Fetcher {

    @Value("${html_save_dir}")
    private String HTML_SAVED_DIR;

    @Value("${rabbitmq.queue.fetch_response}")
    private String FETCH_RESPONSE_QUEUE;

    private final RabbitTemplate rabbitTemplate;

    public Fetcher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${rabbitmq.queue.fetch_request}")
    public void fetchHtmlAndSendResponseQueue(FetchRequest request) {
        FetchResponse response = fetch(request.url);
        sendFetchResponseQueues(response);
    }

    private FetchResponse fetch(String url) {
        String html = fetchHtmlFromUrl(url);
        Path path = createPath(url);
        saveHtmlFile(path, html);
        return new FetchResponse(path.toString(), url);
    }

    private void sendFetchResponseQueues(FetchResponse fetchResponse) {
        rabbitTemplate.convertAndSend(FETCH_RESPONSE_QUEUE, fetchResponse);
    }

    private String fetchHtmlFromUrl(String url) {
        try {
            return Jsoup.connect(url).get().outerHtml();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CrawlerException(e.getMessage());
        }
    }

    private Path createPath(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return Paths.get(HTML_SAVED_DIR + url.getPath() + "/index.html");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new CrawlerException(e.getMessage());
        }
    }

    private void saveHtmlFile(Path path, String html) {
        try {
            FileUtil.createRecursiveDirs(path.getParent().toString());
            Files.writeString(path, html);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CrawlerException(e.getMessage());
        }
    }
}
