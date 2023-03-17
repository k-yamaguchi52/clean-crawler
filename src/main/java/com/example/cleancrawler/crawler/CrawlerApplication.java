package com.example.cleancrawler.crawler;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties
public class CrawlerApplication implements CommandLineRunner {

    @Value("${crawl_seed_url}")
    private String seedUrl;

    @Value("${rabbitmq.queue.fetch_request}")
    private String FETCH_REQUEST_QUEUE;

    private final RabbitTemplate rabbitTemplate;

    public CrawlerApplication(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(String...args) {
        System.out.println("Sending message...");
        rabbitTemplate.convertAndSend(FETCH_REQUEST_QUEUE, new FetchRequest(seedUrl));
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CrawlerApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        SpringApplication.run(CrawlerApplication.class, args);
    }
}
