CREATE STREAM IF NOT EXISTS twitter_stream_ms (includes STRUCT <tweets ARRAY< STRUCT <created_at VARCHAR, text VARCHAR, referenced_tweets ARRAY< STRUCT <type VARCHAR>>>>, users ARRAY< STRUCT <name VARCHAR, username VARCHAR, profile_image_url VARCHAR, public_metrics STRUCT <followers_count BIGINT>>>>) WITH(KAFKA_TOPIC='twitter', VALUE_FORMAT='JSON');
CREATE STREAM twitter_ms AS SELECT REPLACE(REPLACE(includes->tweets[1]->created_at,'T',' ' ),'Z', '') AS fecha, CASE 
WHEN (includes->tweets[1]->referenced_tweets IS NULL) THEN includes->tweets[1]->text
WHEN (includes->tweets[1]->referenced_tweets IS NOT NULL AND includes->tweets[1]->referenced_tweets[1]->type = 'retweeted') THEN includes->tweets[2]->text
ELSE includes->tweets[1]->text
END AS texto , includes->users[1]->username AS usuario, includes->users[1]->name AS nombre, includes->users[1]->profile_image_url AS imagen, includes->users[1]->public_metrics->followers_count AS followers FROM twitter_stream_ms EMIT CHANGES;

