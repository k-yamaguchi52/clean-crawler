package com.example.cleancrawler.crawler;

import com.example.cleancrawler.crawler.exception.CrawlerException;
import com.example.cleancrawler.crawler.util.FileUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Value("${file_save_key}")
    private String fileSaveKey;

    private final RabbitTemplate rabbitTemplate;

    public Fetcher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${rabbitmq.queue.fetch_request}", concurrency = "1")
    @Scheduled(fixedDelay = 1000)
    public void fetchHtmlAndSendResponseQueue(FetchRequest request) {
        FetchResponse response = fetch(request.url);
        if (shouldSaveHtmlFile(request.url)) {
            saveHtmlFile(request.url, response.body);
        }
        sendFetchResponseQueues(response);
    }

    private FetchResponse fetch(String url) {
        System.out.println(url);
        return fetchStaticPage(url);
    }

    private void sendFetchResponseQueues(FetchResponse fetchResponse) {
        rabbitTemplate.convertAndSend(FETCH_RESPONSE_QUEUE, fetchResponse);
    }

    private FetchResponse fetchStaticPage(String url) {
        try {
            Connection.Response response;
            response = Jsoup.connect(url).execute();
            return new FetchResponse(url, response.statusCode(), response.headers(), response.bodyAsBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CrawlerException(e.getMessage());
        }
    }

    private boolean shouldSaveHtmlFile(String url) {
        if (fileSaveKey.isEmpty()) {
            return true;
        }
        return url.contains(fileSaveKey);
    }

    private void saveHtmlFile(String url, byte[] html) {
        try {
            Path path = createPath(url);
            FileUtil.createRecursiveDirs(path.getParent().toString());
            Files.write(path, html);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CrawlerException(e.getMessage());
        }
    }

    private Path createPath(String urlStr) {
        try {
            URL url = new URL(urlStr);
            String fileName = url.getQuery() == null ? "index" : url.getQuery();
            return Paths.get(HTML_SAVED_DIR + url.getPath() + "/" + fileName + ".html");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new CrawlerException(e.getMessage());
        }
    }
}
