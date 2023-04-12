package com.iesalandalus.proyecto.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iesalandalus.proyecto.controllers.EventsController;
import com.iesalandalus.proyecto.model.TweetFromKafka;
import com.iesalandalus.proyecto.model.Tweets;
import com.twitter.clientlib.model.Tweet;

@Configuration
@EnableKafka
public class KafkaListenerConfig {
	ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private Tweets tweets;

	@Autowired
	private EventsController eventController;

	@SuppressWarnings("deprecation")
	@KafkaListener(topics = "TWITTER_MS", groupId = "twitime")
	public void listen(String message) {
		objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

		System.out.println("Received Messasge: " + message);
		try {
			TweetFromKafka t = objectMapper.readValue(message, TweetFromKafka.class);

			tweets.tweets.add(t);
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		SseEmitter latestEm = eventController.getLatestEmitter();

		try {

			latestEm.send(message);
		} catch (IOException e) {
			latestEm.completeWithError(e);
		}

	}
}