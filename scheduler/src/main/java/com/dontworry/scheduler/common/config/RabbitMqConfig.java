package com.dontworry.scheduler.common.config;

import org.springframework.amqp.support.converter.MessageConverter;import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String CRAWLER_EXCHANGE = "crawler.exchange";
    public static final String CRAWLER_RANKING_ROUTING_KEY = "crawler.ranking";
    public static final String CRAWLER_PLACE_ROUTING_KEY = "crawler.place";
    public static final String CRAWLER_KEYWORD_ROUTING_KEY = "crawler.keyword";

    @Bean
    public TopicExchange crawlerExchange() {
        return new TopicExchange(CRAWLER_EXCHANGE);
    }

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
