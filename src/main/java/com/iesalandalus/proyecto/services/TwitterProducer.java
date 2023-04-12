package com.iesalandalus.proyecto.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.iesalandalus.proyecto.model.Tema;
import com.iesalandalus.proyecto.model.Temas;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.JSON;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.AddOrDeleteRulesRequest;
import com.twitter.clientlib.model.AddOrDeleteRulesResponse;
import com.twitter.clientlib.model.AddRulesRequest;
import com.twitter.clientlib.model.DeleteRulesRequest;
import com.twitter.clientlib.model.DeleteRulesRequestDelete;
import com.twitter.clientlib.model.FilteredStreamingTweetResponse;
import com.twitter.clientlib.model.Rule;
import com.twitter.clientlib.model.RuleNoId;
import com.twitter.clientlib.model.Tweet;
import com.twitter.clientlib.model.User;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

@Service
@Scope("singleton")
public class TwitterProducer {

	@Autowired
	private Temas temas;

	private String bearer;

	private TwitterApi apiInstance;

	private KafkaProducer<String, String> producer;

	@Autowired
	public TwitterProducer(@Value("${twitter.bearer}") String bearer) {
		this.bearer=bearer;

		createKafkaProducer();
		apiInstance = new TwitterApi(new TwitterCredentialsBearer(bearer));

	}

	public void comenzar() throws ApiException {
		
		setupRules();

		Set<String> tweetFields = new HashSet<>(Arrays.asList("attachments",
				"author_id",
				"context_annotations",
				"conversation_id",
				"created_at",
				"edit_controls",
				"edit_history_tweet_ids",
				"entities",
				"geo",
				"id",
				"in_reply_to_user_id",
				"lang",
				"non_public_metrics",
				"organic_metrics",
				"possibly_sensitive",
				"promoted_metrics",
				"public_metrics",
				"referenced_tweets",
				"reply_settings",
				"source",
				"text",
				"withheld"));
		
		Set<String> expansions = new HashSet<>(Arrays.asList("attachments.media_keys",
				"attachments.poll_ids",
				"author_id",
				"edit_history_tweet_ids",
				"entities.mentions.username",
				"geo.place_id",
				"in_reply_to_user_id",
				"referenced_tweets.id",
				"referenced_tweets.id.author_id"));
		
		Set<String> mediaFields = new HashSet<>(Arrays.asList("alt_text",
				"duration_ms",
				"height",
				"media_key",
				"non_public_metrics",
				"organic_metrics",
				"preview_image_url",
				"promoted_metrics",
				"public_metrics",
				"type",
				"url",
				"variants",
				"width"));
		
		Set<String> pollFields = new HashSet<>(Arrays.asList("duration_minutes", "end_datetime", "id", "options", "voting_status"));
		
		Set<String> userFields = new HashSet<>(Arrays.asList("created_at",
				"description",
				"entities",
				"id",
				"location",
				"name",
				"pinned_tweet_id",
				"profile_image_url",
				"protected",
				"public_metrics",
				"url",
				"username",
				"verified",
				"withheld"));
		
		Set<String> placeFields = new HashSet<>(Arrays.asList("contained_within", "country", "country_code", "full_name", "geo", "id", "name", "place_type"));
		
		
		

        InputStream result = apiInstance
                .tweets()
                .searchStream()
                .tweetFields(tweetFields)
                .expansions(expansions)
                .mediaFields(mediaFields)
                .pollFields(pollFields)
                .userFields(userFields)
                .placeFields(placeFields)
                .execute();

        try {
            JSON json = new JSON();

            Type localVarReturnType = new TypeToken<FilteredStreamingTweetResponse>(){}.getType();
            BufferedReader reader = new BufferedReader(new InputStreamReader(result));
            String line = reader.readLine();

            while (line != null) {
                if(line.isEmpty()) {
                    line = reader.readLine();
                    continue;
                }
                FilteredStreamingTweetResponse jsonObject = JSON.getGson().fromJson(line, localVarReturnType);
                
                if(jsonObject!=null) {
                	//Tweet t = jsonObject.getData();
                	//System.out.println(jsonObject != null ? jsonObject.toJson() : "Null object");
                	//System.out.println(jsonObject != null ? jsonObject.getData().toJson() : "Null object");
                producer.send(new ProducerRecord<>("twitter", jsonObject.toJson()), new Callback() {

					@Override
					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if (exception != null) {
							System.out.println("Error: Error al enviar tweet a kafka: " + exception);
						}

					}

                });
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }

	}
	
	public void addRules() throws ApiException {
        AddOrDeleteRulesRequest addOrDeleteRulesRequest = new AddOrDeleteRulesRequest();
        AddRulesRequest addRuleRequest = new AddRulesRequest();

        List<Tema> keywords = temas.temas; //twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);

        for (Tema keyword : keywords) {
            RuleNoId newRule = new RuleNoId();
            newRule.value(keyword.getNombre());
            newRule.tag(keyword.getNombre());
            addRuleRequest.addAddItem(newRule);
        }

        addOrDeleteRulesRequest.setActualInstance(addRuleRequest);
        apiInstance.tweets().addOrDeleteRules(addOrDeleteRulesRequest).dryRun(false).execute();
    }
	
	public List<String> getRulesIds(List<Rule> rules) {
		List<String> rulesIds = new ArrayList<>();

		if(rules!=null) {
        for (Rule rule : rules) {
        	
            rulesIds.add(rule.getId());
            System.out.print(rule.toString());
        }
        return rulesIds;
		}
		else return null;
	}

	public  List<Rule> getRules() throws ApiException{
		List<Rule> rules = apiInstance.tweets().getRules().execute().getData();
        return  rules;
	}
	
	
	  private void setupRules() throws ApiException {
	        List<Rule> rules = getRules();
	        if (rules != null) {
	            deleteRules(rules);
	        }
	        addRules();
	    }
	  
	  private void deleteRules(List<Rule> rules) throws ApiException {
	        AddOrDeleteRulesRequest addOrDeleteRulesRequest = new AddOrDeleteRulesRequest();

	        List<String> ids = getRulesIds(rules);
	        if(ids!=null) {

	        DeleteRulesRequest deleteRulesRequest = new DeleteRulesRequest();
	        DeleteRulesRequestDelete deleteRules = new DeleteRulesRequestDelete();

	        deleteRules.ids(ids);
	        deleteRulesRequest.delete(deleteRules);

	        addOrDeleteRulesRequest.setActualInstance(deleteRulesRequest);
	        AddOrDeleteRulesResponse result = apiInstance.tweets().addOrDeleteRules(addOrDeleteRulesRequest).dryRun(false).execute();

	        } else {
	        	
	        }
	        
	    }

	public void createKafkaProducer() {

		String bootstrapServers = "broker:29092";

		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		producer = new KafkaProducer<>(properties);

	}
	
	public void addAndDelete() throws ApiException {

		deleteRules(getRules());
		addRules();

	}

}
