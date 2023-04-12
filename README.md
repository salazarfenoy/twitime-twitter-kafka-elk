# Spring Boot, Kafka, KSQL and Elastic Search Tweet Collector

This project collects tweets using the Twitter API, processes them with Kafka and KSQL, and stores them in Elastic Search. The project uses Spring Boot as producer/consumer.

## Getting Started

### Prerequisites

- Java 8 or later
- Docker
- Docker Compose

### Installation

1. Clone the repository:

```sh
  git clone https://github.com/salazarfenoy/twitime-twitter-kafka-elk.git
```

2. Create a Twitter Developer account and obtain a bearer token.

3. Copy the bearer token in the .env file in the root directory of the project:

```
BEARER=<your-bearer-token>
```

4. Build the Docker image using Jib:

```sh
./mvnw compile jib:dockerBuild
```

5. Start the containers:

```sh
docker-compose up -d
```
6. Verify that the containers are running:

```sh
docker-compose ps
```

### Usage

 Connect to http://localhost:5111 in your web browser to access the user interface. From there, you can enter keywords to search for on Twitter and start collecting tweets.
