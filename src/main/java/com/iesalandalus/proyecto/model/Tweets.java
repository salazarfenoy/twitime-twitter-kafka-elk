package com.iesalandalus.proyecto.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service
@Scope("singleton")
public class Tweets {

	public TweetFromKafka tweet;
	public List<TweetFromKafka> tweets;

	public Tweets() {
		tweets = new ArrayList<>();
	}

	private List<TweetFromKafka> copia() {

		List<TweetFromKafka> copiaT = new ArrayList<TweetFromKafka>();
		for (TweetFromKafka t : tweets) {
			copiaT.add(new TweetFromKafka(t));
		}
		return copiaT;

	}

	public List<TweetFromKafka> getAll() {

		return copia();
	}

	public List<TweetFromKafka> getAllReverse() {
		List<TweetFromKafka> t = copia();
		Collections.reverse(t);
		return t;
	}

	public void delete() {
		tweets.clear();
	}

}
