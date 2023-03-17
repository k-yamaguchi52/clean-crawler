package com.example.cleancrawler.crawler.configuration;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.queue.fetch_request}")
    public String fetchRequestQueue;

    @Value("${rabbitmq.queue.fetch_response}")
    public String fetchResponseQueue;

    @Value("${rabbitmq.queue.dead_letter}")
    public String deadLetterQueue;

    @Value("${rabbitmq.exchange.fetch_request}")
    public String fetchRequestExchange;

    @Value("${rabbitmq.exchange.fetch_response}")
    public String fetchResponseExchange;

    @Value("${rabbitmq.exchange.dead_letter}")
    public String deadLetterExchange;

    @Value("${rabbitmq.routing.fetch_request_key}")
    public String fetchRequestRoutingKey;

    @Value("${rabbitmq.routing.fetch_response_key}")
    public String fetchResponseRoutingKey;

    @Value("${rabbitmq.routing.dead_letter_key}")
    public String deadLetterRoutingKey;

    @Value("${url_scrape_pool_size}")
    public int urlScrapePoolSize;

    @Bean
    public Queue fetchRequestQueue() {
//        return new Queue(fetchRequestQueue);
        return QueueBuilder.durable(fetchRequestQueue).withArgument("x-dead-letter-exchange", deadLetterExchange)
                .withArgument("x-dead-letter-routing-key", deadLetterRoutingKey).build();
    }

    @Bean
    public Queue fetchResponseQueue() {
        return new Queue(fetchResponseQueue);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(deadLetterQueue);
    }

//    今は一つのドメインしかクローリングしないのでExchangeの意味はない
    @Bean
    public TopicExchange fetchRequestExchange() {
        return new TopicExchange(fetchRequestExchange);
    }

    @Bean
    public TopicExchange fetchResponseExchange() {
        return new TopicExchange(fetchResponseExchange);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(deadLetterExchange);
    }

    @Bean
    public Binding fetchRequestBinding() {
        return BindingBuilder
                .bind(fetchRequestQueue())
                .to(fetchRequestExchange())
                .with(fetchRequestRoutingKey);
    }

    @Bean
    public Binding fetchResponseBinding() {
        return BindingBuilder
                .bind(fetchResponseQueue())
                .to(fetchResponseExchange())
                .with(fetchResponseRoutingKey);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(deadLetterRoutingKey);
    }
}
