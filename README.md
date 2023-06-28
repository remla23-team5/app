# app

This repository contains the code for a Spring Boot web application which allows users to share reviews of restaurants. The web application performs sentiment analysis on said reviews using a machine learning model which is exposed by the [model-service](https://github.com/remla23-team5/model-service) through a REST API. Moreover, the web application allows users to provide feedback about the sentiment analysis of their own reviews (marking the evaluation as either correct or incorrect). This feedback is used to evaluate the performance of the machine learning model. Finally, the web application uses a MySQL database to store the reviews.

## Using the web application

The web application from this repository cannot be executed on its own. As described above, it depends on the [model-service](https://github.com/remla23-team5/model-service) and on the MySQL database and, while the URL which is used to query the [model-service](https://github.com/remla23-team5/model-service) can be provided as an environment variable, when building the Docker image, the same cannot be done for the MySQL database. As a result, the web application can only be executed through the Kubernetes setup from the [operation](https://github.com/remla23-team5/operation) repository.

**NOTE**: The [operation](https://github.com/remla23-team5/operation) repository also contains a `docker-compose.yaml` file which can be used to run the web application and the [model-service](https://github.com/remla23-team5/model-service) without a MySQL database. However, in order to work without a MySQL database, the setup from the `docker-compose.yaml` file uses a slightly modified version of the web application (the Docker image [ghcr.io/remla23-team5/app:docker](https://github.com/remla23-team5/app/pkgs/container/app/104772001?tag=docker)) which uses a file-based H2 database, rather than a MySQL database.

## Building a local Docker image

This repository contains a Dockerfile which can be used to build a local Docker image. Said Docker image can then be used in a Kubernetes cluster which runs the entire stack (for additional information, refer to the [operation](https://github.com/remla23-team5/operation) repository). In order to build a local Docker image from this repository, run the following command:
```bash
docker build -t <image-name> --build-arg model_service_url=<model-service-url> .
```

In the above command, `<image-name>` is a placeholder for the name of the local Docker image, while `<model-service-url>` is a placeholder for the value of the environment variable `model_service_url`. The environment variable `<model_service_url>` should be set to the exact URL which can be used to access the machine learning model through the [model-service](https://github.com/remla23-team5/model-service). If no value is provided for the environment variable `model_service_url`, then it uses the default value `http://localhost:8080/predict`.

## Monitoring

The web application exposes six metrics to Prometheus through the `/metrics` endpoint:
- `num_reviews`: This counter metric depicts the number of received reviews.
- `num_feedbacks`: This counter metric depicts the number of received feedbacks.
- `model_accuracy`: This gauge metric depicts the accuracy of the model, based on the user feedback.
- `reviews_of_length`: This histogram metric depicts the number of reviews of different lengths. More concretely, the reviews are split into the following five groups based on their length and this metric depicts the number of reviews in each group:
- - Reviews that contain less than 10 characters;
- - Reviews that contain less than 30 characters;
- - Reviews that contain less than 50 characters;
- - Reviews that contain less than 100 characters;
- - Reviews that contain more than 100 characters;
- `num_evaluations_per_sentiment`: This summary metric distinguishes between 3 different 'sentiments' - positive, negative and unsuccessful (a review`s sentiment is marked as 'unsuccessful', if an error occurs during the sentiment evaluation process), and depicts the number of reviews of each sentiment, according to the machine learning model.
- `num_evaluations_per_type`: This summary metric counts the number of true-positive, true-negative, false-positive and false-negative sentiment evaluations, based on the received user feedback.
