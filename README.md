# app

## Instructions for running this project

Clone this repository and execute the following commands in its root directory:

```bash
mvn clean package
docker build . -t ghcr.io/remla23-team5/app:0.0.1 --build-arg model_service_url=http://localhost:8080/predict
docker run -it --rm -p8000:8000 ghcr.io/remla23-team5/app:0.0.1
```
